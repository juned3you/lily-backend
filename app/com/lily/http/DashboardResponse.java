package com.lily.http;

import java.util.ArrayList;
import java.util.List;

import com.lily.mongo.models.GoalCompletion;

/**
 * Single dashboard response for a user.
 * 
 * @author Mohammad
 */
public class DashboardResponse {

	public GoalCompletion monthlyGoalCompletion;
	public float weeklyGoalCompletionPercentage;
	public List<FriendsResponse> friendsData = new ArrayList<FriendsResponse>();

	public ChartResponse chartData = new ChartResponse();
}