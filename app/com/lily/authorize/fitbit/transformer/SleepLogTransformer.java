package com.lily.authorize.fitbit.transformer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import play.libs.Json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.lily.exception.TransformerException;
import com.lily.extractor.ExtractorResponse;
import com.lily.mongo.models.Sleep;
import com.lily.mongo.models.SleepLog;
import com.lily.mongo.models.SleepSummary;
import com.lily.transformer.Transformer;
import com.lily.utils.DateUtils;
import com.lily.utils.JsonUtils;

/**
 * Transform Json to model object.
 * 
 * @author Mohammad
 *
 */
public class SleepLogTransformer implements Transformer {

	@Override
	public Object transform(Object obj) throws TransformerException {
		ExtractorResponse exResponse = (ExtractorResponse) obj;
		try {
			JsonNode jsValue = Json.parse(exResponse.getResponse());
			
			//JsonNode jsValue = Json.parse(getDumpResponse());
			
			JsonNode sleepNode = jsValue.get("sleep");
			JsonNode summaryNode = jsValue.get("summary");

			//Creating Sleep log model
			SleepLog sleepLog = new SleepLog();
			sleepLog.sleep = JsonUtils.convertToModelCollection(
					Json.stringify(sleepNode),
					new TypeReference<List<Sleep>>() {
					});
			sleepLog.summary = JsonUtils.fromJson(summaryNode,
					SleepSummary.class);

			sleepLog.date = DateUtils.formatDate(exResponse.getDate());
			sleepLog.userId = exResponse.getUserId();
			return sleepLog;
		} catch (Throwable e) {
			throw new TransformerException(e);
		}
	}
	
	protected String getDumpResponse(){
		try {
			return new String(Files.readAllBytes(Paths.get("/juned/Wineta/sleep.json")));
		} catch (IOException e) {			
			e.printStackTrace();
		}
		return null;
	}
}
