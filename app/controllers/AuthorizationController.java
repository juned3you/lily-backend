package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import com.lily.authorize.Authorization;
import com.lily.exception.AuthorizationException;
import com.lily.factory.AuthorizationFactory;

/**
 * Auth methods
 * @author Mohammad Juned
 *
 */
public class AuthorizationController extends Controller {

	/**
	 * Get AUth Url based on client name
	 * 
	 * @return
	 * @throws AuthorizationException
	 */
	public Result getAuthorizationUrl(String clientName)
			throws AuthorizationException {
		Authorization auth = AuthorizationFactory
				.getAuthorizationImpl(clientName);
		if (auth != null)
			return ok(auth.getAuthorizationUrl());

		return badRequest("Client not supported!!");
	}
}
