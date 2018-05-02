package lab.wasikrafal.lab7;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
{

    private GoogleMap map;
    private DrawerLayout drawerLayout;
    private Activity mapsActivity;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor preferenceEditor;
    SwitchCompat satelliteSwitch;
    SwitchCompat positionSwitch;
    private List<Polyline> currentRoad;
    private List<Marker> currentMarkers;

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
        if(actionbar!=null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initNavigationMenu();
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
                        {
                            showFirstRoad();
                        }
                        if(id == R.id.nav_second_road)
                        {
                            showSecondRoad();
                        }
                        if(id == R.id.nav_third_road)
                        {
                            showThirdRoad();
                        }
                        if(id == R.id.nav_fourth_road)
                        {
                            showFourthRoad();
                        }

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
    }

    private void load()
    {
        satelliteSwitch.setChecked(sharedPreferences.getBoolean("satellite", false));
        positionSwitch.setChecked(sharedPreferences.getBoolean("position", false));
    }

    private void showFirstRoad()
    {
        Toast.makeText(MapsActivity.this, "showingfirst" , Toast.LENGTH_LONG).show();
    }

    private void showSecondRoad()
    {
        Toast.makeText(MapsActivity.this, "showing second" , Toast.LENGTH_LONG).show();
    }

    private void showThirdRoad()
    {
        Toast.makeText(MapsActivity.this, "showing third" , Toast.LENGTH_LONG).show();
    }

    private void showFourthRoad()
    {
        Toast.makeText(MapsActivity.this, "showing fourth" , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;
        load();
        LatLng wroclaw = new LatLng(51.0636, 17.0326);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(wroclaw, 11.0f));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
