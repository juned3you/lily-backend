package con.lily.scheduler;

/**
 * Schedule Akka job and cancel.
 * @author Mohammad
 *
 */
public interface Scheduler {
	
	public void schedule();
	public void cancel();
}