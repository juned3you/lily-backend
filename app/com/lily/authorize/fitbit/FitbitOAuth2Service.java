package com.lily.authorize.fitbit;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Map;

import play.db.jpa.Transactional;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.AbstractRequest;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.lily.models.Client;
import com.lily.utils.LilyConstants;

/**
 * Firbit Auth2 implementation
 * 
 * @author Mohammad
 *
 */
public class FitbitOAuth2Service extends OAuth20Service {

	private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
	private static final String GRANT_TYPE = "grant_type";
	private static final String AUTHORIZATION = "Authorization";
	private static final String VERSION = "2.0";

	private DefaultApi20 api;
	private OAuthConfig config;

	public FitbitOAuth2Service(DefaultApi20 api, OAuthConfig config) {
		super(api, config);
		this.api = api;
		this.config = config;
	}

	/**
	 * Get fitbit client from DB.
	 * 
	 * @return
	 */
	@Transactional
	public static Client getFitbitClient() {

		Client fitbitClient = null;
		try {
			fitbitClient = Client.getClientByName(LilyConstants.Fitbit.CLIENT_NAME);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fitbitClient;
	}
	
	/**
	 * Get fitbit client from DB.
	 * 
	 * @return
	 */
	@Transactional
	public static Client getFitbitClient(String clientName) {

		Client fitbitClient = null;
		try {
			fitbitClient = Client.getClientByName(clientName);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fitbitClient;
	}

	/**
	 * Git Fitbit OAuth20Service by client
	 * 
	 * @param fitbitClient
	 * @return
	 */
	public static OAuth20Service getFitbitOAuth2ServiceInstance(
			Client fitbitClient) {

		// Create OAuth20Service for FitbitApi
		OAuth20Service service = new ServiceBuilder()
				.apiKey(fitbitClient.apiKey).apiSecret(fitbitClient.apiSecret)
				.callback(fitbitClient.redirectUri).scope(fitbitClient.scope)
				.build(FitbitApi.instance(fitbitClient));
		return service;
	}

	/**
	 * Git Fitbit OAuth20Service.
	 * 
	 * @return
	 */
	@Transactional
	public static OAuth20Service getFitbitOAuth2ServiceInstance() {
		Client fitbitClient = null;
		try {
			fitbitClient = Client.getClientByName(LilyConstants.Fitbit.CLIENT_NAME);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Create OAuth20Service for FitbitApi
		OAuth20Service service = new ServiceBuilder()
				.apiKey(fitbitClient.apiKey).apiSecret(fitbitClient.apiSecret)
				.callback(fitbitClient.redirectUri).scope(fitbitClient.scope)
				.build(FitbitApi.instance(fitbitClient));
		return service;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends AbstractRequest> T createAccessTokenRequest(
			Verifier verifier, T req) {
		OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(),
				api.getAccessTokenEndpoint(), this);

		addAuthorizatioHeader(request);

		switch (api.getAccessTokenVerb()) {
		case POST:
			request.addBodyParameter(OAuthConstants.CLIENT_ID,
					config.getApiKey());
			request.addBodyParameter(OAuthConstants.CLIENT_SECRET,
					config.getApiSecret());
			request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
			request.addBodyParameter(OAuthConstants.REDIRECT_URI,
					config.getCallback());
			request.addBodyParameter(GRANT_TYPE, GRANT_TYPE_AUTHORIZATION_CODE);
			break;
		case GET:
		default:
			request.addQuerystringParameter(OAuthConstants.CLIENT_ID,
					config.getApiKey());
			request.addQuerystringParameter(OAuthConstants.CLIENT_SECRET,
					config.getApiSecret());
			request.addQuerystringParameter(OAuthConstants.CODE,
					verifier.getValue());
			request.addQuerystringParameter(OAuthConstants.REDIRECT_URI,
					config.getCallback());
			if (config.hasScope())
				request.addQuerystringParameter(OAuthConstants.SCOPE,
						config.getScope());
		}
		return (T) request;
	}

	/**
	 * Refresh token.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <T extends AbstractRequest> T createRefreshTokenRequest(
			String refreshToken, T req) {
		OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(),
				api.getAccessTokenEndpoint(), this);

		addAuthorizatioHeader(request);

		request.addBodyParameter(OAuthConstants.GRANT_TYPE,
				OAuthConstants.REFRESH_TOKEN);
		request.addBodyParameter(OAuthConstants.REFRESH_TOKEN, refreshToken);
		return (T) request;
	}

	/**
	 * Compose base 64 string and add in request header.
	 * 
	 * @param request
	 */
	private void addAuthorizatioHeader(OAuthRequest request) {
		try {
			String base64encodedString = "Basic "
					+ Base64.getEncoder().encodeToString(
							(config.getApiKey() + ":" + config.getApiSecret())
									.getBytes("utf-8"));
			request.addHeader(AUTHORIZATION, base64encodedString);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public DefaultApi20 getApi() {
		return api;
	}

	@Override
	public String getAuthorizationUrl(Map<String, String> additionalParams) {
		return api.getAuthorizationUrl(config);
	}

	@Override
	public String getVersion() {
		return VERSION;
	}

	@Override
	public void signRequest(OAuth2AccessToken accessToken,
			AbstractRequest request) {
		String tokenString = accessToken.getTokenType() + " "
				+ accessToken.getAccessToken();
		request.addHeader(AUTHORIZATION, tokenString);
		// request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN,
		// accessToken.getAccessToken());
	}
}
