package org.activityinfo.ui.full.client.importer.binding;

public class InstanceMatch {


    private boolean newInstance;
    private String instanceId;

    public boolean isNewInstance() {
        return newInstance;
    }

    public void setNewInstance(boolean newInstance) {
        this.newInstance = newInstance;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public String toString() {
        if (newInstance) {
            return "new:" + instanceId;
        } else {
            return "existing:" + instanceId;
        }
    }

    public static InstanceMatch existing(int id) {
        InstanceMatch match = new InstanceMatch();
        match.setInstanceId("" + id);
        match.setNewInstance(false);
        return match;
    }
}
