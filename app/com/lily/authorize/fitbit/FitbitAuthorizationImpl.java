package com.lily.authorize.fitbit;

import java.util.Scanner;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.lily.authorize.Authorization;
import com.lily.authorize.AuthorizationResult;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import con.lily.exception.AuthorizationException;

/**
 * Fitbit authorization steps.
 * 
 * @author Mohammad
 *
 */
public class FitbitAuthorizationImpl implements Authorization {

	@Override
	public AuthorizationResult authorize() throws AuthorizationException {
		Config config = ConfigFactory.load();
		// Create OAuth20Service for FitbitApi
		OAuth20Service service = new ServiceBuilder()
				.apiKey(config.getString("fitbit.api.client.id"))
				.apiSecret(config.getString("fitbit.api.client.secret"))
				.callback(config.getString("fitbit.api.redirect.uri"))
				.scope(config.getString("fitbit.api.scope"))
				.build(new FitbitApi());

		// Obtain the Authorization URL
		System.out.println("Fetching the Authorization URL...");
		String authorizationUrl = service.getAuthorizationUrl(null);
		System.out.println("Got the Authorization URL!");
		System.out.println("Now go and authorize Scribe here:");
		System.out.println();

		Scanner in = new Scanner(System.in);

		// Copy this URL and run in browser.
		System.out.println(authorizationUrl);
		System.out.println();

		// Copy Authorization Code in browser URL and paste to Console
		System.out.println("And paste the authorization code here");
		System.out.print(">>");
		Verifier verifier = new Verifier(in.nextLine());
		System.out.println();

		// Trade the Request Token and Verfier for the Access Token
		System.out.println("Trading the Request Token for an Access Token...");
		OAuth2AccessToken accessToken = service.getAccessToken(verifier);
		System.out.println("Got the Access Token!");
		System.out.println("(if your curious it looks like this: "
				+ accessToken + " )");
		System.out.println();
		return null;
	}
}