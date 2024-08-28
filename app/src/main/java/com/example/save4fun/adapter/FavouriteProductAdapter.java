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

public class FavouriteProductAdapter extends RecyclerView.Adapter<FavouriteProductAdapter.ProductViewHolder> {

    List<Product> products = new ArrayList<>();

    OnAddItemToListClickListener onAddItemToListClickListener;

    OnRemoveFavouriteItemClickListener onRemoveFavouriteItemClickListener;

    public FavouriteProductAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_favourite_product, parent, false);
        ProductViewHolder productViewHolder = new ProductViewHolder(productView);
        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.textViewFavouriteProductName.setText(products.get(position).getName());

        DecimalFormat df = new DecimalFormat("$0.00");
        holder.textViewFavouriteProductPrice.setText(df.format(products.get(position).getPrice()));

        byte[] decodedString = Base64.decode(products.get(position).getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.imageViewFavouriteProduct.setImageBitmap(decodedByte);

        holder.imageViewFavouriteIcon.setImageResource(R.drawable.ic_favorited);
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
        TextView textViewFavouriteProductName, textViewFavouriteProductPrice, textViewAddFavouriteProductToList;
        ImageView imageViewFavouriteIcon, imageViewFavouriteProduct, imageViewAddFavouriteProductToList;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFavouriteProductName = itemView.findViewById(R.id.textViewFavouriteProductName);
            textViewFavouriteProductPrice = itemView.findViewById(R.id.textViewFavouriteProductPrice);

            imageViewFavouriteIcon = itemView.findViewById(R.id.imageViewFavourite);
            imageViewFavouriteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRemoveFavouriteItemClickListener != null) {
                        onRemoveFavouriteItemClickListener.onRemoveFavouriteItemClick(getAdapterPosition());
                        notifyDataSetChanged();
                    }
                }
            });

            imageViewFavouriteProduct = itemView.findViewById(R.id.imageViewFavouriteProduct);

            textViewAddFavouriteProductToList = itemView.findViewById(R.id.textViewAddFavouriteProductToList);
            textViewAddFavouriteProductToList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAddItemToListClickListener != null) {
                        onAddItemToListClickListener.onAddItemToListClick(getAdapterPosition());
                    }
                }
            });

            imageViewAddFavouriteProductToList = itemView.findViewById(R.id.imageViewAddFavouriteProductToList);
            imageViewAddFavouriteProductToList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAddItemToListClickListener != null) {
                        onAddItemToListClickListener.onAddItemToListClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public OnAddItemToListClickListener getOnAddItemToListClickListener() {
        return onAddItemToListClickListener;
    }

    public void setOnAddItemToListClickListener(OnAddItemToListClickListener onAddItemToListClickListener) {
        this.onAddItemToListClickListener = onAddItemToListClickListener;
    }

    public interface OnAddItemToListClickListener {
        void onAddItemToListClick(int position);
    }

    public OnRemoveFavouriteItemClickListener getOnRemoveFavouriteItemClickListener() {
        return onRemoveFavouriteItemClickListener;
    }

    public void setOnRemoveFavouriteItemClickListener(OnRemoveFavouriteItemClickListener onRemoveFavouriteItemClickListener) {
        this.onRemoveFavouriteItemClickListener = onRemoveFavouriteItemClickListener;
    }

    public interface OnRemoveFavouriteItemClickListener {
        void onRemoveFavouriteItemClick(int position);
    }
}
