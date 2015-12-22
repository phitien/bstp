package com.bosch.si.rest;

import com.bosch.si.rest.callback.IServiceCallback;
import com.bosch.si.rest.connection.IServiceConnection;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by sgp0458 on 18/8/15.
 */
public interface IService {

    public static final int READ_TIMEOUT = 10000;
    public static final int CONNECT_TIMEOUT = 15000;
    public static final int REST_UNKNOWN_ERROR = -204;
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_DEFAULT = "application/json";
    public static final String CONTENT_TYPE_FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String SET_COOKIE = "set-cookie";
    public static final String COOKIE = "cookie";
    public static final String AUTHORIZATION = "Authorization";
    public static final String UTF_8 = "UTF-8";

    public enum METHOD {
        GET, POST, PUT, DELETE
    }

    public String getBaseURI();

    public String getURI();

    public IServiceCallback getCallback();

    public String getRequestCookie();

    public int getReadTimeout();

    public int getConnectTimeout();

    public String getBody();

    public String getQueryString();

    public String getContentType();

    public METHOD getMethod();

    public IServiceConnection getConnection();

    public Executor getExecutor();

    public String getAuthorization();

    public Map<String, String> getHeaders();

    public String getHeader(String key);

    public String getResponseString();

    public String getResponseCookie();

    public int getResponseCode();

    public boolean isOK();

    public void executeAsync(IServiceCallback callback);

    public String executeSync(IServiceCallback callback);

}
