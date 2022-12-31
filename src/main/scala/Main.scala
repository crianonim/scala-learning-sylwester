import cats.Monad
import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.HttpRoutes
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.dsl._
import org.http4s.implicits._

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    def helloRoute[F[_] : Monad]: HttpRoutes[F] = {
      val dsl = Http4sDsl[F]
      import dsl._
      HttpRoutes.of[F] {
        case GET -> Root => (Ok("Hello!"))
      }
    }

    BlazeServerBuilder[IO](runtime.compute)
      .bindHttp(8080, "localhost")
      .withHttpApp(helloRoute[IO].orNotFound)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}