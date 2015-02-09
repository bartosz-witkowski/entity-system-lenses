package gamez

import scalaz.{Optional => _, _}, Scalaz._
import monocle._

class Entities(private val entites: Map[Int, Entity]) {
  private lazy val entityList = entites.values.toList

  override def toString = entityList.mkString("Entities(", ", ", ")")
}

object Entities {
  def apply(x: Entity, xs: Entity*): Entities = {
    val entites = (x +: xs).map { x =>
      Entity.id.get(x) -> x
    }.toMap

    new Entities(entites)
  }

  def first[T](list: List[Entity], opt: Optional[Entity, T]): Maybe[T] = {
    def go(list: List[Entity]): Maybe[T] = {
      list match {
        case x :: xs => 
          opt getMaybe x match {
            case a @ scalaz.Maybe.Just(_) => a
            case scalaz.Maybe.Empty()     => go(xs)
          }

        case Nil => 
          Maybe.empty
      }
    }

    go(list)
  }

  def firstWith[T](opt: Optional[Entity, T]) = {
    def go(list: List[Entity]): Maybe[Entity] = list match {
      case x :: xs => 
        opt getMaybe x match {
          case scalaz.Maybe.Just(_) => Maybe.just(x)
          case scalaz.Maybe.Empty() => go(xs)
        }

      case Nil => 
        Maybe.empty
    }

    Optional[Entities, T] { entites =>
      go(entites.entityList).flatMap { entity =>
        opt getMaybe entity
      }
    } { t => entites =>
      go(entites.entityList).cata({ old =>
        val oldEntities = entites.entites
        val newEntities = oldEntities.updated(Entity.id.get(old), opt.set(t)(old))
        new Entities(newEntities)
      }, {
        entites
      })
    }
  }
}
