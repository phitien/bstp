package com.bosch.si.emobility.bstp.core;

import com.bosch.si.emobility.bstp.R;
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
    public int getConnectTimeout() {
        return Constants.REQUEST_TIMEOUT;
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
            public void onPostExecute(IService service) {
                Utils.Indicator.hide();//remove indicator anw
            }

            @Override
            public void onUnauthorized(IService service) {
                Event.broadcast(Utils.getString(R.string.session_expired), Constants.EventType.SESSION_EXPIRED.toString());
            }

            @Override
            public void error(IService service) {
                Utils.Notifier.notify(Utils.getString(R.string.restful_service_error_message));
            }

            @Override
            public void timeout(IService service) {
                Utils.Notifier.notify(Utils.getString(R.string.request_timeout_message));
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
