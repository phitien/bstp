package com.bosch.si.rest;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.bosch.si.rest.anno.Authorization;
import com.bosch.si.rest.anno.ContentType;
import com.bosch.si.rest.anno.Cookie;
import com.bosch.si.rest.anno.DELETE;
import com.bosch.si.rest.anno.FormUrlEncoded;
import com.bosch.si.rest.anno.GET;
import com.bosch.si.rest.anno.Header;
import com.bosch.si.rest.anno.POST;
import com.bosch.si.rest.anno.PUT;
import com.bosch.si.rest.anno.QueryParam;
import com.bosch.si.rest.callback.IServiceCallback;
import com.bosch.si.rest.connection.IServiceConnection;
import com.bosch.si.rest.connection.ServiceConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
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

    protected METHOD method = METHOD.GET;

    protected IServiceConnection connection;

    protected Executor executor = AsyncTask.SERIAL_EXECUTOR;

    protected String authorization = null;

    protected Map<String, String> headers = new HashMap<>();

    protected Map<String, String> formFields = new HashMap<>();

    protected Map<String, File> files = new HashMap<>();

    protected String responseString = null;

    protected String responseCookie = null;

    protected int responseCode = REST_ERROR;

    protected String servicePath = "";

    protected Exception exception = null;

    @Override
    public String getBaseURI() {
        return baseURI;
    }

    @Override
    public String getURI() {
        if (URI == null) {
            updateParams();
        }
        return URI;
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
    public String getRequestCookie() {
        return requestCookie;
    }

    @Override
    public int getReadTimeout() {
        return readTimeout;
    }

    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public String getBody() {
        if (method != METHOD.GET && (body == null || body.isEmpty())) {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            return gson.toJson(this).toString();
        }
        return body;
    }

    @Override
    public String getQueryString() {
        if (method == METHOD.GET && (queryString == null || queryString.isEmpty())) {
            String queryString = "";
            Field[] fields = getDeclaredClass().getFields();
            for (Field field : fields) {
                if (isQueryParam(field)) {
                    String value = "";
                    try {
                        value = String.valueOf(field.get(this));
                    } catch (IllegalAccessException e) {
                        exception = e;
                    }
                    queryString += field.getName() + "=" + value + "&";
                }
            }
            return queryString;
        }
        return queryString;
    }

    private boolean isQueryParam(Field field) {
        int modifiers = field.getModifiers();
        if (!Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
            Annotation[] annotations = field.getAnnotations();
            if (annotations.length > 0) {
                return annotations[annotations.length - 1] instanceof QueryParam;
            }
        }
        return false;
    }

    @Override
    public String getContentType() {
        if (URI == null) {
            updateParams();
        }
        return getHeader(CONTENT_TYPE);
    }

    @Override
    public METHOD getMethod() {
        if (URI == null) {
            updateParams();
        }
        return method;
    }

    @Override
    public IServiceConnection getConnection() throws Exception {
        connection = getServiceConnection();
        return connection;
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public String getAuthorization() {
        return authorization;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public Map<String, String> getFormFields() {
        return formFields;
    }

    @Override
    public Map<String, File> getFiles() {
        return files;
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

    @Override
    public final Exception getException() {
        return exception;
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
                doPreExecution(callback, me);
            }

            @Override
            protected Boolean doInBackground(Object... params) {
                return doExecutionInBackground();
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                doProgressUpdate(callback, me);
            }

            @Override
            protected void onPostExecute(Boolean isOK) {
                doPostExecution(callback, me);
            }
        };

        task.executeOnExecutor(executor);
    }

    @Override
    public final String executeSync(IServiceCallback serviceCallback) {
        final AbstractService me = this;
        final CountDownLatch signal = new CountDownLatch(1);
        final IServiceCallback callback = mergeCallbacks(serviceCallback);
        AsyncTask<Object, Object, Boolean> task = new AsyncTask<Object, Object, Boolean>() {
            @Override
            protected void onPreExecute() {
                doPreExecution(callback, me);
            }

            @Override
            protected Boolean doInBackground(Object... params) {
                return doExecutionInBackground();
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                doProgressUpdate(callback, me);
            }

            @Override
            protected void onPostExecute(Boolean isOK) {
                doPostExecution(callback, me);
                signal.countDown();
            }
        };

        task.executeOnExecutor(executor);

        try {
            signal.await();
        } catch (InterruptedException e) {
            exception = e;
        }

        return responseString;
    }

    private void doProgressUpdate(IServiceCallback callback, AbstractService me) {
        try {
            callback.onProgressUpdate(me);
        } catch (Exception e) {
            exception = e;
        }
    }

    private void doPreExecution(IServiceCallback callback, AbstractService me) {
        try {
            callback.onPreExecute(me);
        } catch (Exception e) {
            exception = e;
        }
    }

    @NonNull
    private Boolean doExecutionInBackground() {
        responseString = null;
        BufferedReader reader = null;
        try {
            IServiceConnection conn = getConnection();
            conn.connect();
            //set responseCode
            responseCode = conn.getResponseCode();
            if (isOK()) {
                StringBuilder builder = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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
        } catch (Exception e) {
            exception = e;
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                exception = e;
            }
        }

        return isOK();
    }

    private void doPostExecution(IServiceCallback callback, AbstractService me) {
        try {
            if (isOK()) {
                callback.success(me);
            } else if (isUnauthorized()) {
                callback.onSessionExpiry(me);
            } else {
                callback.failure(me);
            }
            callback.onPostExecute(me);
        } catch (Exception e) {
            exception = e;
        }
        if (exception != null) {
            System.out.print("REST:" + exception.getMessage());
        }
    }

    protected String getFieldValue(String fieldName) throws Exception {
        Field field = this.getDeclaredClass().getField(fieldName);
        field.setAccessible(true);
        return String.valueOf(field.get(this));
    }

    protected IServiceConnection getServiceConnection() throws Exception {
        URL url = new URL(getURI());
        URLConnection conn = url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            updateConnectionParams(httpsConn);
            return new ServiceConnection(httpsConn);
        } else if (conn instanceof HttpURLConnection) {
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            updateConnectionParams(httpConn);
            return new ServiceConnection(httpConn);
        }
        return null;
    }

    protected PrintWriter writer = null;
    protected String boundary = null;
    protected OutputStream outputStream = null;

    private void updateConnectionParams(HttpURLConnection conn) throws Exception {
        conn.setReadTimeout(getReadTimeout());
        conn.setConnectTimeout(getConnectTimeout());
        conn.setRequestMethod(getMethod().toString());
        conn.setUseCaches(false);
        conn.setInstanceFollowRedirects(false);
        //set cookie
        String cookie = getRequestCookie();
        if (cookie != null && !cookie.isEmpty()) {
            headers.put(COOKIE, cookie);
        }
        headers.remove(AUTHORIZATION);
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
        }
        //set params for non-get method
        if (getMethod() != METHOD.GET) {
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //set authorization
            String authorization = getAuthorization();
            if (authorization != null && !authorization.isEmpty()) {
                conn.setRequestProperty(AUTHORIZATION, authorization);
            }
            if ((formFields != null && formFields.size() > 0) || (files != null && files.size() > 0)) {
                boundary = "===" + System.currentTimeMillis() + "===";
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                outputStream = conn.getOutputStream();
                writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET), true);
                setParamsForFormFields(conn);
                setParamsForFiles(conn);
                writer.append(LINE_FEED).flush();
                writer.append("--" + boundary + "--").append(LINE_FEED);
                writer.close();
            } else {
                String body = getBody();
                if (body != null && !body.isEmpty()) {
                    byte[] postDataBytes = body.getBytes(CHARSET);
                    conn.setRequestProperty(CONTENT_LENGTH, "" + Integer.toString(postDataBytes.length));
                    conn.getOutputStream().write(postDataBytes);
                }
            }
        } else {

        }
    }

    protected void setParamsForFormFields(HttpURLConnection conn) {
        for (Map.Entry<String, String> field : formFields.entrySet()) {
            addFormField(conn, field.getKey(), field.getValue());
        }
    }

    protected void addFormField(HttpURLConnection conn, String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + CHARSET).append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    protected void setParamsForFiles(HttpURLConnection conn) throws Exception {
        for (Map.Entry<String, File> field : files.entrySet()) {
            addFile(conn, field.getKey(), field.getValue());
        }
    }

    protected void addFile(HttpURLConnection conn, String fieldName, File uploadFile) throws Exception {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
        writer.append(LINE_FEED);
        writer.flush();
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
        applyAnnotationsValues();
        URI = URI == null ? servicePath : URI;
        applyPublicPropertiesValues();
        applyQueryString();
    }

    protected void applyAnnotationsValues() {
        headers = getHeaders();
        headers.put(CONTENT_TYPE, CONTENT_TYPE_DEFAULT);
        Annotation[] annotations = getDeclaredClass().getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof GET) {
                servicePath = ((GET) annotation).value().trim().replaceFirst("^/", "");
                method = METHOD.GET;
            } else if (annotation instanceof POST) {
                servicePath = ((POST) annotation).value().trim().replaceFirst("^/", "");
                method = METHOD.POST;
            } else if (annotation instanceof PUT) {
                servicePath = ((PUT) annotation).value().trim().replaceFirst("^/", "");
                method = METHOD.PUT;
            } else if (annotation instanceof DELETE) {
                servicePath = ((DELETE) annotation).value().trim().replaceFirst("^/", "");
                method = METHOD.DELETE;
            } else if (annotation instanceof Authorization) {
                authorization = ((Authorization) annotation).value();
            } else if (annotation instanceof ContentType) {
                headers.put(CONTENT_TYPE, ((ContentType) annotation).value());
            } else if (annotation instanceof FormUrlEncoded) {
                headers.put(CONTENT_TYPE, CONTENT_TYPE_FORM_URL_ENCODED);
            } else if (annotation instanceof Cookie) {
                headers.put(COOKIE, ((Cookie) annotation).value());
            } else if (annotation instanceof Header) {
                Header ann = (Header) annotation;
                String name = ann.name();
                String value = ann.value();
                if (name != null && value != null)
                    headers.put(name, value);
            }
        }
    }

    protected void applyQueryString() {
        String queryString = getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            queryString = String.format("?%s", queryString);
        } else {
            queryString = "";
        }

        if (!URI.toLowerCase().startsWith("http")) {
            String baseURI = getBaseURI();
            if (baseURI != null && !baseURI.isEmpty()) {
                URI = String.format("%s/%s%s", baseURI, URI, queryString);
            } else {
                URI = String.format("%s%s", URI, queryString);
            }
        }
    }

    protected void applyPublicPropertiesValues() {
        Pattern p = Pattern.compile("\\:(\\w+)");
        Matcher m = p.matcher(URI);
        while (m.find()) {
            String fieldName = m.group(1);
            try {
                String value = getFieldValue(fieldName);
                URI = URI.replaceAll(String.format("\\:%s", fieldName), value);
            } catch (Exception e) {
                System.out.print(String.format("REST: Class %s -> Value for field %s is null", getDeclaredClass().getName(), fieldName));
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
