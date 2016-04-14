package com.lily.mongo.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;

import com.lily.mongo.utils.Model;

@Embedded
public class CustomHeartRateZone extends Model {
	
	public Float caloriesOut;
	public Integer max;
	public Integer min;
	public Integer minutes;
	public String name;	
	
	public static Model.Finder<ObjectId, CustomHeartRateZone> find() {
		return new Model.Finder<ObjectId, CustomHeartRateZone>(ObjectId.class,
				CustomHeartRateZone.class);
	}

}
