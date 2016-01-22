package com.bosch.si.emobility.bstp.core;

import com.bosch.si.emobility.bstp.model.SecurityDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SSY1SGP on 22/1/16.
 */
public class SecurityDetailsMapper {

    public static List<SecurityDetails> mapSecurityDetails(List<String> availableSecurityDetails){

        final List<SecurityDetails> updatedList = new ArrayList<SecurityDetails>() {{

            add(new SecurityDetails(Constants.SECURITY_FENCE));
            add(new SecurityDetails(Constants.SECURITY_CCTV));
            add(new SecurityDetails(Constants.SECURITY_LIGHTING));
            add(new SecurityDetails(Constants.SECURITY_GATE));
            add(new SecurityDetails(Constants.SECURITY_LPR));
        }};

        for (String securityDetail: availableSecurityDetails) {

            SecurityDetails existingItem = null;
            for (SecurityDetails itemData: updatedList) {
                if (itemData.getSecurityDetailName().compareToIgnoreCase(securityDetail) == 0) {
                    existingItem = itemData;
                    break;
                }
            }
            if (existingItem != null) {
                existingItem.setIsAvailable(true);
            }
        }

        return updatedList;
    }
}
