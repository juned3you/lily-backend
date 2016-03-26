package com.lily.mongo.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.lily.mongo.utils.Model;

/**
 * Sleep time series data.
 * @author Mohamad
 *
 */
@Entity
public class SleepTimeSeries extends Model {

	@Id
	public ObjectId id;	
	
	public String dateTime;
	public Integer value;
	
	public String resourcePath;

	public static Model.Finder<ObjectId, SleepTimeSeries> find() {
		return new Model.Finder<ObjectId, SleepTimeSeries>(ObjectId.class,
				SleepTimeSeries.class);
	}
}