import akka.actor.ActorSystem

import scala.collection.mutable
import scala.concurrent.ExecutionContext

class RpsLog(implicit system: ActorSystem, ec: ExecutionContext)  {

  private val requestLog: mutable.Map[Int, CounterLog] =  mutable.Map()

  def hasReachedThreshold(sla: Sla): Boolean = {
    if (!requestLog.contains(sla.user.id)) {
      requestLog.put(sla.user.id, CounterLog(sla, new RateCounter))
    }
    !isValid(sla)
  }

  private def isValid(sla: Sla): Boolean = {
    val log = requestLog(sla.user.id)
    val count = log.rateCounter.getCount
    val result = count < sla.rps
    result
  }
}
case class CounterLog(sla: Sla, rateCounter: CounterInterface)

object RpsLog {
  def apply(implicit system: ActorSystem, ec: ExecutionContext) : RpsLog = new RpsLog
}



