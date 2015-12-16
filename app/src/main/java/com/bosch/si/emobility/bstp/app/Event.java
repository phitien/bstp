package com.bosch.si.emobility.bstp.app;

import com.bosch.si.emobility.bstp.helper.Utils;

import de.greenrobot.event.EventBus;

/**
 * Created by sgp0458 on 8/12/15.
 */
public class Event {

    protected String type;
    protected String message;
    protected Object reference;

    public Event() {
        type = "MESSAGE";
        message = "";
        reference = null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getReference() {
        return reference;
    }

    public void setReference(Object reference) {
        this.reference = reference;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param message
     */
    public static void broadcast(String message) {
        try {
            Event event = new Event();
            event.setMessage(message);
            EventBus.getDefault().post(event);
        } catch (Exception e) {
            Utils.Log.e("BSTP_Event_broadcastMessage", e.getMessage());
        }
    }

    /**
     * @param message
     * @param type
     */
    public static void broadcast(String message, String type) {
        try {
            Event event = new Event();
            event.setMessage(message);
            event.setType(type);
            EventBus.getDefault().post(event);
        } catch (Exception e) {
            Utils.Log.e("BSTP_Event_broadcastMessage", e.getMessage());
        }
    }
}
