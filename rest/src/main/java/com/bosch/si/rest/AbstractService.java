package com.bosch.si.rest;

import android.os.AsyncTask;

import com.bosch.si.rest.callback.IServiceCallback;
import com.bosch.si.rest.connection.IServiceConnection;
import com.bosch.si.rest.connection.ServiceConnection;
import com.bosch.si.rest.method.DELETE;
import com.bosch.si.rest.method.GET;
import com.bosch.si.rest.method.POST;
import com.bosch.si.rest.method.PUT;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by sgp0458 on 18/8/15.
 */
public abstract class AbstractService implements IService {

    protected String baseURI;

    protected String URI;

    protected IServiceCallback callback;

    protected String requestCookie = null;

    protected int readTimeout = READ_TIMEOUT;

    protected int connectTimeout = CONNECT_TIMEOUT;

    protected String body = null;

    protected String queryString = "";

    protected String contentType = "text/html";

    protected METHOD method = METHOD.GET;

    protected IServiceConnection connection;

    protected Executor executor = AsyncTask.SERIAL_EXECUTOR;

    protected String responseString = null;

    protected String responseCookie = null;

    protected int responseCode = -1;

    protected String servicePath = "";

    @Override
    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    @Override
    public String getURI() {
        if (URI == null) {
            updateParams();
        }
        return URI;
    }

    @Override
    public void setURI(String URI) {
        this.URI = URI;
    }

    @Override
    public IServiceCallback getCallback() {
        if (callback == null)
            callback = new IServiceCallback() {
                @Override
                public void onPreExecute(IService service) {

                }

                @Override
                public void success(IService service) {

                }

                @Override
                public void failure(IService service) {

                }

                @Override
                public void onProgressUpdate(IService service) {

                }

                @Override
                public void onPostExecute(IService service) {

                }

                @Override
                public void onSessionExpiry(IService service) {

                }
            };
        return callback;
    }

    @Override
    public void setCallback(IServiceCallback callback) {
        this.callback = callback;
    }

    @Override
    public String getRequestCookie() {
        return requestCookie;
    }

    @Override
    public void setRequestCookie(String requestCookie) {
        this.requestCookie = requestCookie;
    }

    @Override
    public int getReadTimeout() {
        return readTimeout;
    }

    @Override
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public METHOD getMethod() {
        return method;
    }

    @Override
    public void setMethod(METHOD method) {
        this.method = method;
    }

    @Override
    public IServiceConnection getConnection() {
        if (connection == null) {
            connection = getServiceConnection();
        }
        return connection;
    }

    @Override
    public void setConnection(IServiceConnection connection) {
        this.connection = connection;
    }

    @Override
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public String getResponseString() {
        return responseString;
    }

    @Override
    public String getResponseCookie() {
        return responseCookie;
    }

    @Override
    public int getResponseCode() {
        return responseCode;
    }

    private IServiceCallback mergeCallbacks(final IServiceCallback serviceCallback) {
        final IServiceCallback defaultCallback = getCallback();
        if (serviceCallback != null) {
            return new IServiceCallback() {
                @Override
                public void onPreExecute(IService service) {
                    serviceCallback.onPreExecute(service);
                    defaultCallback.onPreExecute(service);
                }

                @Override
                public void success(IService service) {
                    serviceCallback.success(service);
                    defaultCallback.success(service);
                }

                @Override
                public void failure(IService service) {
                    serviceCallback.failure(service);
                    defaultCallback.failure(service);
                }

                @Override
                public void onProgressUpdate(IService service) {
                    serviceCallback.onProgressUpdate(service);
                    defaultCallback.onProgressUpdate(service);
                }

                @Override
                public void onPostExecute(IService service) {
                    serviceCallback.onPostExecute(service);
                    defaultCallback.onPostExecute(service);
                }

                @Override
                public void onSessionExpiry(IService service) {
                    serviceCallback.onSessionExpiry(service);
                    defaultCallback.onSessionExpiry(service);
                }
            };
        } else {
            return defaultCallback;
        }
    }

