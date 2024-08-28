package com.example.save4fun.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.save4fun.MainActivity;
import com.example.save4fun.R;
import com.example.save4fun.db.DBUsersHelper;
import com.example.save4fun.model.User;
import com.example.save4fun.util.Constant;

import java.util.Calendar;

public class ProfileFragment extends Fragment {

    DBUsersHelper dbUsersHelper;
    TextView textViewUsername;
    EditText textViewFirstNameContent, textViewLastNameContent, textViewBirthdayContent, textViewEmailContent, textViewPhoneContent;
    Button buttonUpdateProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constant.PREFERENCES_NAME, 0);
        boolean hasLoggedIn = sharedPreferences.getBoolean(Constant.HAS_LOGGED_IN, false);
        String username = sharedPreferences.getString(Constant.USERNAME, "");

        dbUsersHelper = new DBUsersHelper(getContext());

        textViewUsername = view.findViewById(R.id.textViewUsername);
        textViewFirstNameContent = view.findViewById(R.id.textViewFirstNameContent);
        textViewLastNameContent = view.findViewById(R.id.textViewLastNameContent);
        textViewBirthdayContent = view.findViewById(R.id.textViewBirthdayContent);
        textViewEmailContent = view.findViewById(R.id.textViewEmailContent);
        textViewPhoneContent = view.findViewById(R.id.textViewPhoneContent);

        buttonUpdateProfile = view.findViewById(R.id.buttonUpdateProfile);
        buttonUpdateProfile.setEnabled(false);

        if (hasLoggedIn && !username.isEmpty()) {
            User user = dbUsersHelper.getUserByUsername(username);
            if (user != null) {
                textViewUsername.setText(user.getUsername());
                textViewFirstNameContent.setText(user.getFirstName());
                textViewLastNameContent.setText(user.getLastName());
                textViewBirthdayContent.setText(user.getBirthday());
                textViewEmailContent.setText(user.getEmail());
                textViewPhoneContent.setText(user.getPhone());
            }
        } else {
            sharedPreferences.edit().remove(Constant.HAS_LOGGED_IN).apply();
            sharedPreferences.edit().remove(Constant.USERNAME).apply();

            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }

        textViewFirstNameContent.addTextChangedListener(textWatcher);
        textViewLastNameContent.addTextChangedListener(textWatcher);

        textViewBirthdayContent.addTextChangedListener(textWatcher);
        // textViewBirthdayContent.setInputType(InputType.TYPE_NULL);
        textViewBirthdayContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();

                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                if(!textViewBirthdayContent.getText().toString().isEmpty()) {
                    String selectedTime[] = textViewBirthdayContent.getText().toString().split("/");
                    currentYear = Integer.parseInt(selectedTime[2]);
                    currentMonth = Integer.parseInt(selectedTime[1]) - 1;
                    currentDay = Integer.parseInt(selectedTime[0]);
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                textViewBirthdayContent.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        },
                        currentYear, currentMonth, currentDay);
                datePickerDialog.show();
            }
        });

        textViewEmailContent.addTextChangedListener(textWatcher);
        textViewPhoneContent.addTextChangedListener(textWatcher);

        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = textViewFirstNameContent.getText().toString().trim();
                String lastName = textViewLastNameContent.getText().toString().trim();
                String birthday = textViewBirthdayContent.getText().toString().trim();
                String email = textViewEmailContent.getText().toString().trim();
                String phone = textViewPhoneContent.getText().toString().trim();

                User user = new User(firstName, lastName, birthday, email, phone);
                user.setUsername(username);
                dbUsersHelper.updateUser(user);

                textViewFirstNameContent.clearFocus();
                textViewLastNameContent.clearFocus();
                textViewBirthdayContent.clearFocus();
                textViewEmailContent.clearFocus();
                textViewPhoneContent.clearFocus();

                v.requestFocus();
                Toast.makeText(getActivity(), "Updated profile successfully", Toast.LENGTH_SHORT).show();
                buttonUpdateProfile.setEnabled(false);
            }
        });

        return view;
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String firstName = textViewFirstNameContent.getText().toString().trim();
            String lastName = textViewLastNameContent.getText().toString().trim();
            String birthday = textViewBirthdayContent.getText().toString().trim();
            String email = textViewEmailContent.getText().toString().trim();
            String phone = textViewPhoneContent.getText().toString().trim();

            buttonUpdateProfile.setEnabled(!firstName.isEmpty() || !lastName.isEmpty()
                    || !birthday.isEmpty() || !email.isEmpty() || !phone.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}