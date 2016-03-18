package com.lily.models.fitbit;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fitbit_features")
public class FitbitFeatures {

	@Id
	public int id;
	
	public Boolean exerciseGoal;
}
