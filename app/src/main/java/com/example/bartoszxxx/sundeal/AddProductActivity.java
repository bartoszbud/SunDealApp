package com.example.bartoszxxx.sundeal;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bartoszxxx.sundeal.Products.FirebaseHelper;
import com.example.bartoszxxx.sundeal.Products.ProductFirebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddProductActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final int MIN_ITEM_NAME_LENGTH = 4;
    Geocoder geocoder;
    private FirebaseAuth firebaseAuth;
    private NestedScrollView nScrollView;
    private LatLng latLng;
    private Marker marker;
    private GoogleMap mMap;
    private EditText ItemName, ItemDescription;
    private TextView ItemLocation;
    private RadioButton RdBtnGiveaway, RdBtnExchange;
    private RadioGroup RadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        firebaseAuth = FirebaseAuth.getInstance();

        nScrollView = (NestedScrollView) findViewById(R.id.scroll_view);
        ItemLocation = (TextView) findViewById(R.id.TvLocation);
        ItemName = (TextInputEditText) findViewById(R.id.EtItemName);
        ItemDescription = (TextInputEditText) findViewById(R.id.EtItemDesc);
        RdBtnGiveaway = (RadioButton) findViewById(R.id.RadioBtnGiveaway);
        RdBtnExchange = (RadioButton) findViewById(R.id.RadioButtonExchange);
        RadioGroup = (RadioGroup) findViewById(R.id.RadioGroup);

        WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                nScrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });

        ItemName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ItemName.getText().toString().length() < MIN_ITEM_NAME_LENGTH) {
                    ItemName.setError(getString(R.string.titlename_too_short));
                } else {
                    ItemName.setError(null);
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng warsaw = new LatLng(52.229676, 21.012229);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(warsaw, 11));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                latLng = point;

                List<Address> addresses = new ArrayList<>();
                try {
                    addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Address address = addresses.get(0);

                if (address != null) {
                    ItemLocation.setText(address.getAddressLine(0));
                }

                if (marker != null) {
                    marker.remove();
                }

                marker = mMap.addMarker((new MarkerOptions().position(point).title(address.getAddressLine(0))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
            }
        });
    }

    private void setClear() {
        ItemName.setText("");
        ItemDescription.setText("");
        ItemName.clearFocus();
        ItemDescription.clearFocus();
        RadioGroup.clearCheck();
        ItemName.setError(null);
    }

    private void addProduct() {

        String itemName = ItemName.getText().toString().trim();
        String itemDescription = ItemDescription.getText().toString().trim();
        String itemLocation = ItemLocation.getText().toString();
        Boolean itemGiveaway = RdBtnGiveaway.isChecked();

        if (marker == null) {
            itemLocation = getResources().getString(R.string.location_default);
        }
        if (!RdBtnGiveaway.isChecked() && !RdBtnExchange.isChecked() || itemName.length() < MIN_ITEM_NAME_LENGTH) {
            Toast.makeText(this, "Uzupełnij brakujące dane", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference(FirebaseHelper.DATABASE_REFERENCE);
            String id_key = database.push().getKey();
            ProductFirebase productFirebase = new ProductFirebase(
                    firebaseAuth.getCurrentUser().getEmail(),
                    itemName,
                    itemName.toLowerCase(),
                    itemDescription,
                    itemLocation,
                    itemGiveaway,
                    id_key);
            database.child(id_key).setValue(productFirebase);
            Toast.makeText(this, "Pomyślnie dodano: " + productFirebase.getTitle(), Toast.LENGTH_SHORT).show();
            setClear();
        }
    }
}
