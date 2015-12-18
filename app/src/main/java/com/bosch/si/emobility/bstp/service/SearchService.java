package com.bosch.si.emobility.bstp.service;

import com.bosch.si.rest.anno.POST;

@POST("/rest/parking/search")
public class SearchService extends BaseService {

    public String direction;
    public String endTime;
    public String highway;
    public String latitude;
    public String locationName;
    public String longitude;
    public String searchString;
    public String startTime;

}
