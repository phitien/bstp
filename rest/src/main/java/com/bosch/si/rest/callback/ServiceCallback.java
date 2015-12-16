package com.bosch.si.rest.callback;

import com.bosch.si.rest.IService;

/**
 * Created by sgp0458 on 29/8/15.
 */
public abstract class ServiceCallback implements IServiceCallback {

    @Override
    public void onProgressUpdate(IService service) {

    }

    @Override
    public void onPreExecute(IService service) {
    }

    @Override
    public void onPostExecute(IService service) {
    }

    @Override
    public void onSessionExpiry(IService service) {
    }
}
