package bstp.android.boschsi.com.bstp.app;

import android.content.Intent;

import bstp.android.boschsi.com.bstp.helper.Utils;
import de.greenrobot.event.EventBus;

/**
 * Created by sgp0458 on 8/12/15.
 */
public class Event {
    public enum TYPE {
        MESSAGE
    }

    protected TYPE type;
    protected String message;
    protected Object reference;

    public Event() {
        type = TYPE.MESSAGE;
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

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    /**
     * @param message
     */
    public static void broadcastMessage(String message) {
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
    public static void broadcastMessage(String message, TYPE type) {
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
