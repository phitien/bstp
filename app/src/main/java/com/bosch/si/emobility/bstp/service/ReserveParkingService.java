package com.bosch.si.emobility.bstp.service;

import com.bosch.si.emobility.bstp.core.BaseService;
import com.bosch.si.rest.anno.POST;
import com.google.gson.annotations.Expose;

/**
 * Created by SSY1SGP on 20/1/16.
 */

@POST("/rest/parkingtransaction/reserve")
public class ReserveParkingService extends BaseService {

    @Expose
    public String additionalInfo;

    @Expose
    public String driverId;

    @Expose
    public String endTime;

    @Expose
    public String parkingId;

    @Expose
    public String paymentMode;

    @Expose
    public String startTime;

    @Expose
    public String vehicleId;

}
