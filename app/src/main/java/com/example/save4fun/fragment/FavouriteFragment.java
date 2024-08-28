package com.example.save4fun.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.save4fun.R;
import com.example.save4fun.adapter.FavouriteProductAdapter;
import com.example.save4fun.adapter.PopularProductAdapter;
import com.example.save4fun.adapter.ProductByCategoryAdapter;
import com.example.save4fun.db.DBListsHelper;
import com.example.save4fun.db.DBProductsHelper;
import com.example.save4fun.model.MyList;
import com.example.save4fun.model.Product;
import com.example.save4fun.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class FavouriteFragment extends Fragment {

    List<Product> favouriteProducts;
    List<MyList> lists;

    RecyclerView recyclerViewFavouriteProduct;
    SearchView searchViewFavouriteProduct;
    Button positiveButtonDialog;

    DBProductsHelper dbProductsHelper;
    DBListsHelper dbListsHelper;

    String username = "";
    private int selectedListPositionInDialog = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        searchViewFavouriteProduct = view.findViewById(R.id.searchViewFavouriteProduct);
        searchViewFavouriteProduct.clearFocus();

        dbProductsHelper = new DBProductsHelper(getContext());
        dbListsHelper = new DBListsHelper(getContext());

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constant.PREFERENCES_NAME, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(Constant.USERNAME, "");

        favouriteProducts = dbProductsHelper.getFavouriteProductsByUsername(username);

        FavouriteProductAdapter favouriteProductAdapter = new FavouriteProductAdapter(favouriteProducts);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        recyclerViewFavouriteProduct = view.findViewById(R.id.recyclerViewFavouriteProduct);
        recyclerViewFavouriteProduct.setAdapter(favouriteProductAdapter);
        recyclerViewFavouriteProduct.setLayoutManager(gridLayoutManager);

        favouriteProductAdapter.setOnAddItemToListClickListener(new FavouriteProductAdapter.OnAddItemToListClickListener() {
            @Override
            public void onAddItemToListClick(int selectedProductPosition) {
                lists = dbListsHelper.getListByUsername(username);
                if (lists.isEmpty()) {
                    Toast.makeText(getContext(), "Please create a list first before you can add a product to the list", Toast.LENGTH_SHORT).show();
                } else {
                    showListDialog(selectedProductPosition);
                }
            }
        });

        favouriteProductAdapter.setOnRemoveFavouriteItemClickListener(new FavouriteProductAdapter.OnRemoveFavouriteItemClickListener() {
            @Override
            public void onRemoveFavouriteItemClick(int position) {
                int productId = favouriteProducts.get(position).getId();
                dbProductsHelper.addOrRemoveFavouriteProduct(username, productId, false);

                favouriteProducts = dbProductsHelper.getFavouriteProductsByUsername(username);
                favouriteProductAdapter.updateData(favouriteProducts);
            }
        });

        searchViewFavouriteProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Product> filteredFavouriteProducts = filterListOfFavouriteProducts(newText);
                favouriteProductAdapter.updateData(filteredFavouriteProducts);
                return false;
            }
        });

        return view;
    }

    private List<Product> filterListOfFavouriteProducts(String text) {
        favouriteProducts = dbProductsHelper.getFavouriteProductsByUsername(username);
        List<Product> filteredFavouriteProducts = new ArrayList<>();
        for (Product favouriteProduct : favouriteProducts) {
            if (favouriteProduct.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredFavouriteProducts.add(favouriteProduct);
            }
        }
        favouriteProducts = filteredFavouriteProducts;
        return filteredFavouriteProducts;
    }

    private void showListDialog(int selectedProductPosition) {
        List<String> listsNames = getListsNames();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select a list to add this product");

        // Convert listItems to array for ArrayAdapter
        final String[] listsNamesArray = listsNames.toArray(new String[0]);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product_to_list, null);

        // Set up the adapter for the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listsNamesArray) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
                }

                TextView textView = convertView.findViewById(android.R.id.text1);
                textView.setText(listsNamesArray[position]);

                if (position == selectedListPositionInDialog) {
                    convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_light));
                } else {
                    convertView.setBackgroundColor(Color.TRANSPARENT); // Reset background color
                }

                return convertView;
            }
        };

        EditText editTextQuantityAddProduct = dialogView.findViewById(R.id.editTextQuantityAddProduct);
        editTextQuantityAddProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int currentQuantity = Integer.parseInt(s.toString());
                checkAddProductToListDialog(currentQuantity);
            }
        });

        ImageView imageViewDecreaseQuantityProduct = dialogView.findViewById(R.id.imageViewDecreaseQuantityProduct);
        imageViewDecreaseQuantityProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(editTextQuantityAddProduct.getText().toString());
                currentQuantity--;
                if (currentQuantity >= 0) {
                    editTextQuantityAddProduct.setText(String.valueOf(currentQuantity));
                    editTextQuantityAddProduct.clearFocus();

                    checkAddProductToListDialog(currentQuantity);
                }
            }
        });

        ImageView imageViewIncreaseQuantityProduct = dialogView.findViewById(R.id.imageViewIncreaseQuantityProduct);
        imageViewIncreaseQuantityProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(editTextQuantityAddProduct.getText().toString());
                currentQuantity++;
                editTextQuantityAddProduct.setText(String.valueOf(currentQuantity));
                editTextQuantityAddProduct.clearFocus();

                checkAddProductToListDialog(currentQuantity);
            }
        });

        // Set the adapter to the ListView
        ListView listView = dialogView.findViewById(R.id.listViewItems);
        listView.setAdapter(adapter);

        // Set item click listener to handle selection
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click
                // Get the selected list position
                String selectedItem = listsNamesArray[position];

                selectedListPositionInDialog = position;
                editTextQuantityAddProduct.clearFocus();
                adapter.notifyDataSetChanged();

                int currentQuantity = Integer.parseInt(editTextQuantityAddProduct.getText().toString());
                checkAddProductToListDialog(currentQuantity);
            }
        });

        // Add view to the dialog
        builder.setView(dialogView);

        builder.setPositiveButton("Add to List", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                int listId = lists.get(selectedListPositionInDialog).getId();
                int productId = favouriteProducts.get(selectedProductPosition).getId();
                int currentQuantity = Integer.parseInt(editTextQuantityAddProduct.getText().toString());

                onAddButtonClick(listId, productId, currentQuantity);
                selectedListPositionInDialog = -1;
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedListPositionInDialog = -1;
                dialog.dismiss();
            }
        });

        AlertDialog addProductToListDialog = builder.create();
        addProductToListDialog.show();

        positiveButtonDialog = addProductToListDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButtonDialog.setEnabled(false);
    }

    public void onAddButtonClick(int listId, int productId, int quantity) {
        boolean result = dbProductsHelper.addOrUpdateProductByList(listId, productId, quantity);
        if (result) {
            Toast.makeText(getContext(), "Added item to list successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to add item to list", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAddProductToListDialog(int quantity) {
        if (selectedListPositionInDialog != -1 && quantity > 0) {
            positiveButtonDialog.setEnabled(true);
        } else {
            positiveButtonDialog.setEnabled(false);
        }
    }

    private List<String> getListsNames() {
        List<String> listsNames = new ArrayList<>();
        for (MyList list : lists) {
            listsNames.add(list.getName());
        }
        return listsNames;
    }
}