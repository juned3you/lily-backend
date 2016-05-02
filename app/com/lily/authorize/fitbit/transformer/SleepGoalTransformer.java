package com.lily.authorize.fitbit.transformer;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.lily.exception.TransformerException;
import com.lily.extractor.ExtractorResponse;
import com.lily.mongo.models.SleepGoal;
import com.lily.transformer.Transformer;
import com.lily.utils.JsonUtils;

/**
 * Transform Json to model object.
 * 
 * @author Mohammad
 *
 */
public class SleepGoalTransformer implements Transformer {

	@Override
	public Object transform(Object obj) throws TransformerException {
		ExtractorResponse exResponse = (ExtractorResponse) obj;
		try {
			JsonNode jsValue = Json.parse(exResponse.getResponse());
			JsonNode goalNode = jsValue.get("goal");

			SleepGoal sleepGoal = JsonUtils.fromJson(goalNode, SleepGoal.class);
			sleepGoal.userId = exResponse.getUserId();
			return sleepGoal;
		} catch (Throwable e) {
			throw new TransformerException(e);
		}
	}
}
