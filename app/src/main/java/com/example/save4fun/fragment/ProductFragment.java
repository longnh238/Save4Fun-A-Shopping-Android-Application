package com.example.save4fun.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.save4fun.R;
import com.example.save4fun.adapter.ProductByCategoryAdapter;
import com.example.save4fun.db.DBListsHelper;
import com.example.save4fun.db.DBProductsHelper;
import com.example.save4fun.model.MyList;
import com.example.save4fun.model.Product;
import com.example.save4fun.util.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductFragment extends Fragment {

    List<Product> productsByCategory;
    List<MyList> lists;

    TextView textViewVegetable, textViewMeat, textViewSnack, textViewBread, textViewBeverage;
    CircleImageView circleImageViewVegetable, circleImageViewMeat, circleImageViewSnack, circleImageViewBread, circleImageViewBeverage;
    List<TextView> textViewCategories;
    List<CircleImageView> circleImageViewCategories;
    LinearLayout linearLayoutProductCategories;
    RecyclerView recyclerViewProductsByCategories;
    SearchView searchViewProduct;
    Button positiveButtonDialog;

    DBProductsHelper dbProductsHelper;
    DBListsHelper dbListsHelper;

    int selectedCategory = -1;
    String username = "";
    private int selectedListPositionInDialog = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        textViewVegetable = view.findViewById(R.id.textViewVegetable);
        textViewMeat = view.findViewById(R.id.textViewMeat);
        textViewSnack = view.findViewById(R.id.textViewSnack);
        textViewBread = view.findViewById(R.id.textViewBread);
        textViewBeverage = view.findViewById(R.id.textViewBeverage);
        textViewCategories = Arrays.asList(textViewVegetable, textViewMeat, textViewSnack, textViewBread, textViewBeverage);

        circleImageViewVegetable = view.findViewById(R.id.circleImageViewVegetable);
        circleImageViewMeat = view.findViewById(R.id.circleImageViewMeat);
        circleImageViewSnack = view.findViewById(R.id.circleImageViewSnack);
        circleImageViewBread = view.findViewById(R.id.circleImageViewBread);
        circleImageViewBeverage = view.findViewById(R.id.circleImageViewBeverage);
        circleImageViewCategories = Arrays.asList(circleImageViewVegetable, circleImageViewMeat, circleImageViewSnack, circleImageViewBread, circleImageViewBeverage);

        recyclerViewProductsByCategories = view.findViewById(R.id.recyclerViewProductsByCategories);

        searchViewProduct = view.findViewById(R.id.searchViewProductsByCategory);
        searchViewProduct.clearFocus();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constant.PREFERENCES_NAME, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(Constant.USERNAME, "");

        dbProductsHelper = new DBProductsHelper(getContext());
        dbListsHelper = new DBListsHelper(getContext());

        productsByCategory = dbProductsHelper.getProductsByCategory(Constant.VEGETABLE_CATEGORY, username);
        selectedCategory = R.id.linearLayoutVegetable;
        setSelectedTextViewCategory(textViewVegetable, circleImageViewVegetable, textViewCategories, circleImageViewCategories);

        ProductByCategoryAdapter productByCategoryAdapter = new ProductByCategoryAdapter(productsByCategory);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        recyclerViewProductsByCategories.setAdapter(productByCategoryAdapter);
        recyclerViewProductsByCategories.setLayoutManager(gridLayoutManager);

        linearLayoutProductCategories = view.findViewById(R.id.linearLayoutProductCategories);
        for (int i = 0; i < linearLayoutProductCategories.getChildCount(); i++) {
            View linearLayoutProductCategory = linearLayoutProductCategories.getChildAt(i);
            if (linearLayoutProductCategory instanceof LinearLayout) {
                // Set OnClickListener for each child LinearLayout
                linearLayoutProductCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Handle click for the clicked child LinearLayout
                        if (view.getId() == R.id.linearLayoutVegetable) {
                            productsByCategory = dbProductsHelper.getProductsByCategory(Constant.VEGETABLE_CATEGORY, username);
                            setSelectedTextViewCategory(textViewVegetable, circleImageViewVegetable, textViewCategories, circleImageViewCategories);
                        } else if (view.getId() == R.id.linearLayoutMeat) {
                            productsByCategory = dbProductsHelper.getProductsByCategory(Constant.MEAT_CATEGORY, username);
                            setSelectedTextViewCategory(textViewMeat, circleImageViewMeat, textViewCategories, circleImageViewCategories);
                        } else if (view.getId() == R.id.linearLayoutSnack) {
                            productsByCategory = dbProductsHelper.getProductsByCategory(Constant.SNACK_CATEGORY, username);
                            setSelectedTextViewCategory(textViewSnack, circleImageViewSnack, textViewCategories, circleImageViewCategories);
                        } else if (view.getId() == R.id.linearLayoutBread) {
                            productsByCategory = dbProductsHelper.getProductsByCategory(Constant.BREAD_CATEGORY, username);
                            setSelectedTextViewCategory(textViewBread, circleImageViewBread, textViewCategories, circleImageViewCategories);
                        } else if (view.getId() == R.id.linearLayoutBeverage) {
                            productsByCategory = dbProductsHelper.getProductsByCategory(Constant.BEVERAGE_CATEGORY, username);
                            setSelectedTextViewCategory(textViewBeverage, circleImageViewBeverage, textViewCategories, circleImageViewCategories);
                        }
                        selectedCategory = view.getId();
                        productByCategoryAdapter.updateData(productsByCategory);
                    }
                });
            }
        }

        productByCategoryAdapter.setOnAddItemToListClickListener(new ProductByCategoryAdapter.OnAddItemToListClickListener() {
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

        productByCategoryAdapter.setOnAddOrRemoveFavouriteItemClickListener(new ProductByCategoryAdapter.OnAddOrRemoveFavouriteItemClickListener() {
            @Override
            public void onAddOrRemoveFavouriteItemClick(int position) {
                if (productsByCategory.get(position).getIsFavourite()) {
                    dbProductsHelper.addOrRemoveFavouriteProduct(username, productsByCategory.get(position).getId(), false);
                } else {
                    dbProductsHelper.addOrRemoveFavouriteProduct(username, productsByCategory.get(position).getId(), true);
                }
                String category = productsByCategory.get(position).getCategory();
                productsByCategory = dbProductsHelper.getProductsByCategory(category, username);
                productByCategoryAdapter.updateData(productsByCategory);
            }
        });

        searchViewProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Product> filteredProductsByCategory = filterListOfProducts(newText);
                productByCategoryAdapter.updateData(filteredProductsByCategory);
                return false;
            }
        });

        return view;
    }

    private List<Product> filterListOfProducts(String text) {
        if (selectedCategory == R.id.linearLayoutVegetable) {
            productsByCategory = dbProductsHelper.getProductsByCategory(Constant.VEGETABLE_CATEGORY, username);
        } else if (selectedCategory == R.id.linearLayoutMeat) {
            productsByCategory = dbProductsHelper.getProductsByCategory(Constant.MEAT_CATEGORY, username);
        } else if (selectedCategory == R.id.linearLayoutSnack) {
            productsByCategory = dbProductsHelper.getProductsByCategory(Constant.SNACK_CATEGORY, username);
        } else if (selectedCategory == R.id.linearLayoutBread) {
            productsByCategory = dbProductsHelper.getProductsByCategory(Constant.BREAD_CATEGORY, username);
        } else if (selectedCategory == R.id.linearLayoutBeverage) {
            productsByCategory = dbProductsHelper.getProductsByCategory(Constant.BEVERAGE_CATEGORY, username);
        }
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : productsByCategory) {
            if (product.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredProducts.add(product);
            }
        }
        productsByCategory = filteredProducts; // This is important to add a right item to list after filtering
        return filteredProducts;
    }

    private void setSelectedTextViewCategory(TextView textView, CircleImageView circleImageView, List<TextView> textViewCategories, List<CircleImageView> circleImageViewCategories) {
        for (TextView textViewCategory : textViewCategories) {
            textViewCategory.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            textViewCategory.setTypeface(null, Typeface.NORMAL);
        }

        for (CircleImageView circleImageViewCategory : circleImageViewCategories) {
            circleImageViewCategory.setBorderColor(ContextCompat.getColor(requireContext(), R.color.black));
        }

        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
        textView.setTypeface(null, Typeface.BOLD);
        circleImageView.setBorderColor(ContextCompat.getColor(requireContext(), R.color.blue));
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
                int productId = productsByCategory.get(selectedProductPosition).getId();
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