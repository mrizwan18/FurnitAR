package com.razi.furnitar;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewAdapter extends FirestoreRecyclerAdapter<Item, RecyclerViewAdapter.ViewHolder> {

    private OnItemClickListener listener;
    public RecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {
        super(options);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Item model) {
        viewHolder.name.setText(model.getName());
        viewHolder.price.setText(model.getPrice() + "");
        String url = model.getImages().get(0);
        Picasso.get()
                .load(url)
                .into(viewHolder.img);
        if (model.getIsAR()) {
            viewHolder.arLabel.setText("AR");
        } else {
            viewHolder.arLabel.setText("");
        }
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(pos), pos);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
