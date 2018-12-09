package com.razi.furnitar;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class RecyclerViewAdapter extends FirestoreRecyclerAdapter<Item, RecyclerViewAdapter.ViewHolder> {

    private OnItemClickListener listener;

    public RecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {
        super(options);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_view, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Item model) {
        String AR;
        if (model.getIsAR()) {
            AR = "AR";
        } else {
            AR = "";
        }
        if (!AR.equals("")) {
            TextDrawable drawable = TextDrawable.builder().buildRound(AR, R.color.colorPrimaryDark);
            viewHolder.item_view_AR.setImageDrawable(drawable);
        }
        viewHolder.item_view_name.setText(model.getName());
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        viewHolder.item_view_price.setText(fmt.format(model.getPrice()));
        String url = model.getImages().get(0);
        Picasso.get()
                .load(url)
                .into(viewHolder.item_view_image);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView item_view_name, item_view_price;
        ImageView item_view_image, item_view_AR;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_view_name = itemView.findViewById(R.id.item_view_name);
            item_view_price = itemView.findViewById(R.id.item_view_price);
            item_view_image = itemView.findViewById(R.id.item_view_image);
            item_view_AR = itemView.findViewById(R.id.item_view_AR);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(pos), pos);
                    }
                }
            });
        }
    }
}
