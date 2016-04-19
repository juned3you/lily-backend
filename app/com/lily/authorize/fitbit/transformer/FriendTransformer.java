package com.lily.authorize.fitbit.transformer;

import java.util.ArrayList;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.lily.exception.TransformerException;
import com.lily.extractor.ExtractorResponse;
import com.lily.mongo.models.Friend;
import com.lily.mongo.models.User;
import com.lily.transformer.Transformer;
import com.lily.utils.JsonUtils;

/**
 * Transform Json to model object.
 * 
 * @author Mohammad
 *
 */
public class FriendTransformer implements Transformer {

	@Override
	public Object transform(Object obj) throws TransformerException {
		ExtractorResponse exResponse = (ExtractorResponse) obj;
		try {
			JsonNode jsValue = Json.parse(exResponse.getResponse());
			JsonNode friendsNode = jsValue.get("friends");
			Friend friend = new Friend();
			friend.userId = exResponse.getUserId();
			friend.users = new ArrayList<>();

			for (final JsonNode objNode : friendsNode) {
				JsonNode user = objNode.get("user");
				friend.users.add(JsonUtils.fromJson(user, User.class));
			}

			return friend;
		} catch (Throwable e) {
			throw new TransformerException(e);
		}
	}
}
