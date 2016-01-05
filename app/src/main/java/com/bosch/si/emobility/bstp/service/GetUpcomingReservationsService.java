package com.bosch.si.emobility.bstp.service;

import com.bosch.si.emobility.bstp.core.BaseService;
import com.bosch.si.rest.anno.POST;
import com.google.gson.annotations.Expose;

@POST("/rest/parkingtransaction/search")
public class GetUpcomingReservationsService extends BaseService {

    @Expose
    public String fromDate;
    @Expose
    public String toDate;
    @Expose
    public String searchTerm;

}
