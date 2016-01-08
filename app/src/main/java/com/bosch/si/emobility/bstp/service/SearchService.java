package com.bosch.si.emobility.bstp.service;

import com.bosch.si.emobility.bstp.core.BaseService;
import com.bosch.si.rest.anno.POST;
import com.google.gson.annotations.Expose;

@POST("/rest/parking/search")
public class SearchService extends BaseService {

    @Expose
    public String direction;
    @Expose
    public String endTime;
    @Expose
    public String highway;
    @Expose
    public String latitude;
    @Expose
    public String locationName;
    @Expose
    public String longitude;
    @Expose
    public String searchString;
    @Expose
    public String startTime;
    @Expose
    public float radius;
}
