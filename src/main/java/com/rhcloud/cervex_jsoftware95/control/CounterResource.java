package com.rhcloud.cervex_jsoftware95.control;

import org.primefaces.push.annotation.OnMessage;
import org.primefaces.push.annotation.PushEndpoint;
import org.primefaces.push.impl.JSONDecoder;
import org.primefaces.push.impl.JSONEncoder;

@PushEndpoint(CounterResource.COUNTER_CHANNEL)
public class CounterResource {

	public static final String COUNTER_CHANNEL = "/counter";

	@OnMessage(encoders = { JSONEncoder.class }, decoders = { JSONDecoder.class })
	public String onMessage(String count) {
		return count;
	}
}