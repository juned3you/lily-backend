import play.Application;
import play.GlobalSettings;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import com.lily.mongo.utils.MorphiaPlugin;
import com.lily.scheduler.JobScheduler;

/**
 * Configuration for the project.
 * 
 * @author Mohammad
 *
 */
public class Global extends GlobalSettings {

	private MorphiaPlugin morphiaPlugin;

	private class ActionWrapper extends Action.Simple {
		public ActionWrapper(Action<?> action) {
			this.delegate = action;
		}

		@Override
		public Promise<Result> call(Http.Context ctx)
				throws java.lang.Throwable {
			Promise<Result> result = this.delegate.call(ctx);
			Http.Response response = ctx.response();
			response.setHeader("Access-Control-Allow-Origin", "*");
			return result;
		}
	}

	@Override
	public Action<?> onRequest(Http.Request request,
			java.lang.reflect.Method actionMethod) {
		return new ActionWrapper(super.onRequest(request, actionMethod));
	}

	@Override
	public void onStart(Application arg0) {
		JobScheduler.scheduleJobs();
		morphiaPlugin = new MorphiaPlugin(arg0);
		morphiaPlugin.onStart();
	}

	@Override
	public void onStop(Application arg0) {
		JobScheduler.stopJobs();
		morphiaPlugin.onStop();
	}
}