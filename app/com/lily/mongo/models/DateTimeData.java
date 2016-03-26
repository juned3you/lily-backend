package com.lily.mongo.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;

import com.lily.mongo.utils.Model;

@Embedded
public class DateTimeData extends Model {	
	
	public String dateTime;
	public Integer value;
	
	public static Model.Finder<ObjectId, DateTimeData> find() {
		return new Model.Finder<ObjectId, DateTimeData>(ObjectId.class,
				DateTimeData.class);
	}
}
