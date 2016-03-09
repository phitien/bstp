package com.bosch.si.emobility.bstp.service;

import android.util.Log;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.core.Constants;
import com.bosch.si.emobility.bstp.core.Event;
import com.bosch.si.emobility.bstp.core.User;
import com.bosch.si.emobility.bstp.core.Utils;
import com.bosch.si.rest.AbstractService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.anno.ContentType;
import com.bosch.si.rest.anno.POST;
import com.bosch.si.rest.callback.IServiceCallback;
import com.bosch.si.rest.callback.ServiceCallback;
import com.google.gson.annotations.Expose;

@ContentType("application/vnd.bosch-com.im+xml")
//@POST("http://ivsrv034.bosch-si.com:8080/im-server/1/rest/authentication")
@POST(Constants.IM_AUTH_BASE_URL)
public class LoginService extends AbstractService {

    @Override
    public String getBaseURI() {
        return Constants.BASE_URL;
    }

    @Expose
    public User user;

    @Override
    public String getBody() {
        body = user.toXMLString();
        return super.getBody();
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

}
