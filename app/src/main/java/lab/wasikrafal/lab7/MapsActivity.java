package lab.wasikrafal.lab7;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar!=null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        initNavigationMenu();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initNavigationMenu()
    {
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        int id = menuItem.getItemId();

                        if(id == R.id.nav_first_road)
                        {
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        }


                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        Menu navigationMenu = navigationView.getMenu();
        MenuItem satelliteItem = navigationMenu.findItem(R.id.nav_satellite);
        SwitchCompat satelliteSwitch = (SwitchCompat) satelliteItem.getActionView().findViewById(R.id.nav_switch);
        satelliteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                Toast.makeText(MapsActivity.this, "is enabled frs: " + b , Toast.LENGTH_SHORT).show();
            }
        });

        MenuItem positionItem = navigationMenu.findItem(R.id.nav_position);
        SwitchCompat positionSwitch = (SwitchCompat) positionItem.getActionView().findViewById(R.id.nav_switch);
        positionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                try
                {
                    mMap.setMyLocationEnabled(b);
                }
                catch (SecurityException e)
                {
                    Toast.makeText(MapsActivity.this, "GPS error" , Toast.LENGTH_LONG).show();
                    compoundButton.setChecked(false);
                }
                //Toast.makeText(MapsActivity.this, "is enabled sec: " + b , Toast.LENGTH_SHORT).show();
            }
        });
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
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng wroclaw = new LatLng(51.0636, 17.0326);
        //mMap.addMarker(new MarkerOptions().position(wroclaw).title("Marker in wroclaw"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(wroclaw, 11.0f));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(wroclaw));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
