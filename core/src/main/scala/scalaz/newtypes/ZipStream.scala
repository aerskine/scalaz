package scalaz
package newtypes

sealed trait ZipStream[A] {
  val value: Stream[A]
}

object ZipStream extends ZipStreams

trait ZipStreams {

  import wrap.StreamW._

  implicit def ZipStream_^*^[A]: ^*^[ZipStream[A], Stream[A]] =
    ^*^.^*^(_.value, b => new ZipStream[A] {
      val value = b
    })

  implicit def ZipStream_^**^ : ^**^[ZipStream, Stream] =
    new ^**^[ZipStream, Stream] {
      def unpack[A] = _.value

      def pack[A] = b => new ZipStream[A] {
        val value = b
      }
    }

  implicit def ZipStreamZero[A]: Zero[ZipStream[A]] =
    implicitly[Zero[Stream[A]]].deriving[ZipStream[A]]

  implicit def ZipStreamSemigroup[A]: Semigroup[ZipStream[A]] =
    implicitly[Semigroup[Stream[A]]].deriving[ZipStream[A]]

  implicit def ZipStreamMonoid[A]: Monoid[ZipStream[A]] =
    implicitly[Monoid[Stream[A]]].deriving[ZipStream[A]]

  implicit def ZipStreamShow[A: Show]: Show[ZipStream[A]] =
    implicitly[Show[Stream[A]]] contramap ((_: ZipStream[A]).value)

  implicit def ZipStreamEqual[A: Equal]: Equal[ZipStream[A]] =
    implicitly[Equal[Stream[A]]] contramap ((_: ZipStream[A]).value)

  implicit def ZipStreamOrder[A: Order]: Order[ZipStream[A]] =
    implicitly[Order[Stream[A]]] contramap ((_: ZipStream[A]).value)

  implicit val ZipStreamFunctor: Functor[ZipStream] =
    implicitly[Functor[Stream]].deriving[ZipStream]

  implicit val ZipStreamPointed: Pointed[ZipStream] =
    new Pointed[ZipStream] {
      def point[A](a: => A) =
        Stream.continually(a) ʐ
    }

  implicit def ZipStreamPointedFunctor: PointedFunctor[ZipStream] =
    PointedFunctor.pointedFunctor

  implicit val ZipStreamApplic: Applic[ZipStream] = new Applic[ZipStream] {
    def applic[A, B](f: ZipStream[A => B]) =
      a => {
        val ff = f.value
        val aa = a.value
        (if (ff.isEmpty || aa.isEmpty) Stream.empty
        else Stream.cons((ff.head)(aa.head), applic(ff.tail ʐ)(aa.tail ʐ).value)) ʐ
      }
  }

  implicit def ZipStreamApplicative: Applicative[ZipStream] =
    Applicative.applicative

  implicit def ZipStreamApplicFunctor: ApplicFunctor[ZipStream] =
    ApplicFunctor.applicFunctor

  implicit def ZipStreamEach: Each[ZipStream] =
    implicitly[Each[Stream]].deriving[ZipStream]

  implicit def ZipStreamFoldr: Foldr[ZipStream] =
    implicitly[Foldr[Stream]].deriving[ZipStream]

  implicit def ZipStreamFoldl: Foldl[ZipStream] =
    implicitly[Foldl[Stream]].deriving[ZipStream]

  implicit def ZipStreamFoldable: Foldable[ZipStream] =
    implicitly[Foldable[Stream]].deriving[ZipStream]

  implicit def ZipStreamIndex: Index[ZipStream] =
    implicitly[Index[Stream]].deriving[ZipStream]

  implicit def ZipStreamLength: Length[ZipStream] =
    implicitly[Length[Stream]].deriving[ZipStream]

  implicit def ZipStreamTraverse: Traverse[ZipStream] = new Traverse[ZipStream] {
    def traverse[F[_] : Applicative, A, B](f: A => F[B]) =
      za => implicitly[Applicative[F]].fmap((_: Stream[B]) ʐ)(implicitly[Traverse[Stream]].traverse[F, A, B](f) apply (za.value))
  }

}