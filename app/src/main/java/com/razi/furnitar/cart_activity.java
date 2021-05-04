package com.razi.furnitar;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.Database.Database;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class cart_activity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<order> cart = new ArrayList<>();
    TextView total;
    internetConnectivity it;
    Button checkout;
    cartAdapter adapter;
    public Toolbar toolBar;
    private static Context c;
    private static GoogleApiClient mGoogleApiClient;

    protected void onDestroy() {
        unregisterReceiver(it);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_activity);
        c = this;
        toolBar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolBar);

        DrawerUtil.getDrawer(this, toolBar);
        IntentFilter in = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        it = new internetConnectivity();
        registerReceiver(it, in);
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
        adapter.calTotal(cart, total);
    }


    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                status -> c.startActivity(new Intent(c, Login.class)));
    }

    public void checkOut(View view) {
        adapter.checkOutItem();
        total.setText("$0.00");
        this.finish();
        startActivity(new Intent(cart_activity.this, MainActivity.class));
    }
}
