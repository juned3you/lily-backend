package con.lily.scheduler;

/**
 * Scheduler Job.
 * 
 * @author Mohammad
 *
 */
public class JobScheduler {

	private static Scheduler firbitScheduler = null;

	/**
	 * Schedule all Akka Jobs
	 */
	public static void scheduleJobs() {
		firbitScheduler = new FitbitScheduler();
		firbitScheduler.schedule();
	}

	/**
	 * Cancel all Akka jobs
	 */
	public static void stopJobs() {
		firbitScheduler.cancel();
	}
}