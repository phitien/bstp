package com.bosch.si.emobility.bstp.component.ux;

import android.view.View;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.model.ParkingTransaction;

/**
 * Created by sgp0458 on 30/12/15.
 */
public class ReservationViewHolder {

    TextView textViewParkingId;
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
        textViewTransactionId = (TextView) convertView.findViewById(R.id.textViewTransactionId);
        textViewTransactedDate = (TextView) convertView.findViewById(R.id.textViewTransactedDate);
        textViewStartTime = (TextView) convertView.findViewById(R.id.textViewStartTime);
        textViewEndTime = (TextView) convertView.findViewById(R.id.textViewEndTime);

        textViewParkingId = (TextView) convertView.findViewById(R.id.textViewParkingId);
        textViewTransactedBy = (TextView) convertView.findViewById(R.id.textViewTransactedBy);
        textViewTransactedFare = (TextView) convertView.findViewById(R.id.textViewTransactedFare);
        textViewPaymentMode = (TextView) convertView.findViewById(R.id.textViewPaymentMode);
        textViewVehicleId = (TextView) convertView.findViewById(R.id.textViewVehicleId);
        textViewDriverId = (TextView) convertView.findViewById(R.id.textViewDriverId);
        textViewAdditionalInfo = (TextView) convertView.findViewById(R.id.textViewAdditionalInfo);
    }

    public void populateData(ParkingTransaction transaction) {
        textViewTransactionId.setText(transaction.getTransactionId());
        textViewTransactedDate.setText(transaction.getTransactedDate());
        textViewStartTime.setText(transaction.getStartTime());
        textViewEndTime.setText(transaction.getEndTime());
        try {
            textViewParkingId.setText(transaction.getParkingId());
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