package com.lily.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Fitbit user definition from Json.
 * 
 * @author Mohammad
 */
@Entity
@Table(name = "fitbit_user")
@PrimaryKeyJoinColumn(name="user_id")
public class FitbitUser extends User {

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

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getAvatar150() {
		return avatar150;
	}

	public void setAvatar150(String avatar150) {
		this.avatar150 = avatar150;
	}

	public Integer getAverageDailySteps() {
		return averageDailySteps;
	}

	public void setAverageDailySteps(Integer averageDailySteps) {
		this.averageDailySteps = averageDailySteps;
	}

	public Boolean getCorporate() {
		return corporate;
	}

	public void setCorporate(Boolean corporate) {
		this.corporate = corporate;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDistanceUnit() {
		return distanceUnit;
	}

	public void setDistanceUnit(String distanceUnit) {
		this.distanceUnit = distanceUnit;
	}

	public String getEncodedId() {
		return encodedId;
	}

	public void setEncodedId(String encodedId) {
		this.encodedId = encodedId;
	}

	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}

	public String getFoodsLocale() {
		return foodsLocale;
	}

	public void setFoodsLocale(String foodsLocale) {
		this.foodsLocale = foodsLocale;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getGlucoseUnit() {
		return glucoseUnit;
	}

	public void setGlucoseUnit(String glucoseUnit) {
		this.glucoseUnit = glucoseUnit;
	}

	public Float getHeight() {
		return height;
	}

	public void setHeight(Float height) {
		this.height = height;
	}

	public String getHeightUnit() {
		return heightUnit;
	}

	public void setHeightUnit(String heightUnit) {
		this.heightUnit = heightUnit;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getMemberSince() {
		return memberSince;
	}

	public void setMemberSince(String memberSince) {
		this.memberSince = memberSince;
	}

	public String getOffsetFromUTCMillis() {
		return offsetFromUTCMillis;
	}

	public void setOffsetFromUTCMillis(String offsetFromUTCMillis) {
		this.offsetFromUTCMillis = offsetFromUTCMillis;
	}

	public String getStartDayOfWeek() {
		return startDayOfWeek;
	}

	public void setStartDayOfWeek(String startDayOfWeek) {
		this.startDayOfWeek = startDayOfWeek;
	}

	public Float getStrideLengthRunning() {
		return strideLengthRunning;
	}

	public void setStrideLengthRunning(Float strideLengthRunning) {
		this.strideLengthRunning = strideLengthRunning;
	}

	public String getStrideLengthRunningType() {
		return strideLengthRunningType;
	}

	public void setStrideLengthRunningType(String strideLengthRunningType) {
		this.strideLengthRunningType = strideLengthRunningType;
	}

	public Float getStrideLengthWalking() {
		return strideLengthWalking;
	}

	public void setStrideLengthWalking(Float strideLengthWalking) {
		this.strideLengthWalking = strideLengthWalking;
	}

	public String getStrideLengthWalkingType() {
		return strideLengthWalkingType;
	}

	public void setStrideLengthWalkingType(String strideLengthWalkingType) {
		this.strideLengthWalkingType = strideLengthWalkingType;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getTopBadges() {
		return topBadges;
	}

	public void setTopBadges(String topBadges) {
		this.topBadges = topBadges;
	}

	public String getWaterUnit() {
		return waterUnit;
	}

	public void setWaterUnit(String waterUnit) {
		this.waterUnit = waterUnit;
	}

	public String getWaterUnitName() {
		return waterUnitName;
	}

	public void setWaterUnitName(String waterUnitName) {
		this.waterUnitName = waterUnitName;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public String getWeightUnit() {
		return weightUnit;
	}

	public void setWeightUnit(String weightUnit) {
		this.weightUnit = weightUnit;
	}
}