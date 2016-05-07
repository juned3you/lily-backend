package controllers;

import play.mvc.Controller;

/**
 * Base controller
 * @author Mohammad
 *
 */
public class BaseController extends Controller {

	/**
	 * set Response headers
	 */
	protected void setResponseHeaders() {
		response().setHeader(CACHE_CONTROL, "no-store");
		response().setHeader(PRAGMA, "no-cache");
		response().setHeader(CONTENT_TYPE, "application/json");
	}
}