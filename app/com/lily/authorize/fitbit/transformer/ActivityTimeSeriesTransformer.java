package com.lily.authorize.fitbit.transformer;

import java.util.List;

import play.libs.Json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.lily.exception.TransformerException;
import com.lily.extractor.ExtractorResponse;
import com.lily.mongo.models.ActivityTimeSeries;
import com.lily.transformer.Transformer;
import com.lily.utils.JsonUtils;

/**
 * Transform Json to model object.
 * 
 * @author Mohammad
 *
 */
public class ActivityTimeSeriesTransformer implements Transformer {

	@Override
	public Object transform(Object obj) throws TransformerException {
		ExtractorResponse exResponse = (ExtractorResponse) obj;
		String jsonNodeName = exResponse.getUri().replace("/", "-");
		try {
			JsonNode jsValue = Json.parse(exResponse.getResponse());
			JsonNode node = jsValue.get(jsonNodeName);

			// Creating Sleep Series model
			List<ActivityTimeSeries> activityTimeSeriesList = JsonUtils
					.convertToModelCollection(Json.stringify(node),
							new TypeReference<List<ActivityTimeSeries>>() {
							});
			activityTimeSeriesList.forEach(sleep -> {
				sleep.resourcePath = exResponse.getUri();
				sleep.userId = exResponse.getUserId();
			});
			return activityTimeSeriesList;
		} catch (Throwable e) {
			throw new TransformerException(e);
		}
	}

}
