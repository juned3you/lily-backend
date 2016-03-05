package controllers;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.lily.authorize.Authorization;
import com.lily.authorize.AuthorizationRequest;
import com.lily.factory.AuthorizationFactory;
import com.lily.utils.LilyConstants;

import con.lily.exception.AuthorizationException;

/**
 * Fitbit controller for Fitbit operations.
 * 
 * @author Mohammad
 *
 */
public class FitbitController extends Controller {

	/**
	 * check Fitbit code in request and register user.
	 * 
	 * @return
	 * @throws AuthorizationException
	 */
	public Result oauthCallback() throws AuthorizationException {
		final String code = request().getQueryString("code");
		if (code == null)
			return badRequest("No authorization code found !!");		

		AuthorizationRequest authRequest = new AuthorizationRequest();
		authRequest.authorizationCode = code;
		Authorization auth = AuthorizationFactory
				.getAuthorizationImpl(LilyConstants.FITBIT_CLIENT_NAME);		
		return ok(Json.toJson(auth.authorize(authRequest)));
	}
}