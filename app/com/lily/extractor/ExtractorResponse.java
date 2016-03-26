package com.lily.extractor;

/**
 * Extract Results.
 * 
 * @author Mohammad
 *
 */
public class ExtractorResponse {
	private String response;
	private String date;
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ExtractorResponse(String response) {
		this.response = response;
	}

	public String getResponse() {
		return response;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}