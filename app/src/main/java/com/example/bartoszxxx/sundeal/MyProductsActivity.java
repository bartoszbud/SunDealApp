package com.example.bartoszxxx.sundeal;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bartoszxxx.sundeal.Adapters.MyProductsAdapter;
import com.example.bartoszxxx.sundeal.Products.FirebaseHelper;
import com.example.bartoszxxx.sundeal.Products.Product;
import com.example.bartoszxxx.sundeal.Products.ProductFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyProductsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private List<Product> products;
    private MyProductsAdapter mAdapter;
    private SharedPreferences prefs;
    private RecyclerView recyclerView;
    private TextView textEmptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_products);

        ActionBar actionBar = this.getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        textEmptyList = (TextView) findViewById(R.id.TvEmptyList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth = FirebaseAuth.getInstance();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mAdapter = new MyProductsAdapter();
        recyclerView.setAdapter(mAdapter);

        Button BtnLogout = (Button) findViewById(R.id.BtnLogout);
        BtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

        Button BtnSettings = (Button) findViewById(R.id.BtnSettings);
        BtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProductsActivity.this, SettingsActivity.class));
            }
        });

        ItemTouchHelper mIth = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        final int position = viewHolder.getAdapterPosition();
                        final Product product = products.get(position);
                        products.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        Snackbar snackbar = Snackbar
                                .make(recyclerView, "Usunięto: " + product.getTitle(), Snackbar.LENGTH_LONG)
                                .setAction("Cofnij", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        products.add(position, product);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }).addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                    @Override
                                    public void onDismissed(Snackbar transientBottomBar, int event) {
                                        super.onDismissed(transientBottomBar, event);
                                        if (event != DISMISS_EVENT_ACTION) {
                                            DatabaseReference database = FirebaseDatabase.getInstance().getReference(FirebaseHelper.DATABASE_REFERENCE);
                                            database.getRef().child(product.getKey()).removeValue();
                                        }
                                    }
                                });
                        snackbar.show();
                    }
                });
        mIth.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserProducts();
        TextView TvUserName = (TextView) findViewById(R.id.TvUserName);
        String name = prefs.getString("name", "");
        String message = getString(R.string.welcome_message, name);
        TvUserName.setText(message);
    }

    private void getUserProducts() {
        products = new ArrayList<>();
        Query queryRef = FirebaseDatabase.getInstance().getReference(FirebaseHelper.DATABASE_REFERENCE).orderByChild("owner").equalTo(prefs.getString("email", ""));
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    String itemTitle = childDataSnapshot.getValue(ProductFirebase.class).getTitle();
                    String itemDescription = childDataSnapshot.getValue(ProductFirebase.class).getDescription();
                    String itemLocation = childDataSnapshot.getValue(ProductFirebase.class).getLocation();
                    String itemKey = childDataSnapshot.getValue(ProductFirebase.class).getKey();
                    Product product = new Product(itemTitle, itemDescription, itemLocation, itemKey);
                    products.add(product);
                }
                mAdapter.setProducts(products);
                setListMessage();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setListMessage() {
        if (mAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            textEmptyList.setVisibility(View.VISIBLE);
            textEmptyList.setText(R.string.my_products_empty);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textEmptyList.setVisibility(View.GONE);
        }
    }

    private void logoutUser() {
        firebaseAuth.signOut();
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        Log.i("SHARED_PREFRENCES", "preferences cleared");
        Intent intent = new Intent(MyProductsActivity.this, SignInActivity.class);
        finish();
        startActivity(intent);
    }

}
