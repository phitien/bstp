package com.bosch.si.emobility.bstp.service;

import android.util.Log;

import com.bosch.si.emobility.bstp.core.Constants;
import com.bosch.si.emobility.bstp.core.User;
import com.bosch.si.rest.AbstractService;
import com.bosch.si.rest.anno.ContentType;
import com.bosch.si.rest.anno.POST;
import com.google.gson.annotations.Expose;

@ContentType("application/vnd.bosch-com.im+xml")
//@POST("http://ivsrv034.bosch-si.com:8080/im-server/1/rest/authentication")
@POST(Constants.IM_AUTH_BASE_URL)
public class LoginService extends AbstractService {

    @Override
    public String getBaseURI() {
        return Constants.BASE_URL;
    }

    @Expose
    public User user;

    @Override
    public String getBody() {
        body = user.toXMLString();
        return super.getBody();
    }
}
