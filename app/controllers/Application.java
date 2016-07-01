package controllers;

import com.lily.actors.FitBitActor;
import com.lily.models.FitbitUser;
import com.lily.process.GoalCompletionProcess;
import com.lily.services.FitbitService;
import com.lily.utils.LilyConstants.DurationInterval;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class Application extends Controller {
		
    public Result index() throws Throwable {    	
    	FitbitUser fitbitUser = new FitbitService().getFitbitUser("4CMMSH");    	
    	//FitBitActor.loadSleepGoal(fitbitUser);
    	//new GoalCompletionProcess().getGoalCompletion(user.encodedId, DurationInterval.MONTHLY);
    	FitBitActor.calculateWeeklyGoalCompletion(fitbitUser);
        return ok(index.render("Your new application is ready."));
    }
    
    /*import static akka.pattern.Patterns.ask;
	import play.libs.Akka;
	import play.libs.F.Promise;
	import play.mvc.Controller;
	import play.mvc.Result;
	import akka.actor.ActorRef;
	import akka.actor.ActorSelection;
	public Promise<Result> index() {
		ActorRef helloActor = Akka.system().actorOf(FitBitActor.props);
        return Promise.wrap(ask(helloActor, "hello", 1000))
                      .map(response -> ok(response.toString()));
    }*/


    
    /**
	 * Add CORS headers.
	 * 
	 * @return
	 */
	public Result checkPreFlight() {
		response().setHeader("Access-Control-Allow-Origin", "*");
		response().setHeader("Access-Control-Allow-Methods", "POST");
		response()
				.setHeader(
						"Access-Control-Allow-Headers",
						"accept, origin, Content-type, x-json, x-prototype-version, x-requested-with, apiKey, X-Auth-Token");
		return ok();
	}

	/**
	 * Add CORS before any call.
	 * 
	 * @param all
	 * @return
	 */
	public Result preFlight(String all) {
		return checkPreFlight();
	}
	
	
}
