package gamez

import scalaz.{Optional => _, _}, Scalaz._
import monocle._

object Gamez extends App {
  def Rock(position: Position): Entity =
    Entity(Map(position.key -> position))

  def Ship(position: Position, health: Health): Entity =
    Entity(Map(position.key -> position, health.key -> health, Movable.key -> Movable))

  def Building(position: Position, health: Health) =
    Entity(Map(position.key -> position, health.key -> health))

  val entities = Entities(
    Rock(Position(1, 1)),
    Ship(Position(2, 1), Health(10)),
    Building(Position(10, 5), Health(100)))

  /*
   * There's probably a better way to compose these but I'm not versed with
   * optics...
   */
  def movableAt(at: Position) = Optional[Entity, Position] { entity =>
    Entity.movable.getMaybe(entity).flatMap { position =>
      if (position == at) Maybe.just(position)
      else                Maybe.empty
    }
  } { position => entity =>
    Entity.position.set(position)(entity)
  }

  def movableAndHealthAt(at: Position) = Optional[Entity, (Position, Health)] { entity =>
    Entity.movableAndHealth.getMaybe(entity).flatMap { tuple =>
      val (position, health) = tuple
      if (position == at) Maybe.just(tuple)
      else                Maybe.empty
    }
  } { tuple => entity =>
    Entity.movableAndHealth.set(tuple)(entity)
  }

  println("Before: " + entities)
  println("Getting: " + Entities.firstWith(movableAt(Position(2, 1))).getMaybe(entities))
  println("Update: " + 
    Entities.firstWith(movableAt(Position(2, 1))).set(Position(1, 1))(entities))

  println("Health: " + 
    Entities.firstWith(movableAndHealthAt(Position(2, 1))).
      set((Position(1, 1), Health(5)))(entities))
  
}
