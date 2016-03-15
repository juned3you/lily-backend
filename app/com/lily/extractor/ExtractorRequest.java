package com.lily.extractor;

/**
 * Request for extracting uri or can be enhance.
 * 
 * @author Mohammad
 *
 */
public class ExtractorRequest {
	private String uri;

	public String getUri() {
		return uri;
	}

	public ExtractorRequest(String uri) {
		this.uri = uri;
	}
}
