package object gamez {
  implicit class HasKeyOps[A](a: A)(implicit ev: HasAspectKey[A]) {
    def key = ev.key
  }
}
