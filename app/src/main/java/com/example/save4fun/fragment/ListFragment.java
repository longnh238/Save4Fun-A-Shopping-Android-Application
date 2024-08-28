package com.example.save4fun.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.save4fun.R;
import com.example.save4fun.adapter.ListAdapter;
import com.example.save4fun.db.DBListsHelper;
import com.example.save4fun.model.MyList;
import com.example.save4fun.model.Product;
import com.example.save4fun.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    ConstraintLayout constraintLayoutAddList;

    RecyclerView recyclerViewLists;

    SearchView searchViewList;

    DBListsHelper dbListsHelper;

    List<MyList> lists;

    String username = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        constraintLayoutAddList = view.findViewById(R.id.constraintLayoutAddList);

        recyclerViewLists = view.findViewById(R.id.recyclerViewLists);

        searchViewList = view.findViewById(R.id.searchViewList);
        searchViewList.clearFocus();

        dbListsHelper = new DBListsHelper(getContext());

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constant.PREFERENCES_NAME, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(Constant.USERNAME, "");
        lists = dbListsHelper.getListByUsername(username);

        ListAdapter listAdapter = new ListAdapter(lists);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerViewLists.setAdapter(listAdapter);
        recyclerViewLists.setLayoutManager(linearLayoutManager);

        listAdapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putInt("listId", lists.get(position).getId());
                bundle.putString("listName", lists.get(position).getName());

                ListDetailFragment listDetailFragment = new ListDetailFragment();
                listDetailFragment.setArguments(bundle);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, listDetailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        listAdapter.setOnDeleteIconClickListener(new ListAdapter.OnDeleteIconClickListener() {
            @Override
            public void OnDeleteIconClick(int position) {
                showDeleteConfirmationDialog(listAdapter, position);
            }
        });

        // Click on the whole constraint layout to create a new list
        constraintLayoutAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                ListAddFragment listAddFragment = new ListAddFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, listAddFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Search View
        searchViewList.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<MyList> filteredLists = filterListOfLists(newText);
                listAdapter.updateData(filteredLists);
                return false;
            }
        });

        Bundle listData = getArguments();
        if (listData != null) {
            String name = listData.getString("name");
            String description = listData.getString("description");
            String type = listData.getString("type");

            MyList list = new MyList(name, description, type);
            dbListsHelper.createListForUsername(list, username);

            lists = dbListsHelper.getListByUsername(username);
            listAdapter.setLists(lists);
            listAdapter.notifyDataSetChanged();
        }

        return view;
    }

    private void showDeleteConfirmationDialog(ListAdapter listAdapter, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to delete?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbListsHelper.deleteListById(lists.get(position).getId());
                        lists = dbListsHelper.getListByUsername(username);
                        listAdapter.setLists(lists);
                        listAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User cancelled the dialog, do nothing
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private List<MyList> filterListOfLists(String text) {
        lists = dbListsHelper.getListByUsername(username);
        List<MyList> filteredLists = new ArrayList<>();
        for (MyList list : lists) {
            if (list.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredLists.add(list);
            }
        }
        lists = filteredLists;
        return filteredLists;
    }
}