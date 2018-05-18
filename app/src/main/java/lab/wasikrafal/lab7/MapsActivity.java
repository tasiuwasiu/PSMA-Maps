package lab.wasikrafal.lab7;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{

    private GoogleMap map;
    private DrawerLayout drawerLayout;
    private Activity mapsActivity;
    private CustomInfoWindowAdapter windowAdapter;

    private SwitchCompat satelliteSwitch;
    private SwitchCompat positionSwitch;
    private SwitchCompat markerSwitch;
    private SwitchCompat roadSwitch;
    private SwitchCompat nightSwitch;

    private List<Polyline> currentRoad;
    private List<Marker> currentMarkers;
    private int currentIcon;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferenceEditor;

    private int[] urls;
    private List<Integer> markerIcons;
    private List<String> markerIconsDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sharedPreferences = getSharedPreferences("lab.wasikrafal.lab7", Context.MODE_PRIVATE);
        mapsActivity = this;
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar!=null)
        {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initData();
        currentMarkers = new ArrayList<>();
        currentRoad = new ArrayList<>();
        currentIcon = 0;
        windowAdapter = new CustomInfoWindowAdapter(this);
        initNavigationMenu();
    }

    private void initData()
    {
        urls= new int[3];
        urls[0] = R.string.url_first;
        urls[1] = R.string.url_second;
        urls[2] = R.string.url_third;

        markerIcons = new ArrayList<>();
        markerIcons.add(R.drawable.red_marker);
        markerIcons.add(R.drawable.blue_marker);
        markerIcons.add(R.drawable.green_marker);
        markerIcons.add(R.drawable.black_marker);

        markerIconsDesc = new ArrayList<>();
        markerIconsDesc.add(getString(R.string.red));
        markerIconsDesc.add(getString(R.string.blue));
        markerIconsDesc.add(getString(R.string.green));
        markerIconsDesc.add(getString(R.string.black));
    }

    private void initNavigationMenu()
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        int id = menuItem.getItemId();

                        if(id == R.id.nav_first_road)
                            showRoad(0);
                        if(id == R.id.nav_second_road)
                            showRoad(1);
                        if(id == R.id.nav_third_road)
                            showRoad(2);
                        if(id == R.id.nav_marker_icon)
                            changeMarkerIcon();

                        drawerLayout.closeDrawers();
                        return true;
                    }
                });

        Menu navigationMenu = navigationView.getMenu();
        MenuItem satelliteItem = navigationMenu.findItem(R.id.nav_satellite);
        satelliteSwitch = (SwitchCompat) satelliteItem.getActionView().findViewById(R.id.nav_switch);
        satelliteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if(b)
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                else
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                preferenceEditor = sharedPreferences.edit();
                preferenceEditor.putBoolean("satellite", b);
                preferenceEditor.apply();
            }
        });

        MenuItem positionItem = navigationMenu.findItem(R.id.nav_position);
        positionSwitch = (SwitchCompat) positionItem.getActionView().findViewById(R.id.nav_switch);
        positionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                try
                {
                    map.setMyLocationEnabled(b);
                }
                catch (SecurityException e)
                {
                    int resCode=0;
                    ActivityCompat.requestPermissions(mapsActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},resCode);
                    Toast.makeText(MapsActivity.this, "GPS error" , Toast.LENGTH_LONG).show();
                    compoundButton.setChecked(false);
                }
                preferenceEditor = sharedPreferences.edit();
                preferenceEditor.putBoolean("position", positionSwitch.isChecked());
                preferenceEditor.apply();
            }
        });

        MenuItem markerItem = navigationMenu.findItem(R.id.nav_markers);
        markerSwitch = (SwitchCompat) markerItem.getActionView().findViewById(R.id.nav_switch);
        markerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                toogleMarkers(b);
                preferenceEditor = sharedPreferences.edit();
                preferenceEditor.putBoolean("markers", b);
                preferenceEditor.apply();
            }
        });

        MenuItem roadItem = navigationMenu.findItem(R.id.nav_road);
        roadSwitch = (SwitchCompat) roadItem.getActionView().findViewById(R.id.nav_switch);
        roadSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                toogleRoad(b);
                preferenceEditor = sharedPreferences.edit();
                preferenceEditor.putBoolean("road", b);
                preferenceEditor.apply();
            }
        });

        MenuItem nightItem = navigationMenu.findItem(R.id.nav_night);
        nightSwitch = (SwitchCompat) nightItem.getActionView().findViewById(R.id.nav_switch);
        nightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                setMapStyle(b);
                preferenceEditor = sharedPreferences.edit();
                preferenceEditor.putBoolean("night", b);
                preferenceEditor.apply();
            }
        });
    }

    private void load()
    {
        satelliteSwitch.setChecked(sharedPreferences.getBoolean("satellite", false));
        positionSwitch.setChecked(sharedPreferences.getBoolean("position", false));
        markerSwitch.setChecked(sharedPreferences.getBoolean("markers", true));
        roadSwitch.setChecked(sharedPreferences.getBoolean("road", true));
        nightSwitch.setChecked(sharedPreferences.getBoolean("night", false));
        showRoad(sharedPreferences.getInt("chosenRoad", 0));
        setMarkerIcon(sharedPreferences.getInt("markerIcon", 4));
    }

    private void showRoad(final int number)
    {
        clearRoad();
        clearMarkers();
        DownloadTask downloader = new DownloadTask(new Response()
        {
            @Override
            public void processReceiving(boolean isReceived, List<String> directions)
            {
                if(isReceived)
                {
                    for (String path:directions)
                    {
                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.color(Color.BLUE);
                        polylineOptions.width(10);
                        polylineOptions.addAll(PolyUtil.decode(path));
                        currentRoad.add(map.addPolyline(polylineOptions));
                        showMarkers(number);
                        preferenceEditor = sharedPreferences.edit();
                        preferenceEditor.putInt("chosenRoad", number);
                        preferenceEditor.apply();
                    }
                }
                else
                {
                    Toast.makeText(MapsActivity.this, "ERROR" , Toast.LENGTH_LONG).show();
                }
            }
        }, getString(urls[number]), getString(R.string.google_maps_key));
        downloader.execute();
    }

    private void showMarkers(int number)
    {
        switch (number)
        {
            case(0):
            {
                Marker zoo = map.addMarker(new MarkerOptions().position(new LatLng(51.103010,17.071997)));
                zoo.setTag(new MarkerInfoHolder(R.string.zoo_title, R.string.zoo_desc, R.drawable.zoo));
                currentMarkers.add(zoo);
                currentMarkers.add(map.addMarker(new MarkerOptions().position(new LatLng(51.135069,17.047607)).title("4").snippet("44")));
                currentMarkers.add(map.addMarker(new MarkerOptions().position(new LatLng(51.126064,16.990499)).title("2").snippet("22")));
                currentMarkers.add(map.addMarker(new MarkerOptions().position(new LatLng(51.109317,17.054497)).title("pwr").snippet("PWR")));
                currentMarkers.add(map.addMarker(new MarkerOptions().position(new LatLng(51.100528,17.120618)).title("3").snippet("33")));

            }
            case(1):
            {

            }
            case(2):
            {

            }
            default:
            {
            }
        }
        setMarkerIcon(currentIcon);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;
        load();
        LatLng wroclaw = new LatLng(51.107524, 17.038507);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(wroclaw, 11.0f));
        map.setInfoWindowAdapter(windowAdapter);
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        if(marker.isInfoWindowShown())
            marker.hideInfoWindow();
        else
            marker.showInfoWindow();
        return false;
    }

    private void changeMarkerIcon()
    {
        ListAdapter adapter = new CustomArrayAdapter(this, markerIconsDesc, markerIcons);

        new AlertDialog.Builder(this).setTitle(getString(R.string.select_icon)).setAdapter(adapter, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                currentIcon = i;
                setMarkerIcon(currentIcon);
            }
        }).show();
    }

    private void setMarkerIcon(int index)
    {
        switch (index)
        {
            case 0:
            {
                for (int i = 0; i < currentMarkers.size(); i++)
                    currentMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker));
                break;
            }
            case 1:
            {
                Log.d("in", "item 2");
                for (int i = 0; i < currentMarkers.size(); i++)
                    currentMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));
                break;
            }
            case 2:
            {
                for (int i = 0; i < currentMarkers.size(); i++)
                    currentMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.green_marker));
                break;
            }
            case 3:
            {
                for (int i = 0; i < currentMarkers.size(); i++)
                    currentMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.black_marker));
                break;
            }
            default:
            {
                for (int i = 0; i < currentMarkers.size(); i++)
                    currentMarkers.get(i).setIcon(BitmapDescriptorFactory.defaultMarker());
            }
        }
        preferenceEditor = sharedPreferences.edit();
        preferenceEditor.putInt("markerIcon", index);
        preferenceEditor.apply();
    }

    private void setMapStyle( boolean isNightMode)
    {
        if (isNightMode)
        {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.night_style));
        }
        else
        {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.day_style));
        }
    }

    private void toogleMarkers(boolean isVisible)
    {
        for (int i = 0; i < currentMarkers.size(); i++)
            currentMarkers.get(i).setVisible(isVisible);
    }

    private void toogleRoad(boolean isVisible)
    {
        for (int i = 0; i < currentRoad.size(); i++)
            currentRoad.get(i).setVisible(isVisible);
    }

    private void clearRoad()
    {
        for (Polyline poly:currentRoad)
            poly.remove();
        currentRoad.clear();
    }

    private void clearMarkers()
    {
        for (Marker marker:currentMarkers) {
            marker.remove();
        }
        currentRoad.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
