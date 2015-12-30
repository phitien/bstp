package com.bosch.si.rest;

import android.test.suitebuilder.annotation.MediumTest;

import com.bosch.si.rest.anno.Authorization;
import com.bosch.si.rest.anno.ContentType;
import com.bosch.si.rest.anno.Cookie;
import com.bosch.si.rest.anno.POST;
import com.bosch.si.rest.anno.QueryParam;
import com.google.gson.annotations.Expose;

import junit.framework.TestCase;

import org.json.JSONObject;

import java.util.List;

public class BasicTest extends TestCase {

    @POST("http://www.post.com/:path1/?path2=:path2")
    @ContentType("test-content-type")
    @Cookie("test-cookie")
    @Authorization("Basic username/password")
    public class TestService extends AbstractService {
        public String path1;
        public String path2;
        @QueryParam
        public String query1;
        @QueryParam
        public String query2;

        @Expose
        public String post1;
        @Expose
        public String post2;
    }

    @MediumTest
    public void testDetachVariables() {
        String URI = "http://www.google.com:8080/:var1/?var2=:var2";
        List<String> rs = AbstractService.detachVariables(URI);
        assertEquals(2, rs.size());
        assertEquals("var1", rs.get(0));
        assertEquals("var2", rs.get(1));
    }

    @MediumTest
    public void testValidData() throws Exception {
        TestService service = new TestService();
        service.path1 = "path1";
        service.path2 = "path2";
        service.query1 = "query1";
        service.query2 = "query2";
        service.post1 = "post1";
        service.post2 = "post2";
        JSONObject info = service.getInfo();
        assertEquals("http://www.post.com/path1/?path2=path2&query1=query1&query2=query2", info.getString("URI"));
        assertEquals("test-content-type", info.getJSONObject("headers").getString("Content-Type"));
        assertEquals("test-cookie", info.getJSONObject("headers").getString("cookie"));
        assertEquals("Basic username/password", info.getString("authorization"));
        assertEquals("{\"post1\":\"post1\",\"post2\":\"post2\"}", service.getBody());
    }
}