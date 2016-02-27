package com.lily.utils;

import org.mindrot.jbcrypt.BCrypt;

import com.typesafe.config.ConfigFactory;

/**
 * Password hasher
 * @author Mohammad 
 */
public class PasswordHasher {

	/**
	 * Rounds is configurable.
	 */
	private static final String RoundsProperty = "lily.passwordHasher.rounds";

	/**
	 * Encode password.
	 */
	public static String hash(String plainPassword) {
		int logRounds = ConfigFactory.load().getInt(RoundsProperty);
		return BCrypt.hashpw(plainPassword, BCrypt.gensalt(logRounds));
	}

	/**
	 * Match password after encode.
	 */
	public static boolean matches(String suppliedPassword, String dbPassword) {
		return BCrypt.checkpw(suppliedPassword, dbPassword);
	}
}
