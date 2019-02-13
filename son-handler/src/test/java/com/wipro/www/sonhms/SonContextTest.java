package com.wipro.www.sonhms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.wipro.www.sonhms.SdnrNotificationHandlingState;
import com.wipro.www.sonhms.SonContext;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.junit.Test;



public class SonContextTest {

    private BlockingQueue<List<String>> childStatusUpdate;

    @Test
    public void sonContextTest() {
        SonContext sonContext = new SonContext();
        sonContext.setChildThreadId(1);
        sonContext.setNotifToBeProcessed(true);
        sonContext.setSdnrNotification("notification");
        sonContext.setChildStatusUpdate(childStatusUpdate);
        NewNotification newNotification = new NewNotification(true);
        sonContext.setNewNotification(newNotification);
        SdnrNotificationHandlingState pciState = new SdnrNotificationHandlingState();
        sonContext.setPciState(pciState);
        assertEquals(1, sonContext.getChildThreadId());
        assertTrue(sonContext.isNotifToBeProcessed());
        assertEquals("notification", sonContext.getSdnrNotification());
        assertEquals(pciState, sonContext.getPciState());
        assertEquals(childStatusUpdate, sonContext.getChildStatusUpdate());
        assertEquals(newNotification, sonContext.getNewNotification());

    }
}
