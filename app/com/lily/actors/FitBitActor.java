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
		sender().tell("Test: " +msg, self());
	}
}