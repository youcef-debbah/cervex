package com.rhcloud.cervex_jsoftware95.beans;

import java.util.List;

import javax.ejb.EJBException;

import com.rhcloud.cervex_jsoftware95.entities.File;
import com.rhcloud.cervex_jsoftware95.exceptions.InformationSystemException;

public interface FileManager {
	
	/**
	 * This methode retreive file with the given {@code fileID}
	 * @param fileID the ID of file you want to retreive
	 * @return File instance
	 * @throws NullPointerException
	 * 			if {@code fileID} is null
	 *  @throws InformationSystemException
	 *  		if the file not found
	 *  @throws EJBException
	 *  		if something happend during retreiving that file
	 */
	public File getFile(String fileID);
	
	public List<File> getAllFiles();
	
	/**
	 * This methode delete file from database
	 * @param fileID the ID of file you to delete
	 * @throws NullPointerException
	 * 			if {@code fileID} was Null
	 * @throws InformationSystemException
	 *			if the file with {@code fileID} not found or already deleted
	 * @throws EJBException
	 * 			if error happened during removing file
	 */
	public void removeFile(String fileID);

}
