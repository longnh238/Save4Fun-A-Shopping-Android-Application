package com.example.save4fun.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.save4fun.R;
import com.example.save4fun.adapter.ProductByListAdapter;
import com.example.save4fun.db.DBProductsHelper;
import com.example.save4fun.model.Product;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListDetailFragment extends Fragment {

    List<Product> productsInList = new ArrayList<>();

    TextView textViewProductsByList, textViewTotalPrice, textViewProductsByCategories;

    SearchView searchViewProductsInList;

    RecyclerView recyclerViewProductsInList;

    PieChart pieChartProductByCategories;

    DBProductsHelper dbProductsHelper;
    int listId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_detail, container, false);

        textViewProductsByList = view.findViewById(R.id.textViewProductsByList);
        textViewTotalPrice = view.findViewById(R.id.textViewTotalPrice);
        textViewProductsByCategories = view.findViewById(R.id.textViewProductsByCategories);

        searchViewProductsInList = view.findViewById(R.id.searchViewProductsInList);

        recyclerViewProductsInList = view.findViewById(R.id.recyclerViewProductsInList);

        pieChartProductByCategories = view.findViewById(R.id.pieChartProductByCategories);

        dbProductsHelper = new DBProductsHelper(getContext());

        Bundle listData = getArguments();
        if (listData != null) {
            listId = listData.getInt("listId");
            String listName = listData.getString("listName");

            textViewProductsByList.setText("Products in " + listName);

            productsInList = dbProductsHelper.getProductsByListId(listId);
            if (!productsInList.isEmpty()) {

                double total = calculateTotalPrice(productsInList);
                DecimalFormat df = new DecimalFormat("$0.00");
                textViewTotalPrice.setText(df.format(total));
            } else {
                TextView textViewTotalPriceTitle = view.findViewById(R.id.textViewTotalPriceTitle);
                textViewTotalPriceTitle.setText("");

                textViewTotalPrice.setText("");
            }
        }

        ProductByListAdapter productByListAdapter = new ProductByListAdapter(productsInList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerViewProductsInList.setAdapter(productByListAdapter);
        recyclerViewProductsInList.setLayoutManager(linearLayoutManager);

        productByListAdapter.setOnDeleteItemClickListener(new ProductByListAdapter.OnDeleteItemClickListener() {
            @Override
            public void onDeleteItemClick(int position) {
                int productId = productsInList.get(position).getId();
                if (listId != 0) {
                    showDeleteConfirmationDialog(productByListAdapter, productId);
                }
            }
        });

        searchViewProductsInList.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Product> filteredProductsInList = filterListOfProductsInList(newText);
                productByListAdapter.updateData(filteredProductsInList);
                return false;
            }
        });

        displayPieChart();

        return view;
    }

    private void showDeleteConfirmationDialog(ProductByListAdapter productByListAdapter, int productId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to delete?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbProductsHelper.deleteProductInList(listId, productId);
                        productsInList = dbProductsHelper.getProductsByListId(listId);
                        productByListAdapter.updateData(productsInList);

                        double total = calculateTotalPrice(productsInList);
                        DecimalFormat df = new DecimalFormat("$0.00");
                        textViewTotalPrice.setText(df.format(total));

                        displayPieChart();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private double calculateTotalPrice(List<Product> products) {
        double total = 0;
        for (Product product : products) {
            total += product.getPrice() * product.getQuantity();
        }
        return total;
    }

    private List<Product> filterListOfProductsInList(String text) {
        productsInList = dbProductsHelper.getProductsByListId(listId);
        List<Product> filteredProductsInList = new ArrayList<>();
        for (Product productInList : productsInList) {
            if (productInList.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredProductsInList.add(productInList);
            }
        }
        productsInList = filteredProductsInList;
        return filteredProductsInList;
    }

    private void displayPieChart() {
        // Build pie chart
        Map<String, List<Product>> productsInListMap = new LinkedHashMap<>();
        for (Product product : productsInList) {
            String category = product.getCategory();
            if (productsInListMap.get(category) == null) {
                List<Product> products = new ArrayList<>();
                products.add(product);
                productsInListMap.put(category, products);
            } else {
                productsInListMap.get(category).add(product);
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, List<Product>> entry : productsInListMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue().size(), entry.getKey()));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(11);

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Format the value as an integer
                return String.valueOf((int) value);
            }
        };
        pieDataSet.setValueFormatter(formatter);

        PieData pieData = new PieData(pieDataSet);
        pieChartProductByCategories.setData(pieData);

        Legend legend = pieChartProductByCategories.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);

        pieChartProductByCategories.getDescription().setEnabled(false);

        if (productsInList.isEmpty()) {
            textViewProductsByCategories.setText("");
            ViewGroup parentViewGroup = (ViewGroup) textViewProductsByCategories.getParent();
            parentViewGroup.removeView(textViewProductsByCategories);

            // Get the layout parameters of the TextView
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textViewProductsByCategories.getLayoutParams();
            layoutParams.topMargin = 0;
            layoutParams.bottomMargin = 0;

            pieChartProductByCategories.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        }

        float holeRadius = 20f; // in percent
        pieChartProductByCategories.setHoleRadius(holeRadius); // Set the size of the hole

        pieChartProductByCategories.animateY(1000);
        pieChartProductByCategories.invalidate();
    }
}