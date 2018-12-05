package com.razi.furnitar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.Database.Database;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class cart_activity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference request;
    List<order> cart = new ArrayList<>();
    TextView total;
    Button checkout;
    cartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_activity);
       // firebaseDatabase = FirebaseDatabase.getInstance();
        //request = firebaseDatabase.getReference("Requests");
        recyclerView = findViewById(R.id.cart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        total = findViewById(R.id.total);
        checkout = findViewById(R.id.btnCart);
        loadItems();
    }

    public void loadItems() {
        cart = new Database(this).getCarts();
        adapter = new cartAdapter(this, cart);
        recyclerView.setAdapter(adapter);
        int totalP = 0;
        for (order o : cart) {
            totalP += o.getPrice() * o.getQuantity();
        }
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        total.setText(fmt.format(totalP));

    }

    public void removeFromCart(View view){

    }
}