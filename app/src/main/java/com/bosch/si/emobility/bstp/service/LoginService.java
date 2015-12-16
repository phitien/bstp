package com.bosch.si.emobility.bstp.service;

import com.bosch.si.emobility.bstp.model.User;
import com.bosch.si.rest.method.POST;

@POST(value = "http://10.55.112.10:8080/im-server/1/rest/authentication", contentType = "application/vnd.bosch-com.im+xml")
public class LoginService extends BaseService {

    public User user;

    @Override
    public String getBody() {
        return user.toXMLString();
    }
}
