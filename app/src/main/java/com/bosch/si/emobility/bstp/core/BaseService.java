package com.bosch.si.emobility.bstp.core;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.service.LoginService;
import com.bosch.si.rest.AbstractService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.IServiceCallback;
import com.bosch.si.rest.callback.ServiceCallback;

import org.json.JSONObject;
import org.json.XML;

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
            public void onUnauthorized(IService service) {
                UserSessionManager.getInstance().setSessionExpired(true);
                if (UserSessionManager.getInstance().isSaveCredentials()) {//if save credentials
                    doReLogin(service);
                } else {
                    UserSessionManager.getInstance().clearUserSession();
                    Event.broadcast(Utils.getString(R.string.session_expired), Constants.EventType.SESSION_EXPIRED.toString());
                }
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

    protected void doReLogin(final IService executingService) {
        Utils.Indicator.show();

        final User user = UserSessionManager.getInstance().getUser();
        LoginService loginService = new LoginService();
        loginService.user = user;

        loginService.executeAsync(new ServiceCallback() {
            @Override
            public void success(IService service) {
                //Save Authorization Cookie
                try {
                    JSONObject jsonObj = XML.toJSONObject(service.getResponseString());
                    user.setContextId(jsonObj.getJSONObject("ns2:identityContext").getString("ns2:contextId"));
                    UserSessionManager.getInstance().setUserSession(user);
                    if (!(executingService instanceof LoginService))
                        executingService.redoOnce();
                } catch (Exception e) {
                    UserSessionManager.getInstance().clearUserSession();
                }
            }

            @Override
            public void failure(IService service) {
                UserSessionManager.getInstance().clearUserSession();
            }

            @Override
            public void onPostExecute(IService service) {
                Utils.Indicator.hide();
            }
        });
    }

    @Override
    public Map<String, String> getHeaders() {
        headers.put("x-im-context-id", UserSessionManager.getInstance().getUser().getContextId());
        return super.getHeaders();
    }
}
