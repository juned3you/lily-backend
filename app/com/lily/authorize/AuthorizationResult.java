package com.lily.authorize;

/**
 * Auth result.
 * 
 * @author Mohammad
 *
 */
public class AuthorizationResult {
	public String error = null;
	public String success = null;
	public Integer status = null;

	@Override
	public String toString() {
		return status + ": " +error+ " " + success;
	}
}