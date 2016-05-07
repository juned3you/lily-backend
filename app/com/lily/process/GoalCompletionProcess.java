package com.lily.process;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.lily.http.GoalCompletionResponse;
import com.lily.utils.LilyConstants.DurationInterval;

/**
 * Goal Completion based on interval
 * 
 * @author Mohammad
 *
 */
@Singleton
public class GoalCompletionProcess {

	@Inject
	private SleepGoalCalculationProcess sleepProcess;

	@Inject
	private ActivityGoalCalculationProcess activityGoalCalculationProcess;

	@Inject
	private StepGoalCalculationProcess stepGoalCalculationProcess;

	/**
	 * Get Goal completion based on interval.
	 * 
	 * @param interval
	 * @throws Throwable
	 */
	public final GoalCompletionResponse getGoalCompletion(String userId,
			final DurationInterval interval) throws Throwable {

		final GoalCompletionResponse response = new GoalCompletionResponse();

		response.sleepGoal = sleepProcess.getMonthlySleepGoal();
		response.sleepPoints = sleepProcess.calculate(userId, interval);

		response.stepGoal = stepGoalCalculationProcess.getMonthlyStepGoal();
		response.stepPoints = stepGoalCalculationProcess.calculate(userId,
				interval);

		response.activityGoal = activityGoalCalculationProcess
				.getMonthlyActivityGoal();
		response.activityPoints = activityGoalCalculationProcess.calculate(
				userId, interval);

		response.monthlyGoalCompletion = response.sleepGoal + response.stepGoal
				+ response.activityGoal;

		return response;
	}
}