    @Override
    public final void executeAsync(IServiceCallback serviceCallback) {

        final AbstractService me = this;

        final IServiceCallback callback = mergeCallbacks(serviceCallback);

        AsyncTask<Object, Object, Boolean> task = new AsyncTask<Object, Object, Boolean>() {

            @Override
            protected void onPreExecute() {
                callback.onPreExecute(me);
            }

            @Override
            protected Boolean doInBackground(Object... params) {
                responseString = null;
                BufferedReader reader = null;
                try {
                    IServiceConnection conn = getConnection();
                    conn.connect();
                    //set responseCode
                    responseCode = conn.getResponseCode();
                    if (isOK()) {
                        InputStream inputStream = conn.getInputStream();
                        StringBuilder builder = new StringBuilder();
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line + "\n");
                        }
                        //set responseString
                        responseString = builder.toString();
                        responseCookie = conn.getURLConnection().getHeaderField(SET_COOKIE);
                    } else {
                        responseString = responseCode + ": " + conn.getResponseMessage();
                    }
                    conn.disconnect();

                    if (reader != null) {
                        reader.close();
                    }

                } catch (Exception e) {
                    responseCode = REST_UNKNOWN_ERROR;
                    responseString = responseCode + ": " + e.getMessage();
                }

                return me.isOK();
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                callback.onProgressUpdate(me);
            }

