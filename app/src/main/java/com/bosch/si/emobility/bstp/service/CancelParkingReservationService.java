package com.bosch.si.emobility.bstp.service;

import com.bosch.si.emobility.bstp.core.BaseService;
import com.bosch.si.rest.anno.DELETE;
import com.google.gson.annotations.Expose;

/**
 * Created by SSY1SGP on 20/1/16.
 */
@DELETE("/rest/parkingtransaction/cancel")
public class CancelParkingReservationService extends BaseService {

    @Expose
    public String transactionId;
}
