package com.lily.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Akka;
import scala.concurrent.ExecutionContext.Implicits$;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;

import com.lily.actors.FitBitActor;
import com.lily.models.FitbitUser;
import com.lily.services.FitbitService;
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
	@Transactional
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

		Runnable task = new Runnable() {

			@Override
			public void run() {
				Logger.info("Fitbit Scheduler invoked !!!!!!!!!!!!! ");
				// Creating scheduler for per user call.
				List<FitbitUser> usersList = new FitbitService()
						.getFitbitUsers();

				if (usersList != null) {
					int counter = 0;

					for (FitbitUser usr : usersList) {
						
						//Only one time required.
						if (counter == 0) {
							FitBitActor.loadAllPublicActivities(usr);
							counter++;
						}

						try {
							Thread.sleep(2000);
							getSchedulerOnceCancellable(FitBitActor.props,
									Duration.create(1, TimeUnit.SECONDS), usr);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					;
				} else
					Logger.info("No User found in system");

			}
		};

		Cancellable scheduler = Akka
				.system()
				.scheduler()
				.schedule(startDelay, intervalInHours, task,
						Implicits$.MODULE$.global());
		schedulerList.add(scheduler);
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
	protected Cancellable getSchedulerCancellable(Props props,
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

	/**
	 * Create Cancellable Scheduler.
	 * 
	 * @param props
	 * @param startDelay
	 * @param interval
	 * @param msg
	 * @return
	 */
	private void getSchedulerOnceCancellable(Props props,
			FiniteDuration startDelay, Object msg) {
		try {
			ActorRef fitbitActor = Akka.system().actorOf(props);
			Akka.system()
					.scheduler()
					.scheduleOnce(startDelay, fitbitActor, msg,
							Implicits$.MODULE$.global(), null);

		} catch (IllegalStateException e) {
			Logger.error("Error caused by reloading application", e);
		} catch (Exception e) {
			Logger.error("", e);
		}
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
