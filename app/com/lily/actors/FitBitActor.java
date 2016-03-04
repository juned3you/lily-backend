package com.lily.actors;

import akka.actor.Props;
import akka.actor.UntypedActor;

import com.lily.authorize.Authorization;
import com.lily.authorize.AuthorizationResult;
import com.lily.authorize.fitbit.FitbitAuthorizationImpl;

/**
 * Akka Fitbit actor.
 * 
 * @author Mohammad
 *
 */
public class FitBitActor extends UntypedActor {

	public static Props props = Props.create(FitBitActor.class);

	public void onReceive(Object msg) throws Exception {
		System.out.println("Msg received: "+msg);
		
		Authorization auth = new FitbitAuthorizationImpl();
		AuthorizationResult result = auth.authorize();
		
		System.out.println(result);
		
		sender().tell("Test: " +msg, self());
	}
}