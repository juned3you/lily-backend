package com.lily.utils;

import java.util.Scanner;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.lily.authorize.fitbit.FitbitApi;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Auth test with Scribe java.
 * @author root
 *
 */
public class Test {

	public static void main(String[] str) {
		Config config = ConfigFactory.load();
		// Create OAuth20Service for FitbitApi
		OAuth20Service service = new ServiceBuilder()
				.apiKey(config.getString("fitbit.api.client.id"))
				.apiSecret(config.getString("fitbit.api.client.secret"))
				.callback(config.getString("fitbit.api.redirect.uri"))
				.scope(config.getString("fitbit.api.scope"))
				.build(FitbitApi.instance(null));

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

		JsonNode jsResponse = Json.parse(accessToken.getRawResponse());
		JsonNode userId = jsResponse.get("user_id");

		OAuthRequest request = new OAuthRequest(Verb.GET,
				"https://api.fitbit.com/1/user/" + userId.textValue()
						+ "/profile.json", service);
		service.signRequest(accessToken, request);
		Response response = request.send();
		System.out.println("Got it! Lets see what we found...");
		System.out.println();
		System.out.println(response.getCode());
		System.out.println(response.getBody());

		System.out.println();
		System.out
				.println("Thats it man! Go and build something awesome with Scribe! :)");
	}
}
