package com.example.bartoszxxx.sundeal.Adapters;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bartoszxxx.sundeal.Products.ProductLocal;
import com.example.bartoszxxx.sundeal.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<ProductLocal> products;
    private Context context;
    private Activity activity;

    private int mExpandedPosition = -1;

    public RecyclerAdapter() {

    }

    public RecyclerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        activity = (Activity) context;
        View rowView = inflater.inflate(R.layout.product_list_element, parent, false);
        RecyclerAdapter.ViewHolder viewHolder = new RecyclerAdapter.ViewHolder(rowView);
        return viewHolder;
    }

    public void setProducts(List<ProductLocal> reviews) {
        this.products = reviews;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, final int position) {

        holder.title.setText(products.get(position).getTitle());
        if (products.get(position).getGiveaway()) {
            holder.type.setText("Oddam");
        } else {
            holder.type.setText("Zamienię");
        }
        holder.description.setText(products.get(position).getDescription());
        holder.location.setText(products.get(position).getLocation());
        holder.contactBtn.setText(products.get(position).getOwner());

        holder.contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", products.get(position).getOwner(), null));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "SunDeal: " + products.get(position).getTitle());
                    intent.putExtra(Intent.EXTRA_TEXT, "Witam, jestem zainteresowany Pani/Pana ofertą.");
                    activity.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("MAILTO", "error");
                }
            }
        });

        final boolean isExpanded = position == mExpandedPosition;
        holder.moreInfo.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.imageView.setImageResource(isExpanded ? R.drawable.ic_up_less : R.drawable.ic_expand);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (products != null) {
            return products.size();
        }
        return 0;
    }

    private ProductLocal getProduct(int index) {
        return (products != null) ? products.get(index) : null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView type;
        private TextView description;
        private TextView location;
        private LinearLayout moreInfo;
        private ImageView imageView;
        private Button contactBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleView);
            type = itemView.findViewById(R.id.typeView);
            moreInfo = itemView.findViewById(R.id.moreInfo);
            imageView = itemView.findViewById(R.id.imageExpand);
            description = itemView.findViewById(R.id.descriptionView);
            location = itemView.findViewById(R.id.locationView);
            contactBtn = itemView.findViewById(R.id.contactBtn);
        }
    }
}