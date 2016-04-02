package com.lily.authorize.fitbit.transformer;

import java.util.List;

import play.libs.Json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.lily.exception.TransformerException;
import com.lily.extractor.ExtractorResponse;
import com.lily.mongo.models.FatLog;
import com.lily.transformer.Transformer;
import com.lily.utils.JsonUtils;

/**
 * Transform Json to model object.
 * 
 * @author Mohammad
 *
 */
public class FatLogTransformer implements Transformer {

	@Override
	public Object transform(Object obj) throws TransformerException {
		ExtractorResponse exResponse = (ExtractorResponse) obj;
		try {
			JsonNode jsValue = Json.parse(exResponse.getResponse());
			JsonNode fatNode = jsValue.get("fat");

			// Creating Sleep log model
			List<FatLog> fatLogs = JsonUtils.convertToModelCollection(
					Json.stringify(fatNode), new TypeReference<List<FatLog>>() {
					});

			fatLogs.forEach(sleep -> {
				sleep.userId = exResponse.getUserId();
			});
			return fatLogs;
		} catch (Throwable e) {
			throw new TransformerException(e);
		}
	}
}
