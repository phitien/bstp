package com.bosch.si.emobility.bstp.activity.component;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.activity.MapsActivity;
import com.bosch.si.emobility.bstp.helper.Utils;
import com.bosch.si.emobility.bstp.model.ParkingArea;
import com.bosch.si.emobility.bstp.service.SearchService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.ServiceCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by sgp0458 on 16/12/15.
 */
public class MapComponent extends Component {

    private static MapComponent ourInstance = new MapComponent();
    private LatLngBounds currentCameraBounds;
    private Marker myMarker;
    private List<Marker> markers = new ArrayList<>();

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
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                currentCameraBounds = map.getProjection().getVisibleRegion().latLngBounds;
                if (currentCameraBounds != null) {
                    LatLng center = currentCameraBounds.getCenter();
                    if (center != null) {
                        displayParkingAreas(center);
                    }
                }
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

    private void zoomToMyLocation() {
        LatLng myLatLng = getMyLocationLatLng();
        drawMyLocationMarker(myLatLng);
        moveCamera(myLatLng);
    }

    private void drawMyLocationMarker(LatLng myLatLng) {
        if (myMarker == null) {
            myMarker = map.addMarker(new MarkerOptions().position(myLatLng).title(activity.getString(R.string.its_me)));
        } else {
            myMarker.setPosition(myLatLng);
        }
    }

    @NonNull
    private LatLng getMyLocationLatLng() {
        Location location = Utils.getMyLocation(this.activity);
        LatLng myLatLng;
        if (location != null)
            myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        else
            myLatLng = new LatLng(0, 0);
        return myLatLng;
    }

    public void moveCamera(LatLng latLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11.0f));
    }

    private void displayParkingAreas(LatLng latLng) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this.activity, Locale.getDefault());

            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {

                Address address = addresses.get(0);
                SearchService searchService = new SearchService();
                searchService.locationName = address.getFeatureName();
                searchService.latitude = String.valueOf(latLng.latitude);
                searchService.longitude = String.valueOf(latLng.longitude);
                searchService.startTime = this.activity.getFromDate().toString();
                searchService.endTime = this.activity.getToDate().toString();
                searchService.executeAsync(new ServiceCallback() {
                    @Override
                    public void success(IService service) {
                        Gson gson = new Gson();
                        List<ParkingArea> parkingAreas = gson.fromJson(service.getResponseString(), new TypeToken<ArrayList<ParkingArea>>() {
                        }.getType());
                        map.clear();
                        for (ParkingArea parkingArea : parkingAreas) {
                            drawParkingAreaMarker(parkingArea);
                        }
                        drawMyLocationMarker(getMyLocationLatLng());
                    }

                    @Override
                    public void failure(IService service) {
                        service.getResponseCode();
                    }
                });
            } else {

            }
        } catch (Exception e) {
            Utils.Log.e("BSTP_MapComponent_displayParkingAreas: ", e.getMessage());
        }
    }

    private void drawParkingAreaMarker(ParkingArea parkingArea) {
        LatLng latLng = new LatLng(parkingArea.latitude, parkingArea.longitude);
        markers.add(map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(parkingArea.locationTitle)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.parking))));
    }

    public LatLngBounds getCurrentLatLngBounds() {
        if (currentCameraBounds == null) {
            LatLngBounds bounds = null;
            try {
                bounds = map.getProjection().getVisibleRegion().latLngBounds;
            } catch (Exception e) {
                Utils.Log.e("BSTP_MapComponent_getCurrentLatLngBounds: ", e.getMessage());
            }
            if (bounds == null) {
                LatLng center = getMyLocationLatLng();
                double radius = 5000;
                LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
                LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
                return new LatLngBounds(southwest, northeast);

            }
            currentCameraBounds = bounds;
        }
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
