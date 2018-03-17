package com.goticks

import com.github.nscala_time.time.Imports._

import scala.concurrent.Future
import scala.util.control.NonFatal


trait TicketInfoService extends WebServiceCalls {
  import scala.concurrent.ExecutionContext.Implicits.global

  type Recovery[T] = PartialFunction[Throwable,T]

  // None으로 복구하기
  def withNone[T]: Recovery[Option[T]] = { case NonFatal(e) => None }

  def withPrevious(previous: TicketInfo): Recovery[TicketInfo] = {
    case NonFatal(e) => previous
  }

  def getTicketInfo(ticketNr: String, location: Location): Future[TicketInfo] = {
    val emptyTicketInfo = TicketInfo(ticketNr, location)
    val eventInfo = getEvent(ticketNr, location).recover(withPrevious(emptyTicketInfo))

    eventInfo.flatMap {info =>
      val infoWithWeather = getWeather(info)

      val infoWithTravelAdvice = info.event.map { event =>
        getTravelAdvice(info, event)
      }.getOrElse(eventInfo)

      val suggestedEvents = info.event.map { event =>
        getSuggestions(event)
      }.getOrElse(Future.successful(Seq()))

      val ticketInfos = List(infoWithTravelAdvice, infoWithWeather)

      val infoWithTravelAndWeather: Future[TicketInfo] =
        Future.foldLeft(ticketInfos)(info) { (acc, elem) =>
          val (travelAdvice, weather) = (elem.travelAdvice, elem.weather)
          acc.copy(travelAdvice = travelAdvice.orElse(acc.travelAdvice), weather= weather.orElse(acc.weather))
        }

      for (info <- infoWithTravelAndWeather; suggestions <- suggestedEvents)
        yield info.copy(suggestions= suggestions)
    }


  } 

  def getWeather(tickeInfo: TicketInfo): Future[TicketInfo] = {
    val futureWeatherX: Future[Option[Weather]] =
      callWeatherXService(tickeInfo).recover(withNone)

    val futureWeatherY: Future[Option[Weather]] =
      callWeatherYService(tickeInfo).recover(withNone)

    val futures: List[Future[Option[Weather]]] =
      List(futureWeatherX, futureWeatherY)

    val fastestSuccessfulResponse: Future[Option[Weather]] =
      Future.find(futures)(maybeWeather => !maybeWeather.isEmpty)
          .map(_.flatten)

    fastestSuccessfulResponse.map {weatherResponse =>
      tickeInfo.copy(weather = weatherResponse)
    }
  }

  def getTravelAdvice(info: TicketInfo, event: Event): Future[TicketInfo] = {
    val futureR: Future[Option[RouteByCar]] = callTrafficService(
      info.userLocation,
      event.location,
      event.time
    ).recover(withNone)

    val futureP: Future[Option[PublicTransportAdvice]] = callPublicTransportService(
      info.userLocation,
      event.location,
      event.time
    )

    futureR.zip(futureP).map {
      case(routeByCar, publicTransportAdvice) =>
        val travelAdvice = TravelAdvice(routeByCar, publicTransportAdvice)
        info.copy(travelAdvice = Some(travelAdvice))
    }
  }

  /**
    * 비슷한 예술가들의 이벤트정보가 들어있는 Future[Seq[Event]] 반환 한다.
    * @param event
    * @return
    */
  def getSuggestions(event: Event): Future[Seq[Event]] = {
    //비슷한 예술가 정보가 들어있는 퓨처인 Future[Seq[Artist]] 반환 한  다.
    val futureArtists: Future[Seq[Artist]] = callSimilarArtistsService(event)

    for {
      // artists는 언제가 Seq[Artist]가 된다.
      artists <- futureArtists
      // events는 미래에 요청한, 열린 계획이 있는 모든 예술가들의 이벤트 정보가 들어있는 Seq[Event]가 된다.
      events <- getPlannedEvents(event, artists)
    } yield events
  }


  /**
    * 비슷한 예술가 한 사람당 계획된 이벤트가 하나씩 들어있는 Future[Seq[Event]]를 돌려준다.
    * @param event
    * @param artists
    * @return
    */
  def getPlannedEvents(event: Event, artists: Seq[Artist]): Future[Seq[Event]] = {

    val events: Seq[Future[Event]] = artists.map { artist =>
      callArtistCalendarService(artist, event.location)
    }
    Future.sequence(events)
  }

  def getPlannedEventsWithTravers(event: Event, artists: Seq[Artist]): Future[Seq[Event]] = {
    // traverse는 Future를 반환하는 코드 블록을 받는다.
    // 그것은 콜렉션을 순회하는 동시에 결과가 담긴 퓨처를 만들어 낸다
    Future.traverse(artists) { artist =>
      callArtistCalendarService(artist, event.location)

    }


    val events: Seq[Future[Event]] = artists.map { artist =>
      callArtistCalendarService(artist, event.location)
    }
    Future.sequence(events)
  }



}

trait WebServiceCalls {
  def getEvent(ticketNr: String, location: Location): Future[TicketInfo]

  def callWeatherXService(ticketInfo: TicketInfo): Future[Option[Weather]]

  def callWeatherYService(ticketInfo: TicketInfo): Future[Option[Weather]]

  def callTrafficService(origin: Location, destination: Location, time: DateTime): Future[Option[RouteByCar]]

  def callPublicTransportService(origin: Location, destination: Location, time: DateTime): Future[Option[PublicTransportAdvice]]

  def callSimilarArtistsService(event: Event): Future[Seq[Artist]]

  def callArtistCalendarService(artist: Artist, nearLocation: Location): Future[Event]
}
