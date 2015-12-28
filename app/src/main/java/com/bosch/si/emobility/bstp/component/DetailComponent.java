package com.bosch.si.emobility.bstp.component;

import android.view.View;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.activity.MapsActivity;
import com.bosch.si.emobility.bstp.component.detail.DetailLayout;
import com.bosch.si.emobility.bstp.model.ParkingLocation;

/**
 * Created by sgp0458 on 16/12/15.
 */
public class DetailComponent extends Component {

    private static DetailComponent ourInstance = new DetailComponent();

    public static DetailComponent getInstance(MapsActivity activity) {
        if (activity != null)
            ourInstance.setActivity(activity);
        return ourInstance;
    }

    private DetailComponent() {
        super();
    }

    private ParkingLocation parkingLocation;
    private TextView textViewParkingTitle;
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
    }

    @Override
    public void setActivity(MapsActivity activity) {
        super.setActivity(activity);
        detailLayout = (DetailLayout) this.activity.findViewById(R.id.detailLayout);
        layout = detailLayout;

        textViewParkingTitle = (TextView) this.activity.findViewById(R.id.textViewParkingTitle);
        setEnabled(false, true);
    }

    @Override
    protected boolean isSlideUp() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled, boolean noAnimation) {
        if (enabled)
            detailLayout.minimize();
        super.setEnabled(enabled, noAnimation);
    }

    public void onReserveButtonClicked(View view) {

    }
}
