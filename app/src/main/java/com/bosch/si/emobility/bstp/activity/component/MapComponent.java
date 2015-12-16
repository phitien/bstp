package com.bosch.si.emobility.bstp.activity.component;

import android.location.Location;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.activity.MapsActivity;
import com.bosch.si.emobility.bstp.helper.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by sgp0458 on 16/12/15.
 */
public class MapComponent extends Component {

    private static MapComponent ourInstance = new MapComponent();
    private LatLngBounds currentCameraBounds;
    private static int CAMERA_MOVE_REACT_THRESHOLD_MS = 500;
    private long lastCallMs = Long.MIN_VALUE;

    public static MapComponent getInstance(MapsActivity activity) {
        if (activity != null)
            ourInstance.setActivity(activity);
        return ourInstance;
    }

    private MapComponent() {
        super();
    }

    GoogleMap map;
    SupportMapFragment mapFragment;
    View mapView;
    ImageButton imageButtonSearch;
    ImageButton imageButtonMenu;


    @Override
    public void setActivity(MapsActivity activity) {
        super.setActivity(activity);
        layout = (RelativeLayout) this.activity.findViewById(R.id.mapLayout);
        mapFragment = (SupportMapFragment) this.activity.getSupportFragmentManager().findFragmentById(R.id.map);
        mapView = this.activity.findViewById(R.id.map);
        imageButtonSearch = (ImageButton) this.activity.findViewById(R.id.imageButtonSearch);
        imageButtonMenu = (ImageButton) this.activity.findViewById(R.id.imageButtonMenu);
    }

    public void setUpMap() {
        map = mapFragment.getMap();
        map.setMyLocationEnabled(true);
        if (mapView != null && mapView.findViewById(1) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            layoutParams.setMargins(30, 30, 30, 30);
        }
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                moveCamera(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        });
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
                currentCameraBounds = bounds;
            }
        });
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                zoomToMyLocation();
                return true;
            }
        });
        zoomToMyLocation();
    }

    public void zoomToMyLocation() {
        Location location = Utils.getMyLocation(this.activity);
        LatLng myLatLng;
        if (location != null)
            myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        else
            myLatLng = new LatLng(0, 0);
        moveCamera(myLatLng);
    }

    public void moveCamera(LatLng myLatLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 11.0f));
        map.addMarker(new MarkerOptions().position(myLatLng).title("It's Me!"));
    }

    public LatLngBounds getCurrentLatLngBounds() {
        return currentCameraBounds;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        int visibility = enabled ? View.VISIBLE : View.GONE;
        imageButtonSearch.setEnabled(enabled);
        imageButtonSearch.setVisibility(visibility);
        imageButtonMenu.setEnabled(enabled);
        imageButtonMenu.setVisibility(visibility);
    }
}
