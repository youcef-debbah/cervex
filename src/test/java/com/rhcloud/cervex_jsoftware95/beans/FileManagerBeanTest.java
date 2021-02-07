package com.rhcloud.cervex_jsoftware95.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJBException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;

import com.rhcloud.cervex_jsoftware95.entities.File;
import com.rhcloud.cervex_jsoftware95.exceptions.InformationSystemException;

public class FileManagerBeanTest {

    private FileManagerBean fileManagerBean;
    @PersistenceContext
    private EntityManager em; // Mocked EntityManager

    @Before
    public void setUp() {
        fileManagerBean = new FileManagerBean();
        em = mock(EntityManager.class);
        // Inject mock into bean
        fileManagerBean.em = em;
    }

    @Test
    public void testGetFile_validId_returnsFile() {
        String fileId = "testFileId";
        File expectedFile = new File();
        expectedFile.setFileID(fileId);
        when(em.find(File.class, fileId)).thenReturn(expectedFile);

        File actualFile = fileManagerBean.getFile(fileId);

        assertEquals(expectedFile, actualFile);
    }

    @Test
    public void testGetFile_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> fileManagerBean.getFile(null));
    }

    @Test
    public void testGetFile_noFileFound_throwsInformationSystemException() {
        String fileId = "testFileId";
        when(em.find(File.class, fileId)).thenReturn(null);

        assertThrows(InformationSystemException.class, () -> fileManagerBean.getFile(fileId));
    }

    @Test
    public void testGetAllFiles_returnsListOfFiles() {
        List<File> expectedFiles = new ArrayList<>();
        expectedFiles.add(new File());
        expectedFiles.add(new File());
        when(em.createNamedQuery("File.findAll", File.class)).thenReturn((TypedQuery<File>) mock(Query.class));
        when(mock(javax.persistence.Query.class).getResultList()).thenReturn(expectedFiles);

        List<File> actualFiles = fileManagerBean.getAllFiles();

        assertEquals(expectedFiles, actualFiles);
    }

    @Test
    public void testGetAllFiles_throwsEJBException_onPersistenceError() {
        when(em.createNamedQuery("File.findAll", File.class)).thenThrow(new RuntimeException());

        assertThrows(EJBException.class, () -> fileManagerBean.getAllFiles());
    }

    @Test
    public void testRemoveFile_validId_removesFile() {
        String fileId = "testFileId";
        File file = new File();
        file.setFileID(fileId);
        when(em.find(File.class, fileId)).thenReturn(file);

        fileManagerBean.removeFile(fileId);

        verify(em).remove(file);
    }

    @Test
    public void testRemoveFile_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> fileManagerBean.removeFile(null));
    }

    @Test
    public void testRemoveFile_noFileFound_throwsInformationSystemException() {
        String fileId = "testFileId";
        when(em.find(File.class, fileId)).thenReturn(null);

        assertThrows(InformationSystemException.class, () -> fileManagerBean.removeFile(fileId));
    }

    @Test
    public void testRemoveFile_throwsEJBException_onPersistenceError() {
        String fileId = "testFileId";
        File file = new File();
        file.setFileID(fileId);
        when(em.find(File.class, fileId)).thenReturn(file);
        doThrow(new RuntimeException()).when(em).remove(file);

        assertThrows(EJBException.class, () -> fileManagerBean.removeFile(fileId));
    }
}