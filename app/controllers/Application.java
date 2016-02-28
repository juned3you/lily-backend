package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    
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
