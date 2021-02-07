package com.rhcloud.cervex_jsoftware95.beans;

import java.util.List;
import java.util.Objects;

import javax.ejb.EJBException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.rhcloud.cervex_jsoftware95.entities.File;
import com.rhcloud.cervex_jsoftware95.exceptions.InformationSystemException;

public class FileManagerBean implements FileManager {
	private static Logger log = Logger.getLogger(FileManagerBean.class);
	
	@PersistenceContext(unitName = "cervex")
	EntityManager em;

	@Override
	public File getFile(String fileID) {
		try {
			Objects.requireNonNull(fileID);
			log.debug("trying to retreive file with ID :" + fileID);
			File f = em.find(File.class, fileID);
			if (f != null)
				return f;
			else
				throw new InformationSystemException("Can't find file with ID :" + fileID);
		} catch (NullPointerException e) {
			throw new NullPointerException("file is null");
		} catch (Exception e) {
			throw new EJBException("Can't retreive the file :" + fileID, e);
		}
	}
	

	@Override
	public List<File> getAllFiles() {
		try {
			return em.createNamedQuery("File.findAll", File.class).getResultList();
		} catch (Exception e) {
			throw new EJBException("Error during retreiving Files", e);
		}
	}
	
	@Override
	public void removeFile(String fileID) {
		try {
			Objects.requireNonNull(fileID);
			File file = em.find(File.class, fileID);
			if (file != null)
				em.remove(file);
			else
				throw new InformationSystemException("the file with ID :" + fileID + " not found");
		} catch (NullPointerException e) {
			throw new NullPointerException("the given ID was null");
		} catch (InformationSystemException e) {
			throw e;
		} catch (Exception e) {
			throw new EJBException("Somthing happened during deleting file " + fileID, e);
		}

	}
	
}
