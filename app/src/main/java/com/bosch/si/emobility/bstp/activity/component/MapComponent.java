package com.bosch.si.emobility.bstp.activity.component;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.activity.MapsActivity;
import com.bosch.si.emobility.bstp.helper.Constants;
import com.bosch.si.emobility.bstp.helper.Utils;
import com.bosch.si.emobility.bstp.model.ParkingArea;
import com.bosch.si.emobility.bstp.service.SearchService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.ServiceCallback;
import com.google.android.gms.maps.CameraUpdate;
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
    private Marker myLocationMarker;

    private LatLng searchingLatLng;
    private Marker mySearchingMarker;

    private List<Marker> markers = new ArrayList<>();

    private LatLng prevLatLng = null;
    private LatLng currLatLng = null;

    public LatLng getSearchingLatLng() {
        return searchingLatLng;
    }

    public void setSearchingLatLng(LatLng searchingLatLng) {
        this.searchingLatLng = searchingLatLng;
        moveCamera(this.searchingLatLng);
    }

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
        map = mapFragment.getMap();
    }

    private void zoomToLocation(LatLng latLng) {
        moveCamera(latLng);
    }

    private void drawMySearchingMarker(LatLng latLng) {
        if (mySearchingMarker == null)
            mySearchingMarker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(activity.getString(R.string.current_search_location)));
        else
            mySearchingMarker.setPosition(latLng);
    }

    private LatLng drawMyLocationMarker() {
        LatLng latLng = getMyLocationLatLng();
        if (myLocationMarker == null) {
            myLocationMarker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(activity.getString(R.string.your_location))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        } else
            myLocationMarker.setPosition(latLng);
        return latLng;
    }

    @NonNull
    private LatLng getMyLocationLatLng() {
        Location location = Utils.getMyLocation(this.activity);
        LatLng latLng;
        if (location != null)
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        else
            latLng = new LatLng(0, 0);
        return latLng;
    }

    private CameraUpdate getZoomForDistance(LatLng originalPosition, double distance) {
        LatLng rightBottom = SphericalUtil.computeOffset(originalPosition, distance, 135);
        LatLng leftTop = SphericalUtil.computeOffset(originalPosition, distance, -45);
        LatLngBounds sBounds = new LatLngBounds(new LatLng(rightBottom.latitude, leftTop.longitude), new LatLng(leftTop.latitude, rightBottom.longitude));
        return CameraUpdateFactory.newLatLngBounds(sBounds, 50);

    }

    public void moveCamera(LatLng latLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.DEFAULT_ZOOM_LEVEL));
//        map.moveCamera(getZoomForDistance(latLng, Constants.DEFAULT_ZOOM_RADIUS));
    }

    private void displayParkingAreas() {
        drawMyLocationMarker();
        drawMySearchingMarker(searchingLatLng);
        if (shouldRefreshMap()) {
            refresh();
        }
    }

    //refresh map if the distance between the current location and searching location is greater than 100 km
    private boolean shouldRefreshMap() {
        if (currLatLng != null && prevLatLng != null) {
            float[] results = new float[1];
            Location.distanceBetween(prevLatLng.latitude, prevLatLng.longitude, currLatLng.latitude, currLatLng.longitude, results);
            return results[0] > 100000;
        }
        return prevLatLng == null;
    }

    private void clearMarkers() {
        map.clear();
        mySearchingMarker = null;
        myLocationMarker = null;
        markers = new ArrayList<>();
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
                double radius = 50000;//50km
                LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
                LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
                return new LatLngBounds(southwest, northeast);
            }
            currentCameraBounds = bounds;
        }
        return currentCameraBounds;
    }

    @Override
    public void setEnabled(boolean enabled, boolean noAnimation) {
        int visibility = enabled ? View.VISIBLE : View.GONE;
        if (enabled) {
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

                mapView.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        currentCameraBounds = map.getProjection().getVisibleRegion().latLngBounds;
                        prevLatLng = currentCameraBounds.getCenter();
                        return true;
                    }
                });
                map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        currentCameraBounds = map.getProjection().getVisibleRegion().latLngBounds;
                        if (currentCameraBounds != null) {
                            LatLng center = currentCameraBounds.getCenter();
                            if (center != null) {
                                currLatLng = center;
                                displayParkingAreas();
                            }
                        }
                    }
                });
                map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        searchingLatLng = getMyLocationLatLng();
                        zoomToLocation(searchingLatLng);
                        return true;
                    }
                });
                searchingLatLng = Utils.isLocationServiceDisabled(this.activity) ? Constants.DEFAULT_LOCATION : drawMyLocationMarker();
                zoomToLocation(searchingLatLng);
            }
        } else {

        }
        map.setMyLocationEnabled(enabled);
        map.getUiSettings().setMyLocationButtonEnabled(enabled);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setScrollGesturesEnabled(enabled);
        map.getUiSettings().setTiltGesturesEnabled(enabled);
        map.getUiSettings().setRotateGesturesEnabled(enabled);
        imageButtonSearch.setEnabled(enabled);
        imageButtonSearch.setVisibility(visibility);
        imageButtonMenu.setEnabled(enabled);
        imageButtonMenu.setVisibility(visibility);
    }

    public void refresh() {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this.activity, Locale.getDefault());
            addresses = geocoder.getFromLocation(currLatLng.latitude, currLatLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                this.activity.getSearchCriteria()
                        .setLocationName(address.getFeatureName())
                        .setLatLng(currLatLng)
                        .createSearchService()
                        .executeAsync(new ServiceCallback() {
                            @Override
                            public void success(IService service) {
                                Gson gson = new Gson();
                                List<ParkingArea> parkingAreas = gson.fromJson(service.getResponseString(), new TypeToken<ArrayList<ParkingArea>>() {
                                }.getType());
                                clearMarkers();
                                drawMyLocationMarker();
                                drawMySearchingMarker(searchingLatLng);
                                for (ParkingArea parkingArea : parkingAreas) {
                                    drawParkingAreaMarker(parkingArea);
                                }
                            }

                            @Override
                            public void failure(IService service) {
                                service.getResponseCode();
                            }
                        });
            }
        } catch (Exception e) {
            Utils.Log.e("BSTP_MapComponent_displayParkingAreas: ", e.getMessage());
        }
    }
}
