package com.rhcloud.cervex_jsoftware95.beans;

import com.rhcloud.cervex_jsoftware95.entities.Demand;
import com.rhcloud.cervex_jsoftware95.entities.User;
import com.rhcloud.cervex_jsoftware95.exceptions.InformationSystemException;
import org.junit.Before;
import org.junit.Test;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationManagerBeanTest {

    private NotificationManagerBean notificationManagerBean;
    @PersistenceContext
    private EntityManager em; // Mocked EntityManager
    @EJB
    private UserManager um; // Mocked UserManager

    @Before
    public void setUp() {
        notificationManagerBean = new NotificationManagerBean();
        em = mock(EntityManager.class);
        um = mock(UserManager.class);
        // Inject mocks into bean
        notificationManagerBean.em = em;
        notificationManagerBean.um = um;
    }

    @Test
    public void testGetNewDemands_returnsNewDemands() {
        List<Demand> expectedDemands = new ArrayList<>();
        Query mockQuery = mock(Query.class);
        when(mockQuery.setParameter("state", Demand.NEW_DEMAND)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(expectedDemands);

        List<Demand> actualDemands = notificationManagerBean.getNewDemands();

        assertEquals(expectedDemands, actualDemands);
    }

    @Test
    public void testGetNewDemands_persistenceError_throwsInformationSystemException() {
        when(em.createQuery("Select d from Demand d where d.state=:state", Demand.class)).thenThrow(new RuntimeException());

        assertThrows(InformationSystemException.class, () -> notificationManagerBean.getNewDemands());
    }

    // Similar tests can be written for other methods following the same approach:
    //  - getNewComments
    //  - getNewArticles
    //  - getUpdatedArticles
    //  - getNewReceivedMessages

    @Test
    public void testGetNewDemands_withUsername_validData_returnsNewDemands() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        List<Demand> expectedDemands = new ArrayList<>();
        Query mockQuery = mock(Query.class);
        when(um.getUser(username)).thenReturn(user);
        when(mockQuery.setParameter("user", user)).thenReturn(mockQuery);
        when(mockQuery.setParameter("state", Demand.NEW_DEMAND)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(expectedDemands);

        List<Demand> actualDemands = notificationManagerBean.getNewDemands(username);

        assertEquals(expectedDemands, actualDemands);
    }

    @Test
    public void testGetNewDemands_withUsername_nullUsername_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> notificationManagerBean.getNewDemands(null));
    }

    @Test
    public void testGetNewDemands_withUsername_userManagerThrowsException_throwsOriginalException() {
        String username = "testUser";
        when(um.getUser(username)).thenThrow(new InformationSystemException());

        assertThrows(InformationSystemException.class, () -> notificationManagerBean.getNewDemands(username));
    }
}
