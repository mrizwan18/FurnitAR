package com.razi.furnitar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.Database.Database;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class cart_activity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<order> cart = new ArrayList<>();
    TextView total;
    Button checkout;
    cartAdapter adapter;
    @BindView(R.id.toolbar_cart)
    public Toolbar toolBar;
    private static Context c;
    private static GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_activity);
        ButterKnife.bind(this);
        c = this;
        toolBar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolBar);

        DrawerUtil.getDrawer(this, toolBar);

        recyclerView = findViewById(R.id.cart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        total = findViewById(R.id.total);
        checkout = findViewById(R.id.btnCart);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, connectionResult -> Log.i("OK", "NOT OK"))
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        loadItems();
    }

    public void loadItems() {
        cart = new Database(getBaseContext()).getCarts();
        Log.i("Yes", cart.size() + "");
        adapter = new cartAdapter(getBaseContext(), cart);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDelete(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        int totalP = 0;
        for (order o : cart) {
            totalP += o.getPrice() * o.getQuantity();
        }
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        total.setText(fmt.format(totalP));

    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                status -> c.startActivity(new Intent(c, Login.class)));
    }

    public void checkOut(View view) {
        for(int i = 0; i < adapter.getItemCount(); i++){
            adapter.checkOutItem(i);
        }
    }
}
