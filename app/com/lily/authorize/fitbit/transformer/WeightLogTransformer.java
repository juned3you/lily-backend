package com.lily.authorize.fitbit.transformer;

import java.util.List;

import play.Logger;
import play.libs.Json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.lily.exception.TransformerException;
import com.lily.extractor.ExtractorResponse;
import com.lily.mongo.models.WeightLog;
import com.lily.transformer.Transformer;
import com.lily.utils.JsonUtils;

/**
 * Transform Json to model object.
 * 
 * @author Mohammad
 *
 */
public class WeightLogTransformer implements Transformer {

	@Override
	public Object transform(Object obj) throws TransformerException {
		ExtractorResponse exResponse = (ExtractorResponse) obj;
		JsonNode jsValue = null;
		try {
			jsValue = Json.parse(exResponse.getResponse());
			JsonNode fatNode = jsValue.get("weight");

			// Creating weight log model
			List<WeightLog> weightLogs = JsonUtils.convertToModelCollection(
					Json.stringify(fatNode),
					new TypeReference<List<WeightLog>>() {
					});

			weightLogs.forEach(weight -> {
				weight.userId = exResponse.getUserId();
			});
			return weightLogs;
		} catch (Throwable e) {
			if (jsValue != null)
				Logger.info(jsValue.toString());
			throw new TransformerException(e);
		}
	}
}
