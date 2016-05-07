package com.lily.models;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;

import play.db.jpa.JPA;

/**
 * Goal configuration store in db.
 * 
 * @author Mohammad
 *
 */
@Entity
@Table(name = "goal_configuration")
public class GoalConfiguration {

	@Id
	@Column(name = "id")
	public Integer id;

	@Column(name = "minimum_value")
	public Integer minValue;

	@Column(name = "maximum_value")
	public Integer maxValue;

	@Column(name = "value")
	public Float value;

	@Column(name = "type")
	public String type;

	/**
	 * Get Goal Config by type.
	 * @param type
	 * @return
	 * @throws Throwable
	 */
	@SuppressWarnings("unchecked")
	public static List<GoalConfiguration> getGoalConfiguration(String type)
			throws Throwable {
		return JPA.withTransaction(() -> {
			Query query = JPA.em().createQuery(
					"From GoalConfiguration WHERE type = '" + type
							+ "'");
			return query.getResultList();
		});
	}

	/**
	 * Compare value from the hardcoded values from DB and returns percentage value.
	 * @param goalConfigList
	 * @param currentValue
	 * @return
	 */
	public static Float getRelatedPercentage(
			List<GoalConfiguration> goalConfigList, Integer currentValue) {
		Float res = 0.0f;

		List<GoalConfiguration> filteredGoalConfigList = goalConfigList
				.stream()
				.filter(gc -> {
					if (gc.maxValue != -1) {
						if (currentValue >= gc.minValue
								&& currentValue < gc.maxValue)
							return true;
					} else if (currentValue >= gc.minValue)
						return true;
					return false;
				}).collect(Collectors.toList());

		if (filteredGoalConfigList != null && filteredGoalConfigList.size() > 0)
			res = filteredGoalConfigList.get(0).value;

		return res;
	}
}
