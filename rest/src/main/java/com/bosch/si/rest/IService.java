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
    public static final String DEFAULT_CONTENT_TYPE = "application/json";
    public static final String SET_COOKIE = "set-cookie";
    public static final String COOKIE = "cookie";
    public static final String AUTHORIZATION = "Authorization";
    public static final String UTF_8 = "UTF-8";

    public enum METHOD {
        GET, POST, PUT, DELETE
    }

    public String getBaseURI();

    public void setBaseURI(String URI);

    public String getURI();

    public void setURI(String URI);

    public IServiceCallback getCallback();

    public void setCallback(IServiceCallback callback);

    public String getRequestCookie();

    public void setRequestCookie(String cookie);

    public int getReadTimeout();

    public void setReadTimeout(int timeout);

    public int getConnectTimeout();

    public void setConnectTimeout(int timeout);

    public String getBody();

    public void setBody(String body);

    public String getQueryString();

    public void setQueryString(String queryString);

    public String getContentType();

    public void setContentType(String contentType);

    public METHOD getMethod();

    public void setMethod(METHOD method);

    public IServiceConnection getConnection();

    public void setConnection(IServiceConnection connection);

    public Executor getExecutor();

    public void setExecutor(Executor executor);

    public String getAuthorization();

    public void setAuthorization(String authorization);

    public Map<String, String> getHeaders();

    public void setHeaders(Map<String, String> headers);

    public void addHeader(String key, String value);

    public void removeHeader(String key);

    public String getHeader(String key);

    public String getResponseString();

    public String getResponseCookie();

    public int getResponseCode();

    public boolean isOK();

    public void executeAsync(IServiceCallback callback);

    public String executeSync(IServiceCallback callback);

}
