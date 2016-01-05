package com.bosch.si.emobility.bstp.component.ux;

import android.view.View;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.bosch.si.emobility.bstp.model.ParkingTransaction;

/**
 * Created by sgp0458 on 30/12/15.
 */
public class ReservationViewHolder {

    TextView textViewParkingTitle;
    TextView textViewParkingAddress;
    TextView textViewTransactedBy;
    TextView textViewTransactedFare;
    TextView textViewPaymentMode;
    TextView textViewVehicleId;
    TextView textViewDriverId;
    TextView textViewAdditionalInfo;
    TextView textViewTransactionId;
    TextView textViewTransactedDate;
    TextView textViewStartTime;
    TextView textViewEndTime;

    public ReservationViewHolder(View convertView) {
        textViewStartTime = (TextView) convertView.findViewById(R.id.textViewStartTime);
        textViewEndTime = (TextView) convertView.findViewById(R.id.textViewEndTime);

        textViewParkingTitle = (TextView) convertView.findViewById(R.id.textViewParkingTitle);
        textViewParkingAddress = (TextView) convertView.findViewById(R.id.textViewParkingAddress);

//        textViewTransactedDate = (TextView) convertView.findViewById(R.id.textViewTransactedDate);
//        textViewTransactionId = (TextView) convertView.findViewById(R.id.textViewTransactionId);
//        textViewTransactedBy = (TextView) convertView.findViewById(R.id.textViewTransactedBy);
//        textViewTransactedFare = (TextView) convertView.findViewById(R.id.textViewTransactedFare);
//        textViewPaymentMode = (TextView) convertView.findViewById(R.id.textViewPaymentMode);
//        textViewVehicleId = (TextView) convertView.findViewById(R.id.textViewVehicleId);
//        textViewDriverId = (TextView) convertView.findViewById(R.id.textViewDriverId);
//        textViewAdditionalInfo = (TextView) convertView.findViewById(R.id.textViewAdditionalInfo);
    }

    public void populateData(ParkingTransaction transaction) {
        textViewStartTime.setText(transaction.getStartTime());
        textViewEndTime.setText(transaction.getEndTime());
        ParkingLocation parkingLocation = transaction.getParkingLocation();
        if (parkingLocation != null) {
            textViewParkingTitle.setText(parkingLocation.getLocationTitle());
            textViewParkingAddress.setText(parkingLocation.getAddress());
        }
        try {
            textViewTransactedDate.setText(transaction.getTransactedDate());
            textViewTransactionId.setText(transaction.getTransactionId());
            textViewTransactedBy.setText(transaction.getTransactedBy());
            textViewTransactedFare.setText(transaction.getTransactedFare());
            textViewPaymentMode.setText(transaction.getPaymentMode());
            textViewVehicleId.setText(transaction.getVehicleId());
            textViewDriverId.setText(transaction.getDriverId());
            textViewAdditionalInfo.setText(transaction.getAdditionalInfo());
        } catch (Exception e) {

        }
    }
}