package com.lily.authorize.fitbit.transformer;

import java.util.List;

import play.libs.Json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.lily.exception.TransformerException;
import com.lily.extractor.ExtractorResponse;
import com.lily.mongo.models.HeartActivities;
import com.lily.transformer.Transformer;
import com.lily.utils.JsonUtils;

/**
 * Transform Json to model object.
 * 
 * @author Mohammad
 *
 */
public class HeartRateTransformer implements Transformer {

	@Override
	public Object transform(Object obj) throws TransformerException {
		ExtractorResponse exResponse = (ExtractorResponse) obj;
		try {
			JsonNode jsValue = Json.parse(exResponse.getResponse());
			JsonNode heartActivitiesNode = jsValue.get("activities-heart");

			// Creating weight log model
			List<HeartActivities> heartActivitiesList = JsonUtils
					.convertToModelCollection(
							Json.stringify(heartActivitiesNode),
							new TypeReference<List<HeartActivities>>() {
							});

			heartActivitiesList.forEach(weight -> {
				weight.userId = exResponse.getUserId();
			});
			return heartActivitiesList;
		} catch (Throwable e) {
			throw new TransformerException(e);
		}
	}
}
