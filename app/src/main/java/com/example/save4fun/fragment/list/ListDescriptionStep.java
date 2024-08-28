package com.example.save4fun.fragment.list;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import ernestoyaquello.com.verticalstepperform.Step;

public class ListDescriptionStep extends Step<String> {
    private EditText listDescriptionView;

    public ListDescriptionStep(String stepTitle) {
        super(stepTitle);
    }

    @Override
    protected View createStepContentLayout() {
        listDescriptionView = new EditText(getContext());
        listDescriptionView.setSingleLine(true);
        listDescriptionView.setHint("Your list description");

        listDescriptionView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                markAsCompletedOrUncompleted(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return listDescriptionView;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        boolean isDescriptionValid = true;
        String errorMessage = !isDescriptionValid ? "3 characters minimum" : "";

        return new IsDataValid(isDescriptionValid, errorMessage);
    }

    @Override
    public String getStepData() {
        Editable userName = listDescriptionView.getText();
        return userName != null ? userName.toString() : "";
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
        // To restore the step after a configuration change, we restore the text of its EditText view.
        listDescriptionView.setText(stepData);
    }
}
