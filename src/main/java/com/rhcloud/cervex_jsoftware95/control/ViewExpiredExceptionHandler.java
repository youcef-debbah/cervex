package com.rhcloud.cervex_jsoftware95.control;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.util.Constants;

public class ViewExpiredExceptionHandler extends ExceptionHandlerWrapper {

	private static Logger log = Logger.getLogger(ViewExpiredExceptionHandler.class);

	private ExceptionHandler wrapped;

	public ViewExpiredExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return this.wrapped;
	}

	@Override
	public void handle() throws FacesException {
		FacesContext ctx = FacesContext.getCurrentInstance();

		if (ctx == null || ctx.getResponseComplete()) {
			return;
		}

		Map<String, String> parameters = ctx.getExternalContext().getRequestParameterMap();
//		System.out.println("### param: " + parameters);
		if (parameters.get(Constants.DIALOG_FRAMEWORK.CONVERSATION_PARAM) != null) {
//			System.out.println("### closing dialog"); // TODO
			RequestContext.getCurrentInstance().closeDialog(null);
		}

		Iterator<ExceptionQueuedEvent> iterator = getUnhandledExceptionQueuedEvents().iterator();
		if (iterator != null) {
			while (iterator.hasNext()) {
				ExceptionQueuedEvent event = iterator.next();
				ExceptionQueuedEventContext context = ExceptionQueuedEventContext.class.cast(event.getSource());

				if (context != null) {
					Throwable t = context.getException();

					if (t instanceof ViewExpiredException) {
						try {
							handleViewExpiredException(ViewExpiredException.class.cast(t));
						} finally {
							iterator.remove();
						}
					}

				}
			}
		}
		// At this point, the queue will not contain any ViewExpiredEvents.
		// Therefore, let the parent handle them.
		getWrapped().handle();
	}

	private void handleViewExpiredException(ViewExpiredException vee) {
		if (vee == null)
			return;

		FacesContext ctx = FacesContext.getCurrentInstance();
		Map<String, String> parameters = ctx.getExternalContext().getRequestParameterMap();
		String isNewView = parameters.get("isNewView");

		try {
			if (isNewView != null && Boolean.valueOf(isNewView)) {
				log.info("handling view expired exception");

				Writer writer = ctx.getExternalContext().getResponseOutputWriter();

				String url = ctx.getExternalContext().getRequestContextPath() + vee.getViewId();
				writer.write(getRedirectionPage(url));

				writer.close();
			} else {
				NavigationHandler navigationHandler = ctx.getApplication().getNavigationHandler();
				navigationHandler.handleNavigation(ctx, null, "login");
			}
		} catch (IOException e) {
			log.error("could not handle exception: " + vee.getMessage(), e);
		}
	}

	private String getRedirectionPage(String url) {
		return "<!DOCTYPE html>\n<html>\n<head>\n<title>...</title>\n<meta http-equiv=\"refresh\" content=\"0;url="
				+ url + "\" />\n</head>\n<body>\n</body>\n</html>";
	}

}
