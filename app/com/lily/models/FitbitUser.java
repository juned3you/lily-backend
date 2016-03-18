package com.lily.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Fitbit user definition from Json.
 * 
 * @author Mohammad
 */
@Entity
@Table(name = "fitbit_user")
public class FitbitUser extends BaseEntity {

	@Column(name = "age")
	public Integer age;

	@Column(name = "avatar")
	public String avatar;

	@Column(name = "avatar150")
	public String avatar150;

	@Column(name = "average_daily_steps")
	public Integer averageDailySteps;

	@Column(name = "corporate")
	public Boolean corporate;

	@Column(name = "country")
	public String country;

	@Column(name = "date_of_birth")
	public String dateOfBirth;

	@Column(name = "display_name")
	public String displayName;

	@Column(name = "distance_unit")
	public String distanceUnit;

	@Column(name = "encoded_id")
	public String encodedId; // user_id

	@JsonIgnore
	@Column(name = "features")
	public String features;

	@Column(name = "foods_locale")
	public String foodsLocale;

	@Column(name = "full_name")
	public String fullName;

	@Column(name = "gender")
	public String gender;

	@Column(name = "glucose_unit")
	public String glucoseUnit; // unit

	@Column(name = "height")
	public Float height;

	@Column(name = "height_unit")
	public String heightUnit; // unit

	@Column(name = "locale")
	public String locale;

	@Column(name = "member_since")
	public String memberSince;

	@Column(name = "offset_from_utc_millis")
	public String offsetFromUTCMillis;

	@Column(name = "start_day_of_week")
	public String startDayOfWeek;

	@Column(name = "stride_length_running")
	public Float strideLengthRunning;

	@Column(name = "stride_length_running_type")
	public String strideLengthRunningType;

	@Column(name = "stride_length_walking")
	public Float strideLengthWalking;

	@Column(name = "stride_length_walking_type")
	public String strideLengthWalkingType;

	@Column(name = "timezone")
	public String timezone;

	@JsonIgnore
	@Column(name = "top_badges")
	public String topBadges;

	@Column(name = "water_unit")
	public String waterUnit; // unit

	@Column(name = "water_unit_name")
	public String waterUnitName;

	@Column(name = "weight")
	public Float weight;

	@Column(name = "weight_unit")
	public String weightUnit; // unit

	@Column(name = "user_id")
	@OneToOne
	public User user;
}