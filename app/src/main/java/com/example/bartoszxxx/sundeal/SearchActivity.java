package com.example.bartoszxxx.sundeal;

import android.app.ActionBar;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.example.bartoszxxx.sundeal.Adapters.RecyclerAdapter;
import com.example.bartoszxxx.sundeal.Products.FirebaseHelper;
import com.example.bartoszxxx.sundeal.Products.ProductLocal;
import com.example.bartoszxxx.sundeal.Products.ProductFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private RecyclerAdapter rAdapter;
    private List<ProductLocal> products;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = this.getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = findViewById(R.id.products_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        rAdapter = new RecyclerAdapter(this);
        recyclerView.setAdapter(rAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) item.getActionView();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllProducts("");
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                rAdapter.setProducts(null);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            Handler mHandler = new Handler();

            @Override
            public boolean onQueryTextSubmit(final String s) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                mHandler.removeCallbacksAndMessages(null);
                if (s.equals("")) {
                    return false;
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getAllProducts(s);
                    }
                }, 500);
                return true;
            }
        });
        return true;
    }

    public void getAllProducts(String queryText) {
        products = new ArrayList<>();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(FirebaseHelper.DATABASE_REFERENCE);
        Query queryRef = database.orderByChild("item_lowercase").startAt(queryText).endAt(queryText + "\uf8ff");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    String owner = childDataSnapshot.getValue(ProductFirebase.class).getOwner();
                    String item = childDataSnapshot.getValue(ProductFirebase.class).getTitle();
                    String description = childDataSnapshot.getValue(ProductFirebase.class).getDescription();
                    String location = childDataSnapshot.getValue(ProductFirebase.class).getLocation();
                    String key = childDataSnapshot.getValue(ProductFirebase.class).getKey();
                    Boolean itemGiveaway = childDataSnapshot.getValue(ProductFirebase.class).getGiveaway();
                    ProductLocal product = new ProductLocal(owner, item, description, location, itemGiveaway, key);
                    try {
                        products.add(product);
                    } catch (NullPointerException e) {
                        Log.e("ProductList", e.toString());
                    }
                }
                rAdapter.setProducts(products);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
