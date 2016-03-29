package com.lily.authorize.fitbit.transformer;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.lily.exception.TransformerException;
import com.lily.extractor.ExtractorResponse;
import com.lily.mongo.models.DailyActivity;
import com.lily.transformer.Transformer;
import com.lily.utils.DateUtils;
import com.lily.utils.JsonUtils;

/**
 * Transform activites response
 * 
 * @author Mohammad
 *
 */
public class DailyActivitiesTransformer implements Transformer {

	@Override
	public Object transform(Object obj) throws TransformerException {
		ExtractorResponse exResponse = (ExtractorResponse) obj;
		try {
			JsonNode jsValue = Json.parse(exResponse.getResponse());
			DailyActivity dailyActivity = JsonUtils.fromJson(jsValue,
					DailyActivity.class);
			dailyActivity.userId = exResponse.getUserId();
			dailyActivity.date = DateUtils.formatDate(exResponse.getDate());
			return dailyActivity;
		} catch (Throwable e) {
			throw new TransformerException(e);
		}
	}

}
