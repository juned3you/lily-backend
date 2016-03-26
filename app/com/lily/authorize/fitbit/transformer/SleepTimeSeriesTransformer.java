package com.lily.authorize.fitbit.transformer;

import java.util.List;

import play.libs.Json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.lily.exception.TransformerException;
import com.lily.extractor.ExtractorResponse;
import com.lily.mongo.models.SleepTimeSeries;
import com.lily.transformer.Transformer;
import com.lily.utils.JsonUtils;

/**
 * Transform Json to model object.
 * 
 * @author Mohammad
 *
 */
public class SleepTimeSeriesTransformer implements Transformer {

	@Override
	public Object transform(Object obj) throws TransformerException {
		ExtractorResponse exResponse = (ExtractorResponse) obj;
		String jsonNodeName = exResponse.getUri().replace("/", "-");
		try {
			JsonNode jsValue = Json.parse(exResponse.getResponse());
			JsonNode sleepNode = jsValue.get(jsonNodeName);

			// Creating Sleep Series model
			List<SleepTimeSeries> sleepTimeSeriesList = JsonUtils
					.convertToModelCollection(Json.stringify(sleepNode),
							new TypeReference<List<SleepTimeSeries>>() {
							});
			sleepTimeSeriesList.forEach(sleep -> {
				sleep.resourcePath = exResponse.getUri();
				sleep.userId = exResponse.getUserId();
			});
			return sleepTimeSeriesList;
		} catch (Throwable e) {
			throw new TransformerException(e);
		}
	}

}
