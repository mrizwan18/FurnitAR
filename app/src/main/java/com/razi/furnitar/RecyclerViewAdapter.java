package com.razi.furnitar;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Item> items;

    public RecyclerViewAdapter(ArrayList<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.name.setText(items.get(i).getName());
        viewHolder.price.setText(items.get(i).getPrice() + "");
        viewHolder.img.setImageResource(R.drawable.chair);
        if (items.get(i).isStatus()) {
            viewHolder.arLabel.setText("AR");
        } else {
            viewHolder.arLabel.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView price;
        ImageView img;
        TextView arLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            arLabel = itemView.findViewById(R.id.ar_label);
            img = itemView.findViewById(R.id.imageView);
        }
    }
}
