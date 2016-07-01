package com.lily.mongo.models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.lily.mongo.utils.Model;
import com.lily.utils.DateUtils;

@Entity
public class WeeklyGoalCompletion extends Model {
	@Id
	public ObjectId id;
	public String userId;

	public Date date;

	public Integer sleepGoal;
	public Float sleepPoints;

	public Integer stepGoal;
	public Float stepPoints;

	public Integer activityGoal;
	public Float activityPoints;

	public Integer weeklyGoalCompletion;
	public Float weeklyGoalCompletionPoints;

	public Double multiplyingCoefficient;
	public Float weeklyGrowthPercentage;

	public Double weeklyGrowth;

	public static Model.Finder<ObjectId, WeeklyGoalCompletion> find() {
		return new Model.Finder<ObjectId, WeeklyGoalCompletion>(ObjectId.class,
				WeeklyGoalCompletion.class);
	}

	/**
	 * Get Goal completion by Range.
	 * 
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static WeeklyGoalCompletion getGoalCompletion(String userId,
			Date startDate, Date endDate) {
		WeeklyGoalCompletion goalCompletion = WeeklyGoalCompletion
				.q(WeeklyGoalCompletion.class).field("date")
				.greaterThanOrEq(DateUtils.setStartTime(startDate))
				.field("date").lessThanOrEq(DateUtils.setEndTime(endDate))
				.field("userId").equal(userId).get();
		return goalCompletion;
	}
}