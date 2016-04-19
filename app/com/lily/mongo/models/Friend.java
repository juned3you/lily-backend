package com.lily.mongo.models;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import com.lily.mongo.utils.Model;

@Entity
public class Friend extends Model {
	@Id
	public ObjectId id;		
	public String userId;
	
	@Reference
	public List<User> users;
	
	public static Model.Finder<ObjectId, Friend> find() {
		return new Model.Finder<ObjectId, Friend>(ObjectId.class,
				Friend.class);
	}
}