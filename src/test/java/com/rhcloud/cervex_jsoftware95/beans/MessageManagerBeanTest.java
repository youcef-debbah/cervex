package com.rhcloud.cervex_jsoftware95.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;

import com.rhcloud.cervex_jsoftware95.entities.Message;
import com.rhcloud.cervex_jsoftware95.entities.User;
import com.rhcloud.cervex_jsoftware95.exceptions.InformationSystemException;

public class MessageManagerBeanTest {

    private MessageManagerBean messageManagerBean;
    @PersistenceContext
    private EntityManager em; // Mocked EntityManager
    @EJB
    private UserManager userManager; // Mocked UserManager

    @Before
    public void setUp() {
        messageManagerBean = new MessageManagerBean();
        em = mock(EntityManager.class);
        userManager = mock(UserManager.class);
        // Inject mocks into bean
        messageManagerBean.em = em;
        messageManagerBean.userManager = userManager;
    }

    @Test
    public void testSendMessage_validData_sendsMessage() {
        String senderUsername = "sender";
        String receiverUsername = "receiver";
        String title = "Test Message Title";
        String text = "Test Message Text";

        User sender = new User();
        User receiver = new User();
        Message message = new Message();

        when(userManager.getUser(senderUsername)).thenReturn(sender);
        when(userManager.getUser(receiverUsername)).thenReturn(receiver);

        messageManagerBean.sendMessage(senderUsername, receiverUsername, title, text);

        verify(message).setSender(sender);
        verify(message).setReceiver(receiver);
        verify(message).setTitle(title);
        verify(message).setText(text);
        verify(em).persist(message);
    }

    @Test
    public void testSendMessage_nullSenderUsername_throwsNullPointerException() {
        String receiverUsername = "receiver";
        String title = "Test Message Title";
        String text = "Test Message Text";

        assertThrows(NullPointerException.class, () -> messageManagerBean.sendMessage(null, receiverUsername, title, text));
    }

    @Test
    public void testSendMessage_nullReceiverUsername_throwsNullPointerException() {
        String senderUsername = "sender";
        String title = "Test Message Title";
        String text = "Test Message Text";

        assertThrows(NullPointerException.class, () -> messageManagerBean.sendMessage(senderUsername, null, title, text));
    }

    @Test
    public void testSendMessage_nullTitle_throwsNullPointerException() {
        String senderUsername = "sender";
        String receiverUsername = "receiver";
        String text = "Test Message Text";

        assertThrows(NullPointerException.class, () -> messageManagerBean.sendMessage(senderUsername, receiverUsername, null, text));
    }

    @Test
    public void testSendMessage_nullText_throwsNullPointerException() {
        String senderUsername = "sender";
        String receiverUsername = "receiver";
        String title = "Test Message Title";

        assertThrows(NullPointerException.class, () -> messageManagerBean.sendMessage(senderUsername, receiverUsername, title, null));
    }

    @Test
    public void testSendMessage_userManagerThrowsException_throwsEJBException() {
        String senderUsername = "sender";
        String receiverUsername = "receiver";
        String title = "Test Message Title";
        String text = "Test Message Text";

        when(userManager.getUser(senderUsername)).thenThrow(new InformationSystemException());

        assertThrows(EJBException.class, () -> messageManagerBean.sendMessage(senderUsername, receiverUsername, title, text));
    }
}
