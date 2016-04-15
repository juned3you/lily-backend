package com.lily.authorize.fitbit.transformer;

import java.util.List;

import play.libs.Json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.lily.exception.TransformerException;
import com.lily.extractor.ExtractorResponse;
import com.lily.mongo.models.Category;
import com.lily.transformer.Transformer;
import com.lily.utils.JsonUtils;

/**
 * Transform Json to model object.
 * 
 * @author Mohammad
 *
 */
public class CategoryTransformer implements Transformer {

	@Override
	public Object transform(Object obj) throws TransformerException {
		ExtractorResponse exResponse = (ExtractorResponse) obj;

		try {
			JsonNode jsValue = Json.parse(exResponse.getResponse());
			JsonNode node = jsValue.get("categories");

			// Creating Category model
			List<Category> categoryList = JsonUtils.convertToModelCollection(
					Json.stringify(node), new TypeReference<List<Category>>() {
					});
			return categoryList;
		} catch (Throwable e) {
			throw new TransformerException(e);
		}
	}

}
