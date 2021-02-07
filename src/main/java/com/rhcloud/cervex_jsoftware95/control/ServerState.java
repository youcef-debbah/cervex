package com.rhcloud.cervex_jsoftware95.control;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@WebServlet(urlPatterns = "/state")
public class ServerState extends HttpServlet {

	private static final long serialVersionUID = -2679216866062797555L;

	private static final Logger log = Logger.getLogger(ServerState.class);

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("Server state check");
		String currentState = "Server up";
		resp.getWriter().write("<!doctype html>\r\n<html>\r\n<head>\r\n\t<title>Server state</title>\r\n</head>\r\n"
				+ "<body>\r\n\t<h1>" + currentState + "</h1>\r\n</body>\r\n</html>");
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
