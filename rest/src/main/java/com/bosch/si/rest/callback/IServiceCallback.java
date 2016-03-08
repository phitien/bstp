package com.bosch.si.rest.callback;

import com.bosch.si.rest.AbstractService;
import com.bosch.si.rest.IService;

/**
 * Created by sgp0458 on 18/8/15.
 */
public interface IServiceCallback {
    void onPreExecute(IService service);

    void success(IService service);

    void failure(IService service);

    void onProgressUpdate(IService service);

    void onPostExecute(IService service);

    void onUnauthorized(IService service);

    void timeout(IService service);
}
