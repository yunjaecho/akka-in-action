package com.goticks

import akka.actor.{Actor, PoisonPill, Props}

object TicketSeller {
  def props(event: String) = Props(new TicketSeller(event))

  case class Add(tickets: Vector[Ticket])
  case class Buy(tickets: Int)
  case class Ticket(id: Int)
  case class Tickets(event: String,
                     entries: Vector[Ticket] = Vector.empty[Ticket])
  case object GetEvent
  case object Cancel

}


class TicketSeller(event: String) extends Actor {
  import TicketSeller._

  var tickets = Vector.empty[Ticket] // 티켓 목록

  def receive = {
    case Add(newTickets) => tickets = tickets ++ newTickets // 기존의 티켓 목록에 새로은 티켓을 추가한다.
    case Buy(nrOfTickets) =>    // 티켓이 충분이 남아 있을 경우만 목록에서 요청받은 수만큼 티켓을 빼내고, 해당 티켓들의 메세지에 담아 응답한다.
      val entries = tickets.take(nrOfTickets)
      if(entries.size >= nrOfTickets) {
        sender() ! Tickets(event, entries)
        tickets = tickets.drop(nrOfTickets)
      } else sender() ! Tickets(event)
    case GetEvent => sender() ! Some(BoxOffice.Event(event, tickets.size))  // GetEvent를 받으면 남은 티켓 수를 포함하는 이벤트를 반환한다.
    case Cancel =>
      sender() ! Some(BoxOffice.Event(event, tickets.size))
      self ! PoisonPill
  }
}
