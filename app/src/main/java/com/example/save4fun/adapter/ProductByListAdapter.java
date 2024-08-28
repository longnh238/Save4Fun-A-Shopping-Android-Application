package com.example.save4fun.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.save4fun.R;
import com.example.save4fun.model.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductByListAdapter extends RecyclerView.Adapter<ProductByListAdapter.ProductViewHolder> {

    List<Product> products = new ArrayList<>();

    OnDeleteItemClickListener onDeleteItemClickListener;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public ProductByListAdapter(List<Product> productsByList) {
        this.products = productsByList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_product_by_list, parent, false);
        ProductViewHolder productViewHolder = new ProductViewHolder(productView);
        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.textViewProductByListName.setText(products.get(position).getName());

        DecimalFormat df = new DecimalFormat("$#.00");
        holder.textViewProductByListPrice.setText(df.format(products.get(position).getPrice()));

        byte[] decodedString = Base64.decode(products.get(position).getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.imageViewProductByList.setImageBitmap(decodedByte);

        holder.textViewProductByListQuantity.setText("Quantity: " + String.valueOf(products.get(position).getQuantity()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateData(List<Product> newProducts) {
        products.clear();
        products.addAll(newProducts);
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewProductByListName, textViewProductByListPrice, textViewProductByListQuantity;
        ImageView imageViewProductByList, imageViewDeleteProductByList;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewProductByListName = itemView.findViewById(R.id.textViewProductByListName);
            textViewProductByListPrice = itemView.findViewById(R.id.textViewProductByListPrice);
            textViewProductByListQuantity = itemView.findViewById(R.id.textViewProductByListQuantity);

            imageViewProductByList = itemView.findViewById(R.id.imageViewProductByList);

            imageViewDeleteProductByList = itemView.findViewById(R.id.imageViewDeleteProductByList);
            imageViewDeleteProductByList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteItemClickListener.onDeleteItemClick(getAdapterPosition());
                }
            });
        }
    }

    public OnDeleteItemClickListener getOnDeleteItemClickListener() {
        return onDeleteItemClickListener;
    }

    public void setOnDeleteItemClickListener(OnDeleteItemClickListener onDeleteItemClickListener) {
        this.onDeleteItemClickListener = onDeleteItemClickListener;
    }

    public interface OnDeleteItemClickListener {
        void onDeleteItemClick(int position);
    }
}
