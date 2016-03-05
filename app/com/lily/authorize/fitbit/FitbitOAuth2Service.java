package com.lily.authorize.fitbit;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Map;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.AbstractRequest;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
 * Firbit Auth2 implementation
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

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends AbstractRequest> T createAccessTokenRequest(
			Verifier verifier, T req) {
		OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(),
				api.getAccessTokenEndpoint(), this);

		try {
			String base64encodedString = "Basic "
					+ Base64.getEncoder().encodeToString(
							(config.getApiKey() + ":" + config.getApiSecret())
									.getBytes("utf-8"));
			request.addHeader(AUTHORIZATION, base64encodedString);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		try {
			String base64encodedString = "Basic "
					+ Base64.getEncoder().encodeToString(
							(config.getApiKey() + ":" + config.getApiSecret())
									.getBytes("utf-8"));
			request.addHeader(AUTHORIZATION, base64encodedString);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.addBodyParameter(OAuthConstants.GRANT_TYPE,
				OAuthConstants.REFRESH_TOKEN);
		request.addBodyParameter(OAuthConstants.REFRESH_TOKEN,
				refreshToken);		
		return (T) request;
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
		//request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN,
			//	accessToken.getAccessToken());
	}
}
