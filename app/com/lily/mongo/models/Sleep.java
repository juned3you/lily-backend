package com.lily.mongo.models;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Id;

import com.lily.mongo.utils.Model;

public class Sleep extends Model {
	
	@Id
	public ObjectId id;
	public Boolean isMainSleep;

	public Long logId;
	public Integer efficiency;
	public Date startTime;
	public Long duration;
	public Integer minutesToFallAsleep;
	public Integer minutesAsleep;
	public Integer minutesAwake;
	public Integer minutesAfterWakeup;
	public Integer awakeningsCount;
	public Integer awakeCount;

	public Integer awakeDuration;
	public Integer restlessCount;
	public Integer restlessDuration;
	public Integer timeInBed;
	public Date dateOfSleep;
	
	@Embedded
	public List<DateTimeData> minuteData;

	public static Model.Finder<ObjectId, Sleep> find() {
		return new Model.Finder<ObjectId, Sleep>(ObjectId.class, Sleep.class);
	}

}
