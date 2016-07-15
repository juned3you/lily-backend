package com.lily.mongo.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lily.mongo.utils.Model;

@Entity
public class User extends Model {

	@Id
	public ObjectId id;
	public String aboutMe;
	public String avatar;
	public int age;
	public String city;
	public String country;
	public String dateOfBirth;
	public String displayName;
	public String encodedId;
	public String fullName;
	public String gender;
	public String nickname;
	public Float height;
	public String offsetFromUTCMillis;
	public Float strideLengthRunning;
	public Float strideLengthWalking;
	public String strideLengthWalkingType;
	public String timezone;
	public Float weight;
	
	public String avatar150;
	public Integer averageDailySteps;
	public Boolean corporate;
	public String distanceUnit;
	public Boolean friend;
	
	@JsonIgnore
	public String features;
	
	public String state;
	
	public String foodsLocale;
	public String glucoseUnit;
	public String heightUnit;
	public String locale;
	public String memberSince;
	public String startDayOfWeek;
	public String strideLengthRunningType;
	public String clockTimeDisplayFormat;
	
	@JsonIgnore
	public String sedentaryTime;
	
	@JsonIgnore
	public String topBadges;
	public String waterUnit;
	public String waterUnitName;
	public String weightUnit;

	public static Model.Finder<ObjectId, User> find() {
		return new Model.Finder<ObjectId, User>(ObjectId.class, User.class);
	}
}