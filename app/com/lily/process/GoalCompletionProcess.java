package com.lily.process;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.lily.models.FitbitUser;
import com.lily.models.GoalConfiguration;
import com.lily.mongo.models.GoalCompletion;
import com.lily.utils.LilyConstants;
import com.lily.utils.LilyConstants.DurationInterval;

/**
 * Goal Completion based on interval
 * 
 * @author Mohammad
 *
 */
@Singleton
public class GoalCompletionProcess {

	private static GoalCompletionProcess goalCompletionProcess;

	public static GoalCompletionProcess getInstance() {
		if (goalCompletionProcess != null)
			return goalCompletionProcess;

		synchronized (GoalCompletionProcess.class) {
			goalCompletionProcess = new GoalCompletionProcess();
		}
		return goalCompletionProcess;
	}

	@Inject
	private SleepGoalCalculationProcess sleepProcess;

	@Inject
	private ActivityGoalCalculationProcess activityGoalCalculationProcess;

	@Inject
	private StepGoalCalculationProcess stepGoalCalculationProcess;

	public GoalCompletionProcess() {
		if (sleepProcess == null)
			sleepProcess = new SleepGoalCalculationProcess();

		if (activityGoalCalculationProcess == null)
			activityGoalCalculationProcess = new ActivityGoalCalculationProcess();

		if (stepGoalCalculationProcess == null)
			stepGoalCalculationProcess = new StepGoalCalculationProcess();
	}

	/**
	 * Get Goal completion from Db or calculate and save it.
	 * 
	 * @param interval
	 * @throws Throwable
	 */
	public final GoalCompletion getGoalCompletion(FitbitUser fitbitUser,
			final DurationInterval interval) throws Throwable {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		Date startDate = cal.getTime();

		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = cal.getTime();

		//Get from Db.
		GoalCompletion oldGoalCompletion = GoalCompletion
				.q(GoalCompletion.class).field("date")
				.greaterThanOrEq(startDate).field("date")
				.lessThanOrEq(endDate).field("userId")
				.equal(fitbitUser.encodedId).get();
		
		//Else insert and return it.
		if (oldGoalCompletion == null){
			oldGoalCompletion = calculateGoalCompletion(fitbitUser, interval);
			oldGoalCompletion.userId = fitbitUser.encodedId;
			oldGoalCompletion.date = new Date();
			oldGoalCompletion.insert();
		}
		return oldGoalCompletion;
	}

	/**
	 * Get Goal completion based on interval.
	 * 
	 * @param interval
	 * @throws Throwable
	 */
	public final GoalCompletion calculateGoalCompletion(FitbitUser fitbitUser,
			final DurationInterval interval) throws Throwable {

		final GoalCompletion response = new GoalCompletion();

		response.sleepGoal = sleepProcess.getMonthlySleepGoal();
		response.sleepPoints = sleepProcess.calculate(fitbitUser, interval);

		response.stepGoal = stepGoalCalculationProcess.getMonthlyStepGoal();
		response.stepPoints = stepGoalCalculationProcess.calculate(fitbitUser,
				interval);

		response.activityGoal = activityGoalCalculationProcess
				.getMonthlyActivityGoal();
		response.activityPoints = activityGoalCalculationProcess.calculate(
				fitbitUser, interval);

		response.monthlyGoalCompletion = response.sleepGoal + response.stepGoal
				+ response.activityGoal;

		response.monthlyGoalCompletionPoints = response.sleepPoints
				+ response.stepPoints + response.activityPoints;

		response.monthlyGrowthPercentage = (response.monthlyGoalCompletionPoints / response.monthlyGoalCompletion) * 100;

		// Getting Multiplying coefficient from Db.
		List<GoalConfiguration> goalConfigList = GoalConfiguration
				.getGoalConfiguration(LilyConstants.GoalConfiguration.MONTHLY_GROWTH);

		response.multiplyingCoefficient = Math.round(GoalConfiguration
				.getRelatedPercentage(goalConfigList, 0) * 100.0) / 100.0;

		response.monthlyGrowth = (response.monthlyGoalCompletionPoints - response.monthlyGoalCompletion)
				* response.multiplyingCoefficient;
		return response;
	}
}
