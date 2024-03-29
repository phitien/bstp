package com.bosch.si.rest.connection;

import java.io.InputStream;
import java.io.OutputStream;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by sgp0458 on 20/11/15.
 */
public interface IServiceConnection {

    boolean isSecure();

    HttpsURLConnection getURLConnection();

    void connect();

    void disconnect();

    int getResponseCode();

    String getResponseMessage();

    InputStream getInputStream();

    OutputStream getOutputStream();
}
