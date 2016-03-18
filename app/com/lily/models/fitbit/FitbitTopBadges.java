package com.lily.models.fitbit;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fitbit_topbadges")
public class FitbitTopBadges {

	@Id
	public int id;
}
