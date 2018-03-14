
/**
  * Created by USER on 2018-03-12.
  */
object Test extends App {
  def addUmm(x: String) = x + " num"

  def addAhem(x: String) = x + " ahem"

  // function compose
  // addAhem(addUmm(x))
  val numThenAhem = addAhem _ compose addUmm
  println(numThenAhem("well"))

  // function andThen
  // addUmm(addAhem(x))
  val ahemThenUmm = addAhem _ andThen addUmm _
  println(ahemThenUmm("well"))

  val one: PartialFunction[Int, String] = { case 1 => "one" }
  val two: PartialFunction[Int, String] = { case 2 => "two" }
  val three: PartialFunction[Int, String] = { case 3 => "three" }
  val wildcard: PartialFunction[Int, String] = { case _ => "something else" }

  val partial = one orElse two orElse three orElse wildcard
  println(partial(5))
  println(partial(3))
  println(partial(2))
  println(partial(1))
}
