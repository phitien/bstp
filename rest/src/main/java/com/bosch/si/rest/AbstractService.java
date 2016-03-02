package com.bosch.si.rest;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

    protected String body = "";

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

    protected Exception exception = null;

    protected String boundary = null;

    private String servicePath = "";
    private PrintWriter writer = null;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;

    /**
     * Show protected properties
     *
     * @return JSONObject
     */
    @Override
    public JSONObject getInfo() throws JSONException {
        updateParams();
        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.PUBLIC, Modifier.PRIVATE)
                .create();
        return new JSONObject(gson.toJson(this).toString());
    }

    @Override
    public String getBaseURI() {
        return baseURI;
    }

    @Override
    public String getURI() {
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
        if (body == null || body.isEmpty()) {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .excludeFieldsWithModifiers(Modifier.PROTECTED, Modifier.PRIVATE)
                    .create();
            String rs = gson.toJson(this).toString();
            return rs == null || rs.equals("{}") ? "" : rs;
        }
        return body;
    }

    @Override
    public String getQueryString() {
        if (queryString == null || queryString.isEmpty()) {
            String queryString = "";
            Field[] fields = getDeclaredClass().getDeclaredFields();
            for (Field field : fields) {
                if (isQueryParam(field)) {
                    String value = "";
                    try {
                        value = String.valueOf(field.get(this));
                    } catch (IllegalAccessException e) {
                        exception = e;
                    }
                    queryString += "&" + field.getName() + "=" + value;
                }
            }
            if (!queryString.isEmpty())
                queryString = queryString.substring(1);
            return queryString;
        }
        return queryString;
    }

    private boolean isQueryParam(Field field) {
        int modifiers = field.getModifiers();
        if (!Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
            return field.getAnnotation(QueryParam.class) != null;
        }
        return false;
    }

    @Override
    public String getContentType() {
        return getHeader(CONTENT_TYPE);
    }

    @Override
    public METHOD getMethod() {
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
        try {
            responseString = null;
            IServiceConnection conn = getConnection();

//            Log.d("BSTP_SVC", conn.getURLConnection().getRequestMethod());

            conn.connect();
            //set responseCode
            responseCode = conn.getResponseCode();
            inputStream = conn.getInputStream();
            responseCookie = conn.getURLConnection().getHeaderField(SET_COOKIE);

//            Log.d("BSTP_SVC","responseCode "+ responseCode);

            if (isOK()) {
                BufferedReader reader = null;
                try {
                    StringBuilder builder = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line + "\n");
                    }
                    //set responseString
                    responseString = builder.toString();

//                    Log.d("BSTP_SVC","Response string "+ responseString);

                } catch (IOException e) {
                    exception = e;
                } finally {
                    try {
                        if (reader != null)
                            reader.close();
                    } catch (IOException e) {
                    }
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            exception = e;
            inputStream = null;
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

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public String getFieldValue(String fieldName) throws Exception {
        Field field = this.getDeclaredClass().getDeclaredField(fieldName);
        int modifiers = field.getModifiers();
        if (!Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
            field.setAccessible(true);
            return String.valueOf(field.get(this));
        }
        return null;
    }

    protected IServiceConnection getServiceConnection() throws Exception {
        updateParams();
        URL url = new URL(getURI());

        try {

            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            Log.d("BSTP_SVC", e.getLocalizedMessage());
        }

//        Log.d("BSTP_SVC","url " + url.toString());

        URLConnection conn = url.openConnection();
        if (conn instanceof HttpsURLConnection) {

            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            updateConnectionParams(httpsConn);

//            Log.d("BSTP_SVC", httpsConn.getRequestMethod());

            return new ServiceConnection(httpsConn);
        } else if (conn instanceof HttpURLConnection) {
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            updateConnectionParams(httpConn);
            return new ServiceConnection(httpConn);
        }
        return null;
    }

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
            if (getMethod() != METHOD.DELETE)
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
        return getClass();
//        Class<?> klass = getClass();
//        if (klass.getName().contains("$")) {
//            return klass.getSuperclass();
//        }
//        return klass;
    }

    //Set servicePath and method, only proceed with the first one
    protected void updateParams() {
        applyAnnotationsValues();
        URI = URI == null ? servicePath : URI;
        applyPathVariables();
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
        if (!URI.toLowerCase().startsWith("http")) {
            String baseURI = getBaseURI();
            if (baseURI != null && !baseURI.isEmpty()) {
                URI = String.format("%s/%s", baseURI, URI);
            }
        }
        queryString = URI.contains("?") ? "&" + queryString : "?" + queryString;
        URI = String.format("%s%s", URI, queryString);
    }

    protected void applyPathVariables() {
        List<String> fields = detachVariables(URI);
        for (Iterator<String> iterator = fields.iterator(); iterator.hasNext(); ) {
            String field = iterator.next();
            String value = "";
            try {
                value = getFieldValue(field);
            } catch (Exception e) {
                exception = e;
            }
            URI = URI.replaceAll(String.format("\\:%s", field), value);
        }
    }

    public static final List<String> detachVariables(String URI) {
        List<String> rs = new ArrayList<>();
        Pattern p = Pattern.compile("\\:([a-zA-Z_][a-zA-Z_0-9]*)");
        Matcher m = p.matcher(URI);
        while (m.find()) {
            rs.add(m.group(1));
        }
        return rs;
    }

    @Override
    public boolean isOK() {
        return responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_CREATED;
    }

    protected boolean isUnauthorized() {
        return responseCode == HttpURLConnection.HTTP_UNAUTHORIZED;
    }

}
