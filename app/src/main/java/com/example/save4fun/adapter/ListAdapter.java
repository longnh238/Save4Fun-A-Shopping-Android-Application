package com.example.save4fun.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.save4fun.R;
import com.example.save4fun.model.MyList;
import com.example.save4fun.model.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    List<MyList> lists = new ArrayList<>();

    OnItemClickListener onItemClickListener;
    OnDeleteIconClickListener onDeleteIconClickListener;

    public ListAdapter(List<MyList> lists) {
        this.lists = lists;
    }

    public List<MyList> getLists() {
        return lists;
    }

    public void setLists(List<MyList> lists) {
        this.lists = lists;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_list, parent, false);
        ListViewHolder listViewHolder = new ListViewHolder(listView);
        return listViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.textViewListName.setText(lists.get(position).getName());
        holder.textViewListDescription.setText(lists.get(position).getDescription());
        holder.textViewListType.setText(lists.get(position).getType());
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public void updateData(List<MyList> newLists) {
        lists.clear();
        lists.addAll(newLists);
        notifyDataSetChanged();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView textViewListName, textViewListDescription, textViewListType;
        ImageView imageViewDeleteList;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewListName = itemView.findViewById(R.id.textViewListName);
            textViewListDescription = itemView.findViewById(R.id.textViewListDescription);
            textViewListType = itemView.findViewById(R.id.textViewListType);

            imageViewDeleteList = itemView.findViewById(R.id.imageViewDeleteList);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition());
                        notifyDataSetChanged();
                    }
                }
            });

            imageViewDeleteList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeleteIconClickListener != null) {
                        onDeleteIconClickListener.OnDeleteIconClick(getAdapterPosition());
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public OnDeleteIconClickListener getOnDeleteIconClickListener() {
        return onDeleteIconClickListener;
    }

    public void setOnDeleteIconClickListener(OnDeleteIconClickListener onDeleteIconClickListener) {
        this.onDeleteIconClickListener = onDeleteIconClickListener;
    }

    public interface OnDeleteIconClickListener {
        void OnDeleteIconClick(int position);
    }
}
