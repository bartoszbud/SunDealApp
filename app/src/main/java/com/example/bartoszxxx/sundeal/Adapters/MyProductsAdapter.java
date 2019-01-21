package com.example.bartoszxxx.sundeal.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bartoszxxx.sundeal.Products.Product;
import com.example.bartoszxxx.sundeal.R;

import java.util.List;

public class MyProductsAdapter extends RecyclerView.Adapter<MyProductsAdapter.ViewHolder> {

    private List<Product> products;

    public MyProductsAdapter() {

    }

    @NonNull
    @Override
    public MyProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rowView = inflater.inflate(R.layout.my_product_list_element, parent, false);
        MyProductsAdapter.ViewHolder viewHolder = new MyProductsAdapter.ViewHolder(rowView);
        return viewHolder;
    }

    public void setProducts(List<Product> reviews) {
        this.products = reviews;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final MyProductsAdapter.ViewHolder holder, final int position) {
        holder.title.setText(products.get(position).getTitle());
        holder.author.setText(products.get(position).getDescription());
        holder.text.setText(products.get(position).getLocation());
    }

    @Override
    public int getItemCount() {
        if (products != null) {
            return products.size();
        }
        return 0;
    }

    public Product getProduct(int index) {
        return (products != null) ? products.get(index) : null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView author;
        public TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);
            text = itemView.findViewById(R.id.text);
        }
    }
}