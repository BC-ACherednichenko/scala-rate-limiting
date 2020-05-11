import java.util.concurrent.atomic.{AtomicInteger}

import akka.actor.{ActorSystem, Cancellable}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.duration._

trait CounterInterface {
  def getCount: Int
}

case class RateCounter() (implicit system: ActorSystem, ec: ExecutionContext) extends CounterInterface {
  val rpsCounter: AtomicInteger = new AtomicInteger(0)
  val duration: FiniteDuration = 25.seconds // TODO take from config rate decreasing time range 
  val runningScheduler: Cancellable = system.scheduler.scheduleAtFixedRate(Duration.Zero, duration)(runnable = () => {
    rpsCounter.set(0)
  })

  override def getCount: Int = {
    rpsCounter.getAndIncrement
  }
}
