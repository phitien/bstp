package com.bosch.si.emobility.bstp.service;

import com.bosch.si.emobility.bstp.core.BaseService;
import com.bosch.si.rest.anno.GET;
import com.bosch.si.rest.anno.QueryParam;

@GET("/rest/parking/info")
public class ParkingLocationInfoService extends BaseService {

    @QueryParam
    public String parkingid;

}
