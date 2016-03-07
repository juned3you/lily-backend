package com.lily.scheduler;

import java.util.concurrent.TimeUnit;

import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Cancellable;

import com.lily.actors.FitBitActor;

/**
 * Fitbit scheduler.
 * 
 * @author Mohammad
 *
 */
public class FitbitScheduler implements Scheduler {

	private Cancellable scheduler;

	@Override
	public void schedule() {
		try {
			ActorRef fitbitActor = Akka.system().actorOf(FitBitActor.props);
			scheduler = Akka
					.system()
					.scheduler()
					.schedule(Duration.create(0, TimeUnit.MILLISECONDS),
							Duration.create(30, TimeUnit.MINUTES), fitbitActor,
							"tick", Akka.system().dispatcher(), null);
		} catch (IllegalStateException e) {
			Logger.error("Error caused by reloading application", e);
		} catch (Exception e) {
			Logger.error("", e);
		}

	}

	@Override
	public void cancel() {
		if (scheduler != null) {
			scheduler.cancel();
			this.scheduler = null;
		}

	}

}
