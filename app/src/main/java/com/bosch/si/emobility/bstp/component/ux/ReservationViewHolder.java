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
    TextView textViewStartTime;
    TextView textViewEndTime;

    public ReservationViewHolder(View convertView) {
        textViewStartTime = (TextView) convertView.findViewById(R.id.textViewStartTime);
        textViewEndTime = (TextView) convertView.findViewById(R.id.textViewEndTime);

        textViewParkingTitle = (TextView) convertView.findViewById(R.id.textViewParkingTitle);
        textViewParkingAddress = (TextView) convertView.findViewById(R.id.textViewParkingAddress);

    }

    public void populateData(ParkingTransaction transaction) {
        textViewStartTime.setText(transaction.getFormattedStartTime());
        textViewEndTime.setText(transaction.getFormattedEndTime());
        ParkingLocation parkingLocation = transaction.getParkingLocation();
        if (parkingLocation != null) {
            textViewParkingTitle.setText(parkingLocation.getLocationTitle());
            textViewParkingAddress.setText(parkingLocation.getAddress());
        }
    }
}