package com.lily.mongo.models;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import com.lily.mongo.utils.Model;

@Entity
public class Category extends Model {
	@Id
	public ObjectId _id;	
	public Integer id;
	public String name;	
	
	@Embedded
	public List<CategoryActivity> activities;	
	
	@Reference
	public List<Category> subCategories;	

	public static Model.Finder<ObjectId, Category> find() {
		return new Model.Finder<ObjectId, Category>(ObjectId.class,
				Category.class);
	}
}