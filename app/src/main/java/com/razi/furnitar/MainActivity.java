package com.razi.furnitar;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static GoogleApiClient mGoogleApiClient;
    private static Context c;
    internetConnectivity it;

    FirebaseAuth gAuth;
    List<Item> products;
    List<ProductCategory> categories;
    FirebaseFirestore mFirebaseFirestore;
    TextView textView5;
    ImageView imageView3;
    ProductCategoryAdapter productCategoryAdapter;
    RecyclerView productCatRecycler, prodItemRecycler;
    ProductAdapter productAdapter;
    FirebaseAuth.AuthStateListener aL;
    private FirebaseAnalytics mFirebaseAnalytics;

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                status -> c.startActivity(new Intent(c, Login.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        gAuth.addAuthStateListener(aL);
        mGoogleApiClient.connect();
    }

    protected void onDestroy() {
        unregisterReceiver(it);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        // Database reference pointing to root of database
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        textView5 = findViewById(R.id.textView5);
        imageView3 = findViewById(R.id.imageView3);
        gAuth = FirebaseAuth.getInstance();
        c = this;
        IntentFilter in = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        it = new internetConnectivity();
        registerReceiver(it, in);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        fetchCategoriesFromFirebase();
        fetchProductsFromFirebase();

        aL = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
            textView5.setText("Hello, " + firebaseAuth.getCurrentUser().getDisplayName()+"!");
            imageView3.setImageURI(firebaseAuth.getCurrentUser().getPhotoUrl());
        };

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, connectionResult -> Log.i("OK", "NOT OK"))
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    private void fetchProductsFromFirebase() {
        mFirebaseFirestore.collection("items").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d("cat", "onSuccess: LIST EMPTY");
                        } else {
                            products = documentSnapshots.toObjects(Item.class);
                            setProdItemRecycler(products);
                            Log.d("cat", "onSuccess: " + categories);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchCategoriesFromFirebase() {
        mFirebaseFirestore.collection("categories").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d("cat", "onSuccess: LIST EMPTY");
                        } else {
                            categories = documentSnapshots.toObjects(ProductCategory.class);
                            setProductRecycler(categories);
                            Log.d("cat", "onSuccess: " + categories);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setProductRecycler(List<ProductCategory> productCategoryList) {

        productCatRecycler = findViewById(R.id.cat_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        productCatRecycler.setLayoutManager(layoutManager);
        productCategoryAdapter = new ProductCategoryAdapter(this, productCategoryList);
        productCatRecycler.setAdapter(productCategoryAdapter);

    }

    private void setProdItemRecycler(List<Item> productsList) {

        prodItemRecycler = findViewById(R.id.product_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        prodItemRecycler.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(this, productsList);
        prodItemRecycler.setAdapter(productAdapter);

    }

}
