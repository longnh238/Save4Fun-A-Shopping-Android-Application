package com.example.save4fun.fragment;

import android.os.Bundle;

import android.app.ProgressDialog;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.save4fun.R;
import com.example.save4fun.fragment.list.ListDescriptionStep;
import com.example.save4fun.fragment.list.ListNameStep;
import com.example.save4fun.fragment.list.ListTypeStep;

import ernestoyaquello.com.verticalstepperform.Step;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class ListAddFragment extends Fragment implements StepperFormListener {

    private ListNameStep listNameStep;
    private ListDescriptionStep listDescriptionStep;
    private ListTypeStep listTypeStep;

    private VerticalStepperFormView verticalStepperForm;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_add, container, false);

        listNameStep = new ListNameStep("Name");
        listDescriptionStep = new ListDescriptionStep("Description");
        listTypeStep = new ListTypeStep("Type");

        // Find the form view, set it up and initialize it.
        verticalStepperForm = view.findViewById(R.id.stepper_form);
        verticalStepperForm
                .setup(this, listNameStep, listDescriptionStep, listTypeStep)
                .displayBottomNavigation(false)
                .lastStepNextButtonText("Create List")
                .init();

        return view;
    }

    @Override
    public void onCompletedForm() {
        // This method will be called when the user clicks on the last confirmation button of the
        // form in an attempt to save or send the data.
        Bundle bundle = new Bundle();
        bundle.putString("name", listNameStep.getStepData());
        bundle.putString("description", listDescriptionStep.getStepData());
        bundle.putString("type", listTypeStep.getStepData());

        ListFragment listFragment = new ListFragment();
        listFragment.setArguments(bundle);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, listFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCancelledForm() {
        // This method will be called when the user clicks on the cancel button of the form.
    }

    @Override
    public void onStepAdded(int index, Step<?> addedStep) {
        // This will be called when a step is added dynamically through the form method addStep().
    }

    @Override
    public void onStepRemoved(int index) {
        // This will be called when a step is removed dynamically through the form method removeStep().
    }
}