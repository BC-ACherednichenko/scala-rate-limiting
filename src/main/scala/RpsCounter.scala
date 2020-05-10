import scala.collection.mutable

class RpsCounter {

  private val requestLog: mutable.Map[Int, CounterLog] =  mutable.Map()

  def hasReachedThreshold(sla: Sla): Boolean = {
    if (!requestLog.contains(sla.user.id)) {
      requestLog.put(sla.user.id, CounterLog(sla.user.id, sla))
    }
    !isValid(sla)
  }

  private def isValid(sla: Sla): Boolean = {
    val log = requestLog(sla.user.id)
    if (log.requestCount < sla.rps) {
      increaseCounter(sla)
      true
    } else {
      false
    }
  }

  private def increaseCounter(sla: Sla): Unit = {
    val log = requestLog(sla.user.id)
    requestLog.put(sla.user.id, CounterLog(sla.user.id, sla, log.requestCount + 1))
  }
}
case class CounterLog(userId: Int, sla: Sla, requestCount: Int = 1)

object RpsCounter {
  def apply(): RpsCounter = new RpsCounter()
}



