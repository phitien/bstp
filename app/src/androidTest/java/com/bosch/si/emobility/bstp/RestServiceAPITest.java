package com.bosch.si.emobility.bstp;

import android.test.suitebuilder.annotation.MediumTest;

import com.bosch.si.emobility.bstp.helper.Utils;
import com.bosch.si.emobility.bstp.model.User;
import com.bosch.si.emobility.bstp.service.LoginService;
import com.bosch.si.rest.AbstractService;
import com.bosch.si.rest.IService;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;


/**
 * Created by sgp0458 on 4/12/15.
 */
public class RestServiceAPITest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @MediumTest
    public void testLoginService() {
        User user = new User();
        user.setUsername("sgp0458");
        user.setPassword("Sgp04581234");

        LoginService loginService = new LoginService();
        loginService.user = user;
        String response = loginService.executeSync(null);

        assertNotNull(response);
        assertTrue(loginService.isOK());

        IService service = new AbstractService() {

            @Override
            public METHOD getMethod() {
                return METHOD.POST;
            }

            @Override
            public String getURI() {
                return "http://10.191.16.39:8080/upload";
            }

            @Override
            public Map<String, File> getFiles() {
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    // read this file into InputStream
                    inputStream = Utils.getResources().openRawResource(R.drawable.parking_orange);
                    // write the inputStream to a FileOutputStream
                    File file = Utils.getFile("test", "parking_orange.pngnge.png");
                    outputStream = new FileOutputStream(file);
                    int read = 0;
                    byte[] bytes = new byte[1024];
                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }
                    files.put("test", file);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (outputStream != null) {
                        try {
                            // outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return super.getFiles();
            }
        };

        service.executeAsync(null);
    }
}
