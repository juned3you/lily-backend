package com.lily.actors;

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

	public void onReceive(Object msg) throws Exception {
		System.out.println("Msg received: "+msg);
		
		//Authorization auth = new FitbitAuthorizationImpl();
		//AuthorizationResponse result = auth.authorize();
		
		//System.out.println(result);
		
		sender().tell("Test: " +msg, self());
	}
}