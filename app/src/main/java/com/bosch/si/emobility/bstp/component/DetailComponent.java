package com.bosch.si.emobility.bstp.component;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.activity.Activity;
import com.bosch.si.emobility.bstp.component.detail.DetailLayout;
import com.bosch.si.emobility.bstp.model.ParkingLocation;

/**
 * Created by sgp0458 on 16/12/15.
 */
public class DetailComponent extends Component {

    private static DetailComponent ourInstance = new DetailComponent();

    public static DetailComponent getInstance(Activity activity) {
        if (activity != null)
            ourInstance.setActivity(activity);
        return ourInstance;
    }

    private DetailComponent() {
        super();
    }

    private ParkingLocation parkingLocation;
    private TextView textViewParkingName;
    private TextView textViewParkingAddress;
    private TextView textViewAvailability;
    private TextView textViewSecurityLevel;
    private ListView listViewSecurityDetails;

    private DetailLayout detailLayout;

    public ParkingLocation getParkingLocation() {
        return parkingLocation;
    }

    public void setParkingLocation(ParkingLocation parkingLocation) {
        this.parkingLocation = parkingLocation;
        setEnabled(true, false);
        populateData();
    }

    private void populateData() {
        //TODO
        textViewParkingName.setText(parkingLocation.getLocationTitle());
        textViewParkingAddress.setText(parkingLocation.getAddress());
        textViewAvailability.setText(String.format("%d/%d",
                Integer.valueOf(parkingLocation.getAvailabilityCount()),
                Integer.valueOf(parkingLocation.getTotalCapacityCount())));
        textViewSecurityLevel.setText(parkingLocation.getSecurityLevel());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.activity, R.layout.security_item, R.id.text1, parkingLocation.getSecurityDetails());
        listViewSecurityDetails.setAdapter(adapter);
    }

    @Override
    public void setActivity(Activity activity) {
        super.setActivity(activity);
        detailLayout = (DetailLayout) this.activity.findViewById(R.id.detailLayout);
        layout = detailLayout;

        textViewParkingName = (TextView) this.activity.findViewById(R.id.textViewParkingName);
        textViewParkingAddress = (TextView) this.activity.findViewById(R.id.textViewParkingAddress);
        textViewAvailability = (TextView) this.activity.findViewById(R.id.textViewAvailability);
        textViewSecurityLevel = (TextView) this.activity.findViewById(R.id.textViewSecurityLevel);
        listViewSecurityDetails = (ListView) this.activity.findViewById(R.id.listViewSecurityDetails);

        setEnabled(false, true);
    }

    @Override
    protected boolean isSlideUp() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled, boolean noAnimation) {
        super.setEnabled(enabled, noAnimation);
        if (enabled)
            detailLayout.minimize();
    }

    public void onReserveButtonClicked(View view) {

    }
}
