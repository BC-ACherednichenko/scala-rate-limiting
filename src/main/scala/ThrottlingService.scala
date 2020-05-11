import akka.actor.ActorSystem

import scala.concurrent.{Await, ExecutionContext, Future, TimeoutException}


trait ThrottlingService {
  val graceRps:Int // configurable move to yml
  val slaService: SlaService // use mocks/stubs for testing
  // Should return true if the request is within allowed RPS.
  def isRequestAllowed(token: Option[String]): Future[Boolean]
}

class Throttling(val graceRps: Int, val slaService: SlaService) (implicit system: ActorSystem, ec: ExecutionContext) 
  extends ThrottlingService {
  
  val rpsCounter: RpsLog = RpsLog(system, ec)

  override def isRequestAllowed(token: Option[String]): Future[Boolean] = {
    val result = for {
      sla <- slaService.getSlaByToken(token.getOrElse(""))
      isValid = rpsCounter.hasReachedThreshold(sla) 
    } yield isValid
    result
  }
}



