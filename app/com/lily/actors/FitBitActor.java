package com.lily.actors;

import play.Logger;

import com.lily.models.FitbitUser;
import com.lily.services.FitbitService;

import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * Akka Fitbit actor.
 * 
 * @author Mohammad
 *
 */
public class FitBitActor extends UntypedActor {

	public static Props props = Props.create(FitBitActor.class);

	/**
	 * On receive
	 */
	public void onReceive(Object user) throws Exception {
		if (!(user instanceof FitbitUser))
			throw new Exception("Fitbituser param is null in FitBitActor");
		FitbitUser fitbitUser = (FitbitUser) user;
		Logger.info("Update occur for fitbit user: " + fitbitUser.encodedId);
		try {
			new FitbitService().createUpdateUser(fitbitUser.encodedId);
		} catch (Throwable t) {
			Logger.error("Error updating user: " + fitbitUser.encodedId + "-> "
					+ t.getMessage());
			throw new Exception(t);
		}
		sender().tell("Success", self());
	}
}