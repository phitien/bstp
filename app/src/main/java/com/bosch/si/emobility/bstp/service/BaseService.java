package com.bosch.si.emobility.bstp.service;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.UserSessionManager;
import com.bosch.si.emobility.bstp.app.Event;
import com.bosch.si.emobility.bstp.helper.Constants;
import com.bosch.si.emobility.bstp.helper.Utils;
import com.bosch.si.rest.AbstractService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.IServiceCallback;
import com.bosch.si.rest.callback.ServiceCallback;

import java.util.Map;

public class BaseService extends AbstractService {

    @Override
    public String getBaseURI() {
        baseURI = Constants.BASE_URL;
        return super.getBaseURI();
    }

    @Override
    public IServiceCallback getCallback() {
        callback = new ServiceCallback() {
            @Override
            public void success(IService service) {

            }

            @Override
            public void failure(IService service) {

            }

            @Override
            public void onSessionExpiry(IService service) {
                Event.broadcast(Utils.getString(R.string.session_expired), Constants.EventType.SESSION_EXPIRED.toString());
            }
        };
        return super.getCallback();
    }

    @Override
    public Map<String, String> getHeaders() {
        headers.put("x-im-context-id", UserSessionManager.getInstance().getUser().getContextId());
        return super.getHeaders();
    }
}
