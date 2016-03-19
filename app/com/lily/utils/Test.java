package com.lily.utils;

import java.util.Scanner;

import play.db.jpa.JPA;
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
import com.lily.models.Client;

/**
 * Auth test with Scribe java.
 * 
 * @author root
 *
 */
public class Test {

	public static void main(String[] str) throws Throwable {
		// Initialize client
		@SuppressWarnings("unchecked")
		Client fitbitClient = JPA.withTransaction(() -> {
			return (Client) JPA.em()
					.createQuery("FROM Client where name = :name")
					.setParameter("name", LilyConstants.Fitbit.CLIENT_NAME)
					.getResultList()
					.stream().findFirst().orElse(null);
		});

		// Create OAuth20Service for FitbitApi
		OAuth20Service service = new ServiceBuilder()
				.apiKey(fitbitClient.apiKey).apiSecret(fitbitClient.apiSecret)
				.callback(fitbitClient.redirectUri).scope(fitbitClient.scope)
				.build(FitbitApi.instance(fitbitClient));

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
