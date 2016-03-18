package com.lily.authorize.fitbit.extractor;

import com.lily.exception.ExtractorException;
import com.lily.extractor.Extractor;
import com.lily.extractor.ExtractorRequest;
import com.lily.extractor.ExtractorResponse;
import com.lily.services.FitbitService;

/**
 * Fitbit profile extractor.
 * 
 * @author Mohammad
 *
 */
public class FitbitExtractor implements Extractor {	

	/**
	 * Extract any url, just pass userid and uri.
	 */
	@Override
	public ExtractorResponse extract(ExtractorRequest request)
			throws ExtractorException {
		try {
			String response = new FitbitService().getDynamicData(request.getUserId(),
					request.getUri());
			return new ExtractorResponse(response);
		} catch (Exception e) {
			throw new ExtractorException(e);
		}
	}
}