package com.lily.utils;

import java.io.IOException;

import play.libs.Json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json Utils.
 * 
 * @author Mohammad
 *
 */
public class JsonUtils {

	public static <T> T convertToModel(String response, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		T resultClazz = mapper.readValue(response, clazz);
		return resultClazz;
	}
	
	/**
	 * From Json node to Model.
	 * @param jsonNode
	 * @param clazz
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T fromJson(JsonNode jsonNode, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {
		T resultClazz = Json.fromJson(jsonNode, clazz);		
		return resultClazz;
	}
}
