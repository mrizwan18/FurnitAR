package com.razi.furnitar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    public cartAdapter(Context context, List<order> items) {
        this.context = context;
        this.items = items;
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
}
