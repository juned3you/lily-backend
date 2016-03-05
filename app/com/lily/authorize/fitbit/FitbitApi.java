package com.lily.authorize.fitbit;

import java.util.Map;

import play.libs.Json;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.extractors.OAuth2AccessTokenExtractor;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.utils.OAuthEncoder;
import com.github.scribejava.core.utils.Preconditions;
import com.lily.models.Client;
import com.typesafe.config.ConfigFactory;

/**
 * Fitbit api.
 * 
 * @author Mohammad
 *
 */
public class FitbitApi extends DefaultApi20 {

	private Client fitbitClient = null;

	private FitbitApi(Client fitbitClient) {
		this.fitbitClient = fitbitClient;		
	}

	public static final FitbitApi instance(Client fitbitClient) {
		return new FitbitApi(fitbitClient);
	}

	private TokenExtractor<OAuth2AccessToken> tokenExtractor = new FitbitTokenExtractor();

	@Override
	public OAuth20Service createService(OAuthConfig config) {
		return new FitbitOAuth2Service(this, config);
	}

	@Override
	public String getAccessTokenEndpoint() {
		return fitbitClient.accessTokenUrl;
	}

	@Override
	public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
		return tokenExtractor;
	}

	@Override
	public Verb getAccessTokenVerb() {
		return Verb.POST;
	}

	@Override
	public String getAuthorizationUrl(OAuthConfig config,
			Map<String, String> arg1) {
		// Append scope if present
		String authUrl = fitbitClient.authorizationUrl;
		authUrl = authUrl
				+ "?client_id=%s&response_type=code&redirect_uri=%s&scope=%s";

		return String.format(authUrl, config.getApiKey(),
				OAuthEncoder.encode(config.getCallback()), config.getScope());
	}

	@Override
	public String getAuthorizationUrl(OAuthConfig config) {
		return getAuthorizationUrl(config, null);
	}

	/**
	 * Extract access token from Response.
	 * 
	 * @author Mohammad
	 *
	 */
	class FitbitTokenExtractor extends OAuth2AccessTokenExtractor {
		@Override
		public OAuth2AccessToken extract(String response) {
			Preconditions
					.checkEmptyString(response,
							"Response body is incorrect. Can't extract a token from an empty string");
			JsonNode jsResponse = Json.parse(response);
			JsonNode accesstokenJson = jsResponse.get("access_token");
			System.out.println(response);
			if (jsResponse != null && accesstokenJson != null) {
				String accesstoken = OAuthEncoder.decode(accesstokenJson
						.textValue());
				String refreshtoken = OAuthEncoder.decode(jsResponse.get(
						"refresh_token").textValue());
				int expiresIn = jsResponse.get("expires_in").intValue();
				String scope = jsResponse.get("scope").textValue();
				String tokenType = jsResponse.get("token_type").textValue();

				OAuth2AccessToken oauthToken = new OAuth2AccessToken(
						accesstoken, tokenType, expiresIn, refreshtoken, scope,
						response);

				return oauthToken;
			} else {
				throw new OAuthException(
						"Response body is incorrect. Can't extract a token from this: '"
								+ response + "'", null);
			}
		}
	}
}