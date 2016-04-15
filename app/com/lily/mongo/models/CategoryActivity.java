package com.lily.mongo.models;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;

import com.lily.mongo.utils.Model;

@Embedded
public class CategoryActivity extends Model {
	public Integer id;
	public String accessLevel;
	public Boolean hasSpeed;	
	public String name;
	public Float mets;	
	
	@Embedded
	public List<ActivityLevel> activityLevels;

	public static Model.Finder<ObjectId, CategoryActivity> find() {
		return new Model.Finder<ObjectId, CategoryActivity>(ObjectId.class,
				CategoryActivity.class);
	}
}