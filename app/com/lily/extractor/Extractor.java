package com.lily.extractor;

import com.lily.exception.ExtractorException;

/**
 * Extractor Interface.
 * @author Mohammad
 *
 */
public interface Extractor {
	
	/**
	 * Extract logic and response.
	 * @param request
	 * @return
	 * @throws ExtractorException
	 */
	public ExtractorResponse extract(ExtractorRequest request) throws ExtractorException;
}
