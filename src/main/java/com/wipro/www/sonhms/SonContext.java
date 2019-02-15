/*******************************************************************************
 * ============LICENSE_START=======================================================
 * pcims
 *  ================================================================================
 *  Copyright (C) 2018 Wipro Limited.
 *  ==============================================================================
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   ============LICENSE_END=========================================================
 ******************************************************************************/

package com.wipro.www.sonhms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class SonContext implements SonState {
    private SonState pciState;
    boolean notifToBeProcessed;
    private String sdnrNotification;
    private long childThreadId;
    private Map<Long, String> childStatus;
    private BlockingQueue<List<String>> childStatusUpdate;
    private NewNotification newNotification;

    public String getSdnrNotification() {
        return sdnrNotification;
    }

    public void setSdnrNotification(String sdnrNotification) {
        this.sdnrNotification = sdnrNotification;
    }

    SonContext(SonState pciState) {
        this.pciState = pciState;
        this.childStatus = new HashMap<>();
    }

    public SonContext() {

    }

    /**
     * Parameterized constructor.
     */
    public SonContext(BlockingQueue<List<String>> childStatusUpdate, NewNotification newNotification) {
        this.setChildStatusUpdate(childStatusUpdate);
        this.setNewNotification(newNotification);
        this.childStatus = new HashMap<>();
    }

    public SonState getPciState() {
        return pciState;
    }

    public long getChildThreadId() {
        return childThreadId;
    }

    public void setChildThreadId(long childThreadId) {
        this.childThreadId = childThreadId;
    }

    public void setPciState(SonState pciState) {
        this.pciState = pciState;
    }

    public boolean isNotifToBeProcessed() {
        return notifToBeProcessed;
    }

    public void setNotifToBeProcessed(boolean notifToBeProcessed) {
        this.notifToBeProcessed = notifToBeProcessed;
    }

    @Override
    public void stateChange(SonContext pciContext) {
        this.pciState.stateChange(pciContext);
    }

    public BlockingQueue<List<String>> getChildStatusUpdate() {
        return childStatusUpdate;
    }

    public void setChildStatusUpdate(BlockingQueue<List<String>> childStatusUpdate) {
        this.childStatusUpdate = childStatusUpdate;
    }

    public void addChildStatus(Long threadId, String status) {
        this.childStatus.put(threadId, status);
    }

    public String getChildStatus(Long threadId) {
        return childStatus.get(threadId);

    }

    public NewNotification getNewNotification() {
        return newNotification;
    }

    public void setNewNotification(NewNotification newNotification) {
        this.newNotification = newNotification;
    }

    public void deleteChildStatus() {
        this.childStatus.remove(childThreadId);

    }

}
