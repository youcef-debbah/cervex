package com.rhcloud.cervex_jsoftware95.control;

import java.io.IOException;
import java.util.ArrayList;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.util.Constants;

import com.rhcloud.cervex_jsoftware95.util.WebKit;

public class LoginForwardListener implements PhaseListener {

	private static final long serialVersionUID = 2735131174374130549L;

	private static Logger log = Logger.getLogger(LoginForwardListener.class);
	private static String loginViewId = "/login.xhtml";

	@Override
	public void beforePhase(final PhaseEvent event) {
		final FacesContext ctx = FacesContext.getCurrentInstance();
		if (!ctx.getPartialViewContext().isAjaxRequest() || ctx.getRenderResponse()) {
			return;
		}

		final ExternalContext externalContext = ctx.getExternalContext();
		final HttpServletRequest request = HttpServletRequest.class.cast(externalContext.getRequest());

		String path = request.getServletPath();

		if (request.getDispatcherType() == DispatcherType.FORWARD) {
			if (request.getParameterMap().get(Constants.DIALOG_FRAMEWORK.CONVERSATION_PARAM) != null) {
				RequestContext.getCurrentInstance().closeDialog(null);
			} else if (loginViewId.equals(path)) {
				forwardToLogin(ctx);
			}
		}
	}

	private void forwardToLogin(FacesContext ctx) {
		ArrayList<String> parameters = new ArrayList<>(2);

		String nextView = WebKit.getOriginalURL(ctx, false);
		if (nextView != null)
			parameters.add("nextView=" + nextView);

		UIViewRoot viewRoot = ctx.getViewRoot();
		if (viewRoot == null) {
			log.info("creating new view root");
			ctx.setViewRoot(ctx.getApplication().getViewHandler().createView(ctx, loginViewId));
			parameters.add("isNewView=true");
		}

		ExternalContext externalContext = ctx.getExternalContext();
		String url = WebKit.getURL(externalContext.getRequestContextPath(), loginViewId, parameters);
		log.info("forwarding to: " + url);

		try {
			externalContext.redirect(url);
		} catch (final IOException e) {
			log.error("failed to redirect to: " + url, e);
		}
	}

	@Override
	public void afterPhase(PhaseEvent event) {
		UIViewRoot viewRoot = event.getFacesContext().getViewRoot();
		log.info("view restored: " + (viewRoot == null ? viewRoot : viewRoot.getViewId()));
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

}
