package com.lily.extractor;

/**
 * Request for extracting uri or can be enhance.
 * 
 * @author Mohammad
 *
 */
public class ExtractorRequest {
	private String userId;
	private String uri;

	public String getUri() {
		return uri;
	}

	public ExtractorRequest(String userId, String uri) {
		this.userId = userId;
		this.uri = uri;
	}
	
	public String getUserId() {
		return userId;
	}
}
