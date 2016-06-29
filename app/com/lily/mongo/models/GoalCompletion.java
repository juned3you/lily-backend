package com.lily.mongo.models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.lily.mongo.utils.Model;

@Entity
public class GoalCompletion extends Model {
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

	public Integer monthlyGoalCompletion;
	public Float monthlyGoalCompletionPoints;

	public Double multiplyingCoefficient;
	public Float monthlyGrowthPercentage;

	public Double monthlyGrowth;

	public static Model.Finder<ObjectId, GoalCompletion> find() {
		return new Model.Finder<ObjectId, GoalCompletion>(ObjectId.class,
				GoalCompletion.class);
	}

	/**
	 * Get Goal completion by Range.
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static GoalCompletion getGoalCompletion(String userId,
			Date startDate, Date endDate) {
		GoalCompletion goalCompletion = GoalCompletion.q(GoalCompletion.class)
				.field("date").greaterThanOrEq(startDate).field("date")
				.lessThanOrEq(endDate).field("userId").equal(userId).get();
		return goalCompletion;
	}
}