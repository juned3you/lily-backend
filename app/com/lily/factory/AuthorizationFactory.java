package com.lily.factory;

import com.lily.authorize.Authorization;
import com.lily.authorize.fitbit.FitbitAuthorizationImpl;
import com.lily.utils.LilyConstants;

public class AuthorizationFactory {

	/**
	 * Return Instance based on clientName
	 * 
	 * @param clientName
	 * @return
	 */
	public static Authorization getAuthorizationImpl(String clientName) {
		if (clientName.equals(LilyConstants.Fitbit.CLIENT_NAME)
				|| clientName.equals("fitbitdev")) {
			return new FitbitAuthorizationImpl();
		}
		return null;
	}
}