package com.lily.mongo.models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.lily.mongo.utils.Model;

/**
 * Sleep time series data.
 * @author Mohammad
 *
 */
@Entity
public class SleepTimeSeries extends Model {

	@Id
	public ObjectId id;	
	
	public Date dateTime;
	public String value;
	
	public String resourcePath;
	public String userId;

	public static Model.Finder<ObjectId, SleepTimeSeries> find() {
		return new Model.Finder<ObjectId, SleepTimeSeries>(ObjectId.class,
				SleepTimeSeries.class);
	}
}