package com.razi.furnitar;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.Database.Database;
import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class cartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView cart_item_name, cart_item_price;
    public ImageView cart_item_quantity;
    private RecyclerViewAdapter.OnItemClickListener itemClickListener;

    public void setCart_item_name(TextView cart_item_name) {
        this.cart_item_name = cart_item_name;
    }

    public cartViewHolder(@NonNull View itemView) {
        super(itemView);
        cart_item_name = itemView.findViewById(R.id.cart_item_name);
        cart_item_price = itemView.findViewById(R.id.cart_item_price);
        cart_item_quantity = itemView.findViewById(R.id.cart_item_quantity);

    }

    @Override
    public void onClick(View view) {

    }
}

public class cartAdapter extends RecyclerView.Adapter<cartViewHolder> {
    private Context context;
    private List<order> items = new ArrayList<>();
    FirebaseFirestore db;
    List<order> cart = new ArrayList<>();
    TextView total;

    public cartAdapter(Context context, List<order> items) {
        this.context = context;
        this.items = items;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public cartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_view, viewGroup, false);
        return new cartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull cartViewHolder cartViewHolder, int i) {
        TextDrawable drawable = TextDrawable.builder().buildRound("" + items.get(i).getQuantity(), R.color.colorPrimaryDark);
        cartViewHolder.cart_item_quantity.setImageDrawable(drawable);

        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        double price = (items.get(i).getPrice()) * items.get(i).getQuantity();
        cartViewHolder.cart_item_price.setText(fmt.format(price));
        cartViewHolder.cart_item_name.setText(items.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Context getContext() {
        return context;
    }

    public void deleteItem(int position) {
        order o = items.get(position);
        Pair<String, Integer> itemDetail = new Database(getContext()).removeFromCart(o);
        DocumentReference itemRef = db.collection("items").document(itemDetail.first);
        final int[] quantity = new int[1];
        itemRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                quantity[0] = documentSnapshot.getLong("quantity").intValue();
                itemRef.update("quantity", quantity[0] + itemDetail.second);
                items.remove(position);
                notifyItemRemoved(position);
                calTotal(cart, total);
            }
        });

    }

    public void checkOutItem() {
        ArrayList<order> orders = new ArrayList<>();
        for (int position = 0; position < items.size(); ) {
            orders.add(items.get(position));
            items.remove(position);
            notifyItemRemoved(position);
        }
        for (int position = 0; position < orders.size(); position++) {
            Pair<String, Integer> itemDetail = new Database(getContext()).removeFromCart(orders.get(position));
            Map<String, Object> history_item = new HashMap<>();
            history_item.put("id", itemDetail.first);
            history_item.put("quantity", itemDetail.second);
            history_item.put("user", orders.get(position).getUserid());
            db.collection("purchase_history").add(history_item);
            final String[] id = new String[1];
            int finalPosition = position;
            db.collection("cart").whereEqualTo("id", itemDetail.first).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    id[0] = queryDocumentSnapshots.getDocuments().get(0).getId();
                    db.collection("cart").document(id[0]).delete();
                }
            });
            String purchase = "Name: "
                    + orders.get(position).getName() + " Quantity: "
                    + orders.get(position).getQuantity() + " Total Price: $"
                    + (orders.get(position).getQuantity() * orders.get(position).getPrice());
            Intent intent = new Intent(context, HistoryService.class);
            intent.putExtra("purchase", purchase);
            context.startService(intent);
        }

    }

    public void calTotal(List<order> cart_t, TextView total_t) {
        cart = items;
        total = total_t;
        int totalP = 0;
        for (order o : cart) {
            totalP += o.getPrice() * o.getQuantity();
        }
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        total.setText(fmt.format(totalP));
    }

}
