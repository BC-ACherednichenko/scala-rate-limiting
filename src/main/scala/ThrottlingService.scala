import scala.concurrent.{Await, ExecutionContext, Future, TimeoutException}
import ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Main {

  def main(args: Array[String]) : Unit = {
    val slaService = SlaService
    val throttlingService = new Throttling(10, slaService)
    throttlingService.isRequestAllowed(Some("6N2peswmp7IEFwiXxFWk"))
    throttlingService.isRequestAllowed(Some("6N2peswmp7IEFwiXxFWk"))
  }
}

trait ThrottlingService {
  val graceRps:Int // configurable move to yml
  val slaService: SlaService // use mocks/stubs for testing
  // Should return true if the request is within allowed RPS.
  def isRequestAllowed(token: Option[String]): Future[Boolean]
}

class Throttling(val graceRps: Int, val slaService: SlaService) extends ThrottlingService {

  val rpsCounter: RpsCounter = RpsCounter()

  override def isRequestAllowed(token: Option[String]): Future[Boolean] = {
    val result = for {
      sla <- slaService.getSlaByToken(token.getOrElse(""))
      isValid = rpsCounter.hasReachedThreshold(sla) // todo it also could be a future
    } yield isValid
    result
  }
}



