package com.lily.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;

import com.avaje.ebean.Ebean;
import com.lily.actors.FitBitActor;
import com.lily.models.FitbitUser;
import com.lily.models.User;
import com.lily.utils.DateUtils;
import com.typesafe.config.ConfigFactory;

/**
 * Fitbit scheduler.
 * 
 * @author Mohammad
 *
 */
public class FitbitScheduler implements Scheduler {

	private List<Cancellable> schedulerList;

	/**
	 * Schedule multiple jobs using Actors.
	 */
	@Override
	public void schedule() {
		schedulerList = new ArrayList<Cancellable>();

		final int hour = ConfigFactory.load().getInt(
				"fitbit.scheduler.invoke.hour");
		final int mins = ConfigFactory.load().getInt(
				"fitbit.scheduler.invoke.mins");
		final int seconds = DateUtils.nextExecutionInSeconds(hour, mins,
				DateUtils.getCetDateTime());

		FiniteDuration startDelay = Duration.create(seconds, TimeUnit.SECONDS);
		FiniteDuration intervalInHours = Duration.create(24, TimeUnit.HOURS);

		
		//Creating scheduler for per user call.
		List<FitbitUser> user = Ebean.createQuery(FitbitUser.class).findList();
		user.stream().forEach(
				usr -> {
					schedulerList.add(getSchedulerCancellable(
							FitBitActor.props, startDelay, intervalInHours,
							usr));
				});
	}

	/**
	 * Create Cancellable Scheduler.
	 * 
	 * @param props
	 * @param startDelay
	 * @param interval
	 * @param msg
	 * @return
	 */
	private Cancellable getSchedulerCancellable(Props props,
			FiniteDuration startDelay, FiniteDuration interval, Object msg) {
		try {
			ActorRef fitbitActor = Akka.system().actorOf(props);
			Cancellable scheduler = Akka
					.system()
					.scheduler()
					.schedule(startDelay, interval, fitbitActor, msg,
							Akka.system().dispatcher(), null);
			return scheduler;
		} catch (IllegalStateException e) {
			Logger.error("Error caused by reloading application", e);
		} catch (Exception e) {
			Logger.error("", e);
		}
		return null;
	}

	@Override
	public void cancel() {
		if (schedulerList != null) {
			schedulerList.stream().forEach(scheduler -> {
				scheduler.cancel();
			});
		}
	}
}
