package com.lily.actors;

import com.lily.authorize.fitbit.extractor.FitbitExtractor;
import com.lily.extractor.Extractor;
import com.lily.extractor.ExtractorRequest;
import com.lily.extractor.ExtractorResponse;

import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * Akka Fitbit Profile actor.
 * 
 * @author Mohammad
 *
 */
public class FitbitProfileActor extends UntypedActor {

	public static Props props = Props.create(FitbitProfileActor.class);

	/**
	 * On Receive msg from Scheduler.
	 */
	public void onReceive(Object msg) throws Exception {
		System.out.println("Msg received: "+msg);
		
		final Extractor fitbitExtractor = new FitbitExtractor();
		ExtractorResponse response = fitbitExtractor.extract(new ExtractorRequest("4CMMSH", "profile"));
		
		System.out.println(response.getResponse());
		
		//Return data to sender.
		sender().tell(response.getResponse(), self());
	}
}