package com.example.save4fun.fragment.list;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.Step;

public class ListTypeStep extends Step<String> {
    private Spinner listTypeView;

    public ListTypeStep(String stepTitle) {
        super(stepTitle);
    }

    @Override
    protected View createStepContentLayout() {
        listTypeView = new Spinner(getContext());

        // Define data source
        List<String> items = new ArrayList<>();
        items.add("Daily Essentials");
        items.add("Party");
        items.add("Economy");
        items.add("Healthy Eating");
        items.add("Quick Meals");
        items.add("Special Diet");
        items.add("Breakfast Essentials");
        items.add("Snack Attack");
        items.add("International Cuisine");
        items.add("Family Favorites");
        items.add("On-the-Go Options");
        items.add("Bulk Shopping");
        items.add("Other");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        listTypeView.setAdapter(adapter);

        listTypeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                markAsCompletedOrUncompleted(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                markAsCompletedOrUncompleted(false);
            }
        });

        return listTypeView;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        boolean isDescriptionValid = true;
        String errorMessage = !isDescriptionValid ? "3 characters minimum" : "";

        return new IsDataValid(isDescriptionValid, errorMessage);
    }

    @Override
    public String getStepData() {
        return listTypeView.getSelectedItem().toString();
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        String listDescription = getStepData();
        return !listDescription.isEmpty() ? listDescription : "(Empty)";
    }

    @Override
    protected void onStepOpened(boolean animated) {
        // This will be called automatically whenever the step gets opened.
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // This will be called automatically whenever the step gets closed.
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as completed.
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as uncompleted.
    }

    @Override
    protected void restoreStepData(String stepData) {
        // To restore the step after a configuration change.
        listTypeView.setSelection(0);
    }
}
