package gamez

class Entity private (
  private val id: Int, 
  private val aspects: Map[AspectKey, Aspect]) {

  override def toString = 
    s"""Entity(${id}, ${aspects.values.map(_.toString).mkString(", ")})"""
}

object Entity {
  // Could run in a state - just POC
  private var currentId: Int = 0

  private def nextId: Int = synchronized {
    val i = currentId
    currentId += 1
    i
  }

  def apply(aspects: Map[AspectKey, Aspect]): Entity = {
    val id = nextId
    new Entity(id, aspects)
  }

  import scalaz.{Optional => _, Lens => _, _}, Scalaz._
  import monocle._, Monocle._

  private val _aspects = Lens[Entity, Map[AspectKey, Aspect]] { entity =>
    entity.aspects
  } { aspects => entity =>
    new Entity(entity.id, aspects)
  }

  private def getAspect[A](entity: Entity, prism: Prism[Aspect, A])(implicit ev: HasAspectKey[A]) = {
    val aspects = (entity applyLens _aspects).get

    aspects.get(ev.key).toMaybe.flatMap { aspect =>
      prism.getMaybe(aspect)
    }
  }

  private def modifyAspect[A <: Aspect](entity: Entity, aspect: A)(implicit ev: HasAspectKey[A]) = {
    (entity applyLens _aspects).modify { aspects =>
      aspects.updated(ev.key, aspect)
    }
  }

  // private lenses

  private val _position = Optional[Entity, Position] { entity =>
    // the typeclass drives the key selection. 
    getAspect[Position](entity, Aspect.position)
  } { position => entity =>
    modifyAspect(entity, position)
  }

  private val _health = Optional[Entity, Health] { entity =>
    getAspect[Health](entity, Aspect.health)
  } { health => entity =>
    modifyAspect(entity, health)
  }

  private val _movable = Optional[Entity, Movable.type] { entity =>
    getAspect[Movable.type](entity, Aspect.movable)
  } { movable => entity =>
    modifyAspect(entity, movable)
  }

  // public lenses

  val id = Getter[Entity, Int] { entity =>
    entity.id
  }

  // The setter operation is a no-op. Use movable
  val position = Optional[Entity, Position] { entity =>
    (_position getMaybe entity)
  } { position => entity =>
    entity
  }

  val health = _health

  val movable = Optional[Entity, Position] { entity =>
    for {
      m <- (_movable getMaybe entity)
      p <- (_position getMaybe entity)
    } yield p
  } { position => entity =>
    _position.set(position)(entity)
  }

  val movableAndHealth = Optional[Entity, (Position, Health)] { entity =>
    for {
      m <- (movable getMaybe entity)
      h <- (health getMaybe entity)
    } yield (m, h)
  } { movableAndHealth => entity =>
    val (p, h) = movableAndHealth
    (movable.set(p) andThen health.set(h))(entity)
  }
}
