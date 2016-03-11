package com.bosch.si.rest;

import com.bosch.si.rest.callback.IServiceCallback;
import com.bosch.si.rest.connection.IServiceConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by sgp0458 on 18/8/15.
 */
public interface IService {

    public static final int REST_ERROR = -1;
    public static final int READ_TIMEOUT = 0;
    public static final int CONNECT_TIMEOUT = 30000;
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_DEFAULT = "application/json";
    public static final String CONTENT_TYPE_FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String SET_COOKIE = "set-cookie";
    public static final String COOKIE = "cookie";
    public static final String AUTHORIZATION = "Authorization";
    public static final String CHARSET = "UTF-8";
    public static final String LINE_FEED = "\r\n";

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

    public IServiceConnection getConnection() throws Exception;

    public Executor getExecutor();

    public String getAuthorization();

    public Map<String, String> getHeaders();

    public String getHeader(String key);

    public Map<String, String> getFormFields();

    public Map<String, File> getFiles();

    public String getResponseString();

    public String getResponseCookie();

    public int getResponseCode();

    public Exception getException();

    public boolean isOK();

    public void executeAsync(IServiceCallback callback);

    public String executeSync(IServiceCallback callback);

    public void redoOnce();

    public void redo();

    public String getFieldValue(String fieldName) throws Exception;

    public InputStream getInputStream();

    public JSONObject getInfo() throws JSONException;

}
