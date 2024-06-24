
package com.rhcloud.cervex_jsoftware95.control;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;

/**
 * Application Lifecycle Listener implementation class VisitorsCounter
 *
 */
@WebListener("keep track of the current visitors number")
public class VisitorsCounter implements HttpSessionListener {

	private static Logger log = Logger.getLogger(VisitorsCounter.class);

	private static final Object concurrenceKey = new Object();

	private static int currentVisitors;

	/**
	 * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent se) {
		int count;
		synchronized (concurrenceKey) {
			count = ++currentVisitors;
		}
		publish(count);
		log.info("current visitors = " + count);
	}

	/**
	 * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent se) {
		int count;
		synchronized (concurrenceKey) {
			count = --currentVisitors;
		}
		publish(count);
		log.info("current visitors = " + count);
	}

	private void publish(int count) {
		EventBusFactory eventBusFactory = EventBusFactory.getDefault();
		if (eventBusFactory != null) {
			EventBus eventBus = eventBusFactory.eventBus();
			eventBus.publish(CounterResource.COUNTER_CHANNEL, String.valueOf(count));
		} else {
			log.error("null EventBusFactory");
		}
	}

	public static int getCurrentVisitors() {
		return currentVisitors;
	}

}