            @Override
            protected void onPostExecute(Boolean isOK) {
                if (isOK()) {
                    callback.success(me);
                } else if (isUnauthorized()) {
                    callback.onSessionExpiry(me);
                } else {
                    callback.failure(me);
                }
                callback.onPostExecute(me);
            }
        };

        task.executeOnExecutor(executor);
    }


    @Override
    public final String executeSync(IServiceCallback serviceCallback) {
        final IService me = this;
        final CountDownLatch signal = new CountDownLatch(1);
        final IServiceCallback callback = mergeCallbacks(serviceCallback);
        AsyncTask<Object, Object, Boolean> task = new AsyncTask<Object, Object, Boolean>() {
            @Override
            protected void onPreExecute() {
                callback.onPreExecute(me);
            }

            @Override
            protected Boolean doInBackground(Object... params) {
                responseString = null;
                BufferedReader reader = null;
                try {
                    IServiceConnection conn = getConnection();
                    conn.connect();
                    //set responseCode
                    responseCode = conn.getResponseCode();
                    if (isOK()) {
                        InputStream inputStream = conn.getInputStream();
                        StringBuilder builder = new StringBuilder();
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line + "\n");
                        }
                        //set responseString
                        responseString = builder.toString();
                        responseCookie = conn.getURLConnection().getHeaderField(SET_COOKIE);
                    } else {
                        responseString = responseCode + ": " + conn.getResponseMessage();
                    }
                    conn.disconnect();

                    if (reader != null) {
                        reader.close();
                    }

                } catch (Exception e) {
                    responseCode = REST_UNKNOWN_ERROR;
                    responseString = responseCode + ": " + e.getMessage();
                }

                return isOK();
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                callback.onProgressUpdate(me);
            }

            @Override
            protected void onPostExecute(Boolean isOK) {
                if (isOK()) {
                    callback.success(me);
                } else if (isUnauthorized()) {
                    callback.onSessionExpiry(me);
                } else {
                    callback.failure(me);
                }
                callback.onPostExecute(me);
                signal.countDown();
            }
        };

        task.executeOnExecutor(executor);

        try {
            signal.await();
        } catch (InterruptedException e) {
            System.out.print("REST_executeSync: " + e.getMessage());
        }
        return responseString;
    }

    @Override
    public void setAuthorization(HttpURLConnection conn) {
        String cookie = getRequestCookie();
        if (cookie != null && !cookie.isEmpty()) {
            conn.setRequestProperty(COOKIE, cookie);
        }
    }

    protected String getFieldValue(String fieldName) {
        try {
            Field field = this.getDeclaredClass().getField(fieldName);
            field.setAccessible(true);
            return String.valueOf(field.get(this));
        } catch (Exception e) {
            System.out.print("REST_AbstractService_getFieldValue: " + e.getMessage());
        }
        return null;
    }

    protected IServiceConnection getServiceConnection() {
        try {
            URL url = new URL(getURI());
            URLConnection conn = url.openConnection();
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
                httpsConn.setReadTimeout(getReadTimeout());
                httpsConn.setConnectTimeout(getConnectTimeout());
                httpsConn.setRequestMethod(getMethod().toString());
                httpsConn.setRequestProperty(CONTENT_TYPE, getContentType());
                httpsConn.setUseCaches(false);
                httpsConn.setInstanceFollowRedirects(false);
                setAuthorization(httpsConn);
                //set params for non-get method
                String body = getBody();
                if (getMethod() != METHOD.GET && body != null && !body.isEmpty()) {
                    byte[] postDataBytes = body.getBytes(UTF_8);
                    httpsConn.setRequestProperty(CONTENT_LENGTH, "" + Integer.toString(postDataBytes.length));
                    httpsConn.setDoInput(true);
                    httpsConn.setDoOutput(true);
                    httpsConn.getOutputStream().write(postDataBytes);
                }
                return new ServiceConnection(httpsConn);
            } else if (conn instanceof HttpURLConnection) {
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setReadTimeout(getReadTimeout());
                httpConn.setConnectTimeout(getConnectTimeout());
                httpConn.setRequestMethod(getMethod().toString());
                httpConn.setRequestProperty(CONTENT_TYPE, getContentType());
                httpConn.setUseCaches(false);
                setAuthorization(httpConn);
                //set params for non-get method
                String body = getBody();
                if (getMethod() != METHOD.GET && body != null && !body.isEmpty()) {
                    byte[] postDataBytes = body.getBytes(UTF_8);
                    httpConn.setRequestProperty(CONTENT_LENGTH, "" + Integer.toString(postDataBytes.length));
                    httpConn.setDoInput(true);
                    httpConn.setDoOutput(true);
                    httpConn.getOutputStream().write(postDataBytes);
                }
                return new ServiceConnection(httpConn);
            } else {
                System.out.print("REST_AbstractService_getURLConnection: " + "Could not find url connection");
            }
        } catch (Exception e) {
            System.out.print("REST_AbstractService_getURLConnection: " + e.getMessage());
        }
        return null;
    }

    protected Class<?> getDeclaredClass() {
        Class<?> klass = getClass();
        if (klass.getName().contains("$")) {
            return klass.getSuperclass();
        }
        return klass;
    }

    //Set servicePath and method, only proceed with the first one
    protected void updateParams() {
        Annotation[] annotations = getDeclaredClass().getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof GET) {
                GET anno = (GET) annotation;
                servicePath = anno.value().trim().replaceFirst("^/", "");
                method = METHOD.GET;
                contentType = anno.contentType();
            } else if (annotation instanceof POST) {
                POST anno = (POST) annotation;
                servicePath = anno.value().trim().replaceFirst("^/", "");
                method = METHOD.POST;
                contentType = anno.contentType();
            } else if (annotation instanceof PUT) {
                PUT anno = (PUT) annotation;
                servicePath = anno.value().trim().replaceFirst("^/", "");
                method = METHOD.PUT;
                contentType = anno.contentType();
            } else if (annotation instanceof DELETE) {
                DELETE anno = (DELETE) annotation;
                servicePath = anno.value().trim().replaceFirst("^/", "");
                method = METHOD.DELETE;
                contentType = anno.contentType();
            }
            break;
        }

        URI = URI == null ? servicePath : URI;

        Pattern p = Pattern.compile("\\:(\\w+)");
        Matcher m = p.matcher(URI);

        while (m.find()) {
            String fieldName = m.group(1);
            String value = getFieldValue(fieldName);
            if (value != null) {
                URI = URI.replaceAll(String.format("\\:%s", fieldName), value);
            } else {
                try {
                    throw new Exception(String.format("Class %s Value for field %s is null", getDeclaredClass().getName(), fieldName));
                } catch (Exception e) {
                    System.out.print("REST_AbstractService_updateParams: " + e.getMessage());
                }
            }
        }

        String getQueryString = getQueryString();
        if (getQueryString != null && !getQueryString.isEmpty()) {
            getQueryString = String.format("?%s", getQueryString);
        } else {
            getQueryString = "";
        }

        if (!URI.toLowerCase().startsWith("http")) {
            String baseURI = getBaseURI();
            if (baseURI != null && !baseURI.isEmpty()) {
                URI = String.format("%s/%s%s", baseURI, URI, getQueryString);
            } else {
                URI = String.format("%s%s", URI, getQueryString);
            }
        }
    }

    @Override
    public boolean isOK() {
        return responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_CREATED;
    }

    protected boolean isUnauthorized() {
        return responseCode == HttpURLConnection.HTTP_UNAUTHORIZED;
    }

}
