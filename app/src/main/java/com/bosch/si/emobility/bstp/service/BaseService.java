package com.bosch.si.emobility.bstp.service;

import com.bosch.si.emobility.bstp.helper.Constants;
import com.bosch.si.rest.AbstractService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.IServiceCallback;
import com.bosch.si.rest.callback.ServiceCallback;

public class BaseService extends AbstractService {

    @Override
    public String getBaseURI() {
        return Constants.BASE_URL;
    }

    @Override
    public IServiceCallback getCallback() {
        if (callback == null)
            callback = new ServiceCallback() {
                @Override
                public void success(IService service) {

                }

                @Override
                public void failure(IService service) {

                }

                @Override
                public void onSessionExpiry(IService service) {
                    //TODO re-login

                }
            };
        return callback;
    }

}
