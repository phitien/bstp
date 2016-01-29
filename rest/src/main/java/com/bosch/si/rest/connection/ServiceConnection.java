package com.bosch.si.rest.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by sgp0458 on 20/11/15.
 */
public class ServiceConnection implements IServiceConnection {

    protected boolean secure = false;

    protected URLConnection connection;

    protected HttpsURLConnection httpsURLConnection;

    protected HttpURLConnection httpURLConnection;

    public ServiceConnection(URLConnection connection) {
        this.connection = connection;
        if (connection instanceof HttpsURLConnection) {
            this.httpsURLConnection = (HttpsURLConnection) connection;
            this.secure = true;
        } else if (connection instanceof HttpURLConnection) {
            this.httpURLConnection = (HttpURLConnection) connection;
            this.secure = false;
        }
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public HttpsURLConnection getURLConnection() {
        if (secure) //{
            return httpsURLConnection;
//        } else if (httpURLConnection != null) {
//            return httpURLConnection;
//        }
        return null;
    }

    @Override
    public void connect() {
        try {
            if (secure) {
                httpsURLConnection.connect();
            } else if (httpURLConnection != null) {
                httpURLConnection.connect();
            }
        } catch (IOException e) {
            System.out.print("REST_ServiceConnection_connect:" + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        if (secure) {
            httpsURLConnection.disconnect();
        } else if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
    }

    @Override
    public int getResponseCode() {
        try {
            if (secure) {
                return httpsURLConnection.getResponseCode();
            } else if (httpURLConnection != null) {
                return httpURLConnection.getResponseCode();
            }
        } catch (IOException e) {
            System.out.print("REST_ServiceConnection_getResponseCode:" + e.getMessage());
        }
        return -1;
    }

    @Override
    public String getResponseMessage() {
        try {
            if (secure) {
                return httpsURLConnection.getResponseMessage();
            } else if (httpURLConnection != null) {
                return httpURLConnection.getResponseMessage();
            }
        } catch (IOException e) {
            System.out.print("REST_ServiceConnection_getResponseMessage:" + e.getMessage());
        }
        return null;
    }

    @Override
    public InputStream getInputStream() {
        try {
            if (secure) {
                return httpsURLConnection.getInputStream();
            } else if (httpURLConnection != null) {
                return httpURLConnection.getInputStream();
            }
        } catch (IOException e) {
            System.out.print("REST_ServiceConnection_getInputStream:" + e.getMessage());
        }
        return null;
    }

    @Override
    public OutputStream getOutputStream() {
        try {
            if (secure) {
                return httpsURLConnection.getOutputStream();
            } else if (httpURLConnection != null) {
                return httpURLConnection.getOutputStream();
            }
        } catch (IOException e) {
            System.out.print("REST_ServiceConnection_getInputStream:" + e.getMessage());
        }
        return null;
    }
}
