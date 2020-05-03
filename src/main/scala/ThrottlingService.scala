import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

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
  def isRequestAllowed(token:Option[String]): Boolean
}

class Throttling(val graceRps: Int, val slaService: SlaService) extends ThrottlingService {

  val rpsCounter: RpsCounter = RpsCounter()

  def isRequestAllowed(token:Option[String]): Boolean = {
    val result = for {
      sla <- slaService.getSlaByToken(token.getOrElse(""))
      isValid = rpsCounter.hasReachedThreshold(sla) // it also could be a future
    } yield isValid
    true
  }
}



