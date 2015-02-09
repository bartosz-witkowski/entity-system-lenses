package gamez

sealed abstract class AspectKey
case object PositionKey extends AspectKey
case object HealthKey extends AspectKey
case object MovableKey extends AspectKey

sealed trait Aspect
object Aspect {
  import scalaz.{Optional => _, Lens => _, _}, Scalaz._
  import monocle._, Monocle._

  val position = Prism[Aspect, Position] { aspect =>
    aspect match {
      case x @ Position(_, _) => Maybe.just(x)
      case other              => Maybe.empty
    }
  } { x => x }

  val movable = Prism[Aspect, Movable.type] { aspect =>
    aspect match {
      case Movable => Maybe.just(Movable)
      case other   => Maybe.empty
    }
  } { x => x }

  val health = Prism[Aspect, Health] { aspect =>
    aspect match {
      case x @ Health(_) => Maybe.just(x)
      case other         => Maybe.empty
    }
  } { x => x }
}

case class Position(x: Int, y: Int) extends Aspect
object Position {
  implicit val _ = HasAspectKey[Position] { PositionKey }
}

case class Health(hp: Int) extends Aspect
object Health {
  implicit val _ = HasAspectKey[Health] { HealthKey }
}

case object Movable extends Aspect {
  implicit val _ = HasAspectKey[Movable.type] { MovableKey }
}

trait HasAspectKey[A] {
  def key: AspectKey
}
object HasAspectKey {
  def apply[A](k: AspectKey): HasAspectKey[A] = new HasAspectKey[A] {
    override def key: AspectKey = k
  }
}
