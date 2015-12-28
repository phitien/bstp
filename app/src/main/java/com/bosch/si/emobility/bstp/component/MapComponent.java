package com.bosch.si.emobility.bstp.component;

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
import com.bosch.si.emobility.bstp.app.Event;
import com.bosch.si.emobility.bstp.helper.Constants;
import com.bosch.si.emobility.bstp.helper.Utils;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.bosch.si.emobility.bstp.service.ParkingLocationInfoService;
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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sgp0458 on 16/12/15.
 */
public class MapComponent extends Component {

    private static MapComponent ourInstance = new MapComponent();
    private LatLngBounds currentCameraBounds;
    private Marker myLocationMarker;

    private LatLng searchingLatLng;
    private Marker searchingMarker;

    private Map<String, Marker> markers = new HashMap<>();

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
//        if (searchingMarker == null)
//            searchingMarker = map.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .title(activity.getString(R.string.current_search_location)));
//        else
//            searchingMarker.setPosition(latLng);
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

    @NonNull
    private LatLngBounds getLatLngBounds(LatLng latLng, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(latLng, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(latLng, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    private CameraUpdate getZoomForDistance(LatLng latLng, double radius) {
        return CameraUpdateFactory.newLatLngBounds(getLatLngBounds(latLng, radius), 0);
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
        searchingMarker = null;
        myLocationMarker = null;
        markers = new HashMap<>();
    }

    private void drawParkingLocationMarker(ParkingLocation parkingLocation) {
        String imageName = "parking_";
        try {
            float usedPercentage = 1 - (parkingLocation.getAvailabilityCount() / parkingLocation.getTotalCapacityCount());
            if (usedPercentage < 0.7)
                imageName += "green";
            else if (usedPercentage == 0)
                imageName += "red";
            else
                imageName += "orange";
        } catch (Exception e) {
            imageName += "grey";
        }
        LatLng latLng = new LatLng(parkingLocation.getLatitude(), parkingLocation.getLongitude());
        markers.put(parkingLocation.getParkingId(), map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(parkingLocation.getLocationTitle())
                .icon(BitmapDescriptorFactory.fromResource(Utils.getImage(imageName)))));
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
                return getLatLngBounds(getMyLocationLatLng(), Constants.DEFAULT_ZOOM_RADIUS);
            }
            currentCameraBounds = bounds;
        }
        return currentCameraBounds;
    }

    @Override
    public void setEnabled(boolean enabled, boolean noAnimation) {
        int visibility = enabled ? View.VISIBLE : View.INVISIBLE;
        if (enabled) {
            if (mapView != null && mapView.findViewById(1) != null) {
                // Get the button view
                View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
                // and next place it, on bottom right (as Google Maps app)
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 0, 30, 310);

                map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        Event.broadcast("", Constants.EventType.CAMERA_CHANGED.toString());
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
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        openLocationDetail(marker);
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
        map.getUiSettings().setZoomControlsEnabled(enabled);
        map.getUiSettings().setScrollGesturesEnabled(enabled);
        map.getUiSettings().setTiltGesturesEnabled(enabled);
        map.getUiSettings().setRotateGesturesEnabled(enabled);
        imageButtonSearch.setEnabled(enabled);
        imageButtonSearch.setVisibility(visibility);
        imageButtonMenu.setEnabled(enabled);
        imageButtonMenu.setVisibility(visibility);
    }


    private boolean openLocationDetail(Marker marker) {
        for (Map.Entry<String, Marker> entry : markers.entrySet()) {
            if (marker.equals(entry.getValue())) {
                ParkingLocationInfoService service = new ParkingLocationInfoService();
                service.parkingid = entry.getKey();
                service.executeAsync(new ServiceCallback() {
                    @Override
                    public void success(IService service) {
                        activity.openLocationDetail((new GsonBuilder().create()).fromJson(service.getResponseString(), ParkingLocation.class));
                    }

                    @Override
                    public void failure(IService service) {
                        service.getResponseCode();
                    }
                });
                return true;
            }
        }

        if (marker.equals(myLocationMarker)) {
            //do nothing
        } else if (marker.equals(searchingMarker)) {
            //do nothing
        }
        return false;
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
                                List<ParkingLocation> parkingLocations = gson.fromJson(service.getResponseString(), new TypeToken<ArrayList<ParkingLocation>>() {
                                }.getType());
                                clearMarkers();
                                drawMyLocationMarker();
                                drawMySearchingMarker(searchingLatLng);
                                for (ParkingLocation parkingLocation : parkingLocations) {
                                    drawParkingLocationMarker(parkingLocation);
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
