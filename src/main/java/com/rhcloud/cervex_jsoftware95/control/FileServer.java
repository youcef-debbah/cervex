package com.rhcloud.cervex_jsoftware95.control;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.rhcloud.cervex_jsoftware95.beans.BlogManager;
import com.rhcloud.cervex_jsoftware95.entities.File;

@WebServlet(urlPatterns = FileServer.FILE_SERVER_PATTERN)
public class FileServer extends HttpServlet {

	private static final long serialVersionUID = -8546311766603470621L;
	private static final Logger log = Logger.getLogger(FileServer.class);

	public static final String FILE_SERVER_PATTERN = "/file";
	public static final String FILE_ID = "id";
	
	@EJB
	private BlogManager blogManager;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileID = request.getParameter(FILE_ID);
		if (fileID != null) {
			try {
				File file = blogManager.getFile(fileID);
				response.setContentType(file.getContentType());
				response.getOutputStream().write(file.getContents());	
			} catch (Exception e) {
				log.error("could not serve the file: " + fileID, e);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
