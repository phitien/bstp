package com.bosch.si.emobility.bstp.component;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.component.ListView.SecurityDetailsListAdapater;
import com.bosch.si.emobility.bstp.component.ux.DetailLayout;
import com.bosch.si.emobility.bstp.core.Activity;
import com.bosch.si.emobility.bstp.core.Component;
import com.bosch.si.emobility.bstp.core.FacilityViewManager;
import com.bosch.si.emobility.bstp.core.Utils;
import com.bosch.si.emobility.bstp.manager.DataManager;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by sgp0458 on 16/12/15.
 */
public class DetailComponent extends Component {

    private ParkingLocation parkingLocation;

    private ImageView imageViewParkingIcon;
    private TextView textViewParkingName;
    private TextView textViewParkingAddress;
    private TextView textViewAvailability;
    private TextView textViewSecurityLevel;
    private ListView listViewSecurityDetails;

    private FacilityViewManager facilityViewManager;

    public DetailComponent(Activity activity) {
        super(activity);
    }

    public ParkingLocation getParkingLocation() {
        return parkingLocation;
    }

    public void parkingLocationUpdated() {
        setParkingLocation(DataManager.getInstance().getCurrentParkingLocation());
    }

    public void setParkingLocation(ParkingLocation parkingLocation) {
        try {
            DetailLayout detailLayout = (DetailLayout) this.activity.findViewById(R.id.detailLayout);
            detailLayout.minimize();
        } catch (Exception e) {
        }
        this.parkingLocation = parkingLocation;
        setEnabled(true, false);
        populateData();
    }

    private void populateData() {
        if (this.parkingLocation != null) {

            String imageName = Utils.getParkingIconName(parkingLocation);
            imageViewParkingIcon.setImageResource(Utils.getImage(imageName));

            textViewParkingName.setText(parkingLocation.getLocationTitle());
            textViewParkingAddress.setText(parkingLocation.getAddress());
            textViewAvailability.setText(String.format("%d/%d",
                    Integer.valueOf(parkingLocation.getAvailabilityCount()),
                    Integer.valueOf(parkingLocation.getTotalCapacityCount())));
            textViewSecurityLevel.setText(parkingLocation.getSecurityLevel());

            SecurityDetailsListAdapater adpater = new SecurityDetailsListAdapater(this.activity, R.layout.security_item, parkingLocation.getMappedSecurityDetails());
            listViewSecurityDetails.setAdapter(adpater);

            facilityViewManager.updateIcons(parkingLocation.getFacilities());

        }
    }

    @Override
    public void setActivity(Activity activity) {
        super.setActivity(activity);
        layout = (ViewGroup) this.activity.findViewById(R.id.detailLayout);

        imageViewParkingIcon = (ImageView) this.activity.findViewById(R.id.imageViewParkingIcon);
        textViewParkingName = (TextView) this.activity.findViewById(R.id.textViewParkingName);
        textViewParkingAddress = (TextView) this.activity.findViewById(R.id.textViewParkingAddress);
        textViewAvailability = (TextView) this.activity.findViewById(R.id.textViewAvailability);
        textViewSecurityLevel = (TextView) this.activity.findViewById(R.id.textViewSecurityLevel);
        listViewSecurityDetails = (ListView) this.activity.findViewById(R.id.listViewSecurityDetails);

        facilityViewManager = new FacilityViewManager(this.activity);

        setEnabled(false, true);
    }

    @Override
    protected boolean isFade() {
        return true;
    }

    public void onReserveButtonClicked(View view) {
        //show the reservation confirmation ui

    }
}
