import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

final case class User(id: Int, name: String)
case class Sla(user: User, rps:Int)

trait SlaService {
  val users : Map[String, Sla]
  def getSlaByToken(token:String): Future[Sla] = {
    Thread.sleep(250) // assuming the call will take 250 ms
    if (!users.contains(token)) {
      throw new Exception("No Sla exist")
    }
    Future.successful(users(token))
  }
}

trait SlaServiceCache extends SlaService {

  val tokensCache: mutable.Map[String, Sla] =  mutable.Map()

  override def getSlaByToken(token:String): Future[Sla] = {
    if (tokensCache.contains(token)) {
      Future.successful(tokensCache(token))
    } else {
      try {

      }
      val tokenValue = super.getSlaByToken(token)
      tokenValue.map {
        sla =>
          tokensCache.put(token, sla)
      }
      tokenValue
    }
  }
}

object SlaService extends SlaService with SlaServiceCache {

  // stub data for tokens
  val users : Map[String, Sla] =  Map(
    "6N2peswmp7IEFwiXxFWk" -> Sla(User(1, "test 1"), 5),
    "1UuD0G4NXVExEqJ12lkV" -> Sla(User(2, "test 2"), 8),
    "1K3s8Q1UGkz87jopiGn0" -> Sla(User(3, "test 3"), 10),
    "1K4s58Q1UGkz87jopiGn" -> Sla(User(3, "test 3"), 10)
  )
}


