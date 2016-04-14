package com.lily.mongo.models;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;

import com.lily.mongo.utils.Model;

@Embedded
public class HeartActivitiesValue extends Model {	

	public List<CustomHeartRateZone> customHeartRateZones;

	public List<CustomHeartRateZone> heartRateZones;

	public static Model.Finder<ObjectId, HeartActivitiesValue> find() {
		return new Model.Finder<ObjectId, HeartActivitiesValue>(ObjectId.class,
				HeartActivitiesValue.class);
	}

}
