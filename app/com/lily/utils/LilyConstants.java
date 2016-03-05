package com.lily.utils;

import com.typesafe.config.ConfigFactory;

public interface LilyConstants {

	String AUTHORIZATION_CODE = "AUTHORIZATION_CODE";

	public String FITBIT_CLIENT_NAME = ConfigFactory.load().getString(
			"fitbit.client.name");
}
