package com.lily.mongo.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.lily.mongo.utils.Model;

@Entity
public class SleepSummary extends Model {
	@Id
	public ObjectId id;

	public Integer totalMinutesAsleep;

	public Integer totalSleepRecords;

	public Integer totalTimeInBed;

	public static Model.Finder<ObjectId, SleepSummary> find() {
		return new Model.Finder<ObjectId, SleepSummary>(ObjectId.class,
				SleepSummary.class);
	}
}
