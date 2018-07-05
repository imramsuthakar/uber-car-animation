package ubercar.com.ubercaranimation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ubercar.com.ubercaranimation.currentlocation.EasyLocationInit;
import ubercar.com.ubercaranimation.currentlocation.event.Event;
import ubercar.com.ubercaranimation.currentlocation.event.LocationEvent;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private int timeInterval = 3000;
    private int fastestTimeInterval = 3000;
    private boolean runAsBackgroundService = true;

    private LatLng oldPosition = null, newPosition = null;
    private boolean initialProcess = true;
    public static Double latitude = 0.0;
    public static Double longitude = 0.0;

    private Marker marker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        settingStatusBarTransparent();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
//            if (shouldChangeStatusBarTintToDark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            } else {
//                // We want to change tint color to white again.
//                // You can also record the flags in advance so that you can turn UI back completely if
//                // you have set other flags before, such as translucent or full screen.
//                decor.setSystemUiVisibility(0);
//            }
        }


        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initView();
    }

    private void initView() {

        //Onlocation changed code

        // new EasyLocationInit(context, timeInterval , fastestTimeInterval, runAsBackgroundService);

        //timeInterval -> setInterval(long)(inMilliSeconds) means - set the interval in which you want to get locations.
        //fastestTimeInterval -> setFastestInterval(long)(inMilliSeconds) means - if a location is available sooner you can get it.
        //(i.e. another app is using the location services).
        //runAsBackgroundService = True (Service will run in Background and updates Frequently(according to the timeInterval and fastestTimeInterval))
        //runAsBackgroundService = False (Service will getDestroyed after a successful location update )
        new EasyLocationInit(MapsActivity.this, timeInterval, fastestTimeInterval, runAsBackgroundService);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }



        mMap.setMyLocationEnabled(true);
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );



        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            MapsActivity.this, R.raw.styled_json));

            if (!success) {
                Log.e("Map", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Map", "Can't find style.", e);
        }

        // Add a marker in Sydney and move the camera
     /*   LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }


    private void settingStatusBarTransparent() {

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


            getWindow().setNavigationBarColor(Color.BLACK);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }

    @Subscribe
    public void getEvent(final Event event) {

        if (event instanceof LocationEvent) {


            if (((LocationEvent) event).location != null) {


                String lat = String.valueOf(((LocationEvent) event).location.getLatitude());
                String lng = String.valueOf(((LocationEvent) event).location.getLongitude());

                Location targetLocation = new Location("");//provider name is unnecessary
                targetLocation.setLatitude(Double.parseDouble(lat));//your coords of course
                targetLocation.setLongitude(Double.parseDouble(lng));


                Toast.makeText(getApplicationContext(),"The Latitude is "
                        + ((LocationEvent) event).location.getLatitude()
                        + " and the Longitude is "
                        + ((LocationEvent) event).location.getLongitude(), Toast.LENGTH_SHORT).show();

                showLocation(targetLocation);

            }
        }
    }




    private void showLocation(Location l) {
        if (l != null && l.getLatitude() != 0 && l.getLongitude() != 0) {
            if (newPosition != null)
                oldPosition = newPosition;
            newPosition = new LatLng(l.getLatitude(), l.getLongitude());
            latitude = l.getLatitude();
            longitude = l.getLongitude();
            if (initialProcess) {
                initialProcess = false;
                LatLng latLng = new LatLng(l.getLatitude(), l.getLongitude());



                marker = mMap
                        .addMarker(new MarkerOptions().position(new LatLng(l.getLatitude(), l.getLongitude()))
                                .anchor(0.5f, 0.75f)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));
                showGoogleMapFeatures(latLng, 0);





            } else {




                animateMarker(oldPosition, newPosition);
                marker.setRotation(bearingBetweenLocations(oldPosition,newPosition));
            }
        }
    }


    private float bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {
        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        return (float) brng;
    }

    private void animateMarker(final LatLng startPosition, final LatLng toPosition) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1000;
        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);
                    double lng = t * toPosition.longitude + (1 - t) * startPosition.longitude;
                    double lat = t * toPosition.latitude + (1 - t) * startPosition.latitude;

                    marker.setPosition(new LatLng(lat, lng));

                    // Post again 16ms later.
                    if (t < 1.0) handler.postDelayed(this, 16);
                    else {
                        marker.setVisible(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showGoogleMapFeatures(LatLng sydney, int i) {
        if (i == 0) {
            // Google Map like & feel
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // Rotate
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            // Compass
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            // zooming the google map
            mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
            //Move the camera to the user's location and zoom in!
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12.0f));
            CameraUpdate cameraUpdate;
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, 16);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.animateCamera(cameraUpdate);
        } else {
            // Google Map like & feel
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // Rotate
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            // Compass
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            // zooming the google map
            mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        }
    }

}
