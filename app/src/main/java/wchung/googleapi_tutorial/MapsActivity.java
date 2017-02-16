package wchung.googleapi_tutorial;


import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import java.util.Calendar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 1; //request location
    private Polygon polygon1, polygon2, polygon3;
    private PolygonOptions rectOptions1, rectOptions2, rectOptions3;
    Boolean crossed = false; //when user crosses location fence change colour
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    double latitude;
    double longitude;
    private Boolean crossed1, crossed2, crossed3;
    private String time;
    private Calendar cal;
    private long startTime, endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cal = Calendar.getInstance();

        //get start time in seconds
        startTime = System.currentTimeMillis()/1000;
        Log.d("Log", "startTime: " + startTime);
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            //move app to the background to not waste battery
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
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
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        crossed1 = false;
        crossed2 = false;
        crossed3 = false;

        // Add a marker in TRU and move the camera
        LatLng kamloops = new LatLng(50.67046254, -120.3623406);

        //create a marker on the map
        //mMap.addMarker(new MarkerOptions().position(kamloops).title("Marker in Kamloops 1")
        //        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ghost)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kamloops, 17)); //17 is the zoom amount

        // Add a polygon
        rectOptions1 = new PolygonOptions()
                .add(   new LatLng(50.670783, -120.361965), //top right
                        new LatLng(50.670783, -120.362407), //top left
                        new LatLng(50.670591, -120.362407), //bottom left
                        new LatLng(50.670591, -120.361965));//bottom right

        rectOptions1.strokeColor(-65536);
        rectOptions1.fillColor(Color.argb(20, 255, 80, 255));

        rectOptions2 = new PolygonOptions()
                .add(   new LatLng(50.670742, -120.361825),
                        new LatLng(50.670742, -120.361394),
                        new LatLng(50.670500, -120.361394),
                        new LatLng(50.670500, -120.361825));

        rectOptions2.strokeColor(-65536);
        rectOptions2.fillColor(Color.argb(20, 255, 80, 255));


        rectOptions3 = new PolygonOptions()
                .add(   new LatLng(50.670427, -120.362548),
                        new LatLng(50.670427, -120.362088),
                        new LatLng(50.670234, -120.362088),
                        new LatLng(50.670234, -120.362548));

        rectOptions3.strokeColor(-65536);
        rectOptions3.fillColor(Color.argb(20, 255, 80, 255));


        // Get back the mutable Polygon
        polygon1 = mMap.addPolygon(rectOptions1);
        polygon2 = mMap.addPolygon(rectOptions2);
        polygon3 = mMap.addPolygon(rectOptions3);


        //when we cross the fence it will change the colour of the rectangle
        //if (crossed == true) {
        //    rectOptions.strokeColor(-65536);
        //    rectOptions.fillColor(Color.argb(20, 255, 80, 255));
        //}
        // Get back the mutable Polygon
        //polygon = mMap.addPolygon(rectOptions);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                Log.d("Log", "#1 INFO: shouldShowRequestPermissionRationale");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                showMessageOKCancel("You need to allow access to your location. Otherwise, you can't use the app!");

            } else {

                Log.d("Log", "#2 INFO: request the permission - No explanation needed");

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {

            //Permission granted
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void showMessageOKCancel(String message) {
        Log.d("Log", "#5 INFO: call AlertDialog");

        new AlertDialog.Builder(MapsActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", listener)
                .create()
                .show();
    }


    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

        final int BUTTON_NEGATIVE = -2;
        final int BUTTON_POSITIVE = -1;

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_NEGATIVE:
                    // int which = -2
                    Log.d("Log", "#7 INFO: permission is denied for the second time!");

                    dialog.dismiss();
                    break;

                case BUTTON_POSITIVE:
                    // int which = -1
                    Log.d("Log", "#6 INFO: send a second request");

                    ActivityCompat.requestPermissions(
                            MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                    dialog.dismiss();
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("Log", "#3 INFO: received a response permission was granted");
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    } else {
                        buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    Log.d("Log", "#4 INFO: received a response permission - denied");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        //start the timer
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            //mLastLocation = location;
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            if (latitude <= 50.670783 && latitude >= 50.670591 && longitude <= -120.361965 && longitude >= -120.362407){
                //Toast.makeText(getBaseContext(),"Current Location: Lat = " + latitude + ", and longitude = " + longitude, Toast.LENGTH_SHORT).show();
                Log.d("Log", "Here #1");

                if(crossed1 == false && crossed2 == false && crossed3 == false){
                    crossed1 = true;
                    Toast.makeText(getBaseContext(),"Checkpoint1 found!", Toast.LENGTH_SHORT).show();
                    Log.d("Log", "Here #2");
                }
            }

            else if (latitude <= 50.670742 && latitude >= 50.670500 && longitude <= -120.361394 && longitude >= -120.361825){
                //Toast.makeText(getBaseContext(),"Current Location: Lat = " + latitude + ", and longitude = " + longitude, Toast.LENGTH_SHORT).show();
                Log.d("Log", "Here #3");

                if(crossed1 == true && crossed2 == false && crossed3 == false) {
                    crossed2 = true;
                    Toast.makeText(getBaseContext(), "Checkpoint2 found!", Toast.LENGTH_SHORT).show();
                    Log.d("Log", "Here #4");
                }
                else {
                    Toast.makeText(getBaseContext(),"Oops, wrong checkpoint!", Toast.LENGTH_SHORT).show();
                    Log.d("Log", "Here #5");
                }

            }

            else if (latitude <= 50.670427 && latitude >= 50.670234 && longitude <= -120.362088 && longitude >= -120.362548){
                //Toast.makeText(getBaseContext(),"Current Location: Lat = " + latitude + ", and longitude = " + longitude, Toast.LENGTH_SHORT).show();
                Log.d("Log", "Here #6");

                if(crossed1 == true && crossed2 == true && crossed3 == false) {
                    crossed3 = true;
                    Toast.makeText(getBaseContext(), "Checkpoint3 found! Timer stopped." , Toast.LENGTH_SHORT).show();
                    Log.d("Log", "Here #7");
                }
                else {
                    Toast.makeText(getBaseContext(),"Oops, wrong checkpoint!" , Toast.LENGTH_SHORT).show();
                    Log.d("Log", "Here #8");
                }
            }
        }

        //TODO: put this code where it is not called every second
        if (crossed1 == true && crossed2 == true && crossed3 == true) {
            Toast.makeText(getBaseContext(),"You found them all!" , Toast.LENGTH_SHORT).show();
            Log.d("Log", "Here #9");

            //get end time in seconds
            endTime = System.currentTimeMillis()/1000;
            Log.d("Log", "endTime: " + endTime);

            //calculate total time spent
            //set time to string
            time = String.valueOf(getTime(startTime, endTime));

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);0

            //display time
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Your time is: " + time + " seconds!");

            builder.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    crossed1 = false;
                    crossed2 = false;
                    crossed3 = false;

                    //MainActivity.super.onBackPressed();
                    System.exit(0);
                }
            });

            builder.show();
        }
    }

    public long getTime(long start, long end) {
        long calculatedTime = 0;
        calculatedTime = end - start;

        return calculatedTime;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Do you want to stop the game?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //MainActivity.super.onBackPressed();
                System.exit(0);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}