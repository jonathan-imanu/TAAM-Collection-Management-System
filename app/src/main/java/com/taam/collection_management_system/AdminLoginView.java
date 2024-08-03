package com.taam.collection_management_system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.graphics.drawable.Drawable;

public class AdminLoginView extends Fragment {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private CheckBox checkboxIsUsername;
    private Drawable drawableEmail;
    private Drawable drawablePerson;
    private AdminLoginPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_login, container, false);

        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        checkboxIsUsername = view.findViewById(R.id.checkboxIsUsername);

        presenter = new AdminLoginPresenter(this, new AdminLoginModel());

        checkboxIsUsername.setOnCheckedChangeListener((buttonView, isChecked) -> {
            presenter.onUsernameCheckboxChanged(isChecked);
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onLoginButtonClicked(editTextEmail.getText().toString().trim(),
                        editTextPassword.getText().toString().trim());
            }
        });

        return view;
    }

    public void goToAdminPage() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new AdminPageFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void displayMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void setEmailHint(String hint) {
        editTextEmail.setHint(hint);
    }

    public void setEmailDrawable(Drawable drawable) {
        editTextEmail.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    public void setErrorEditTextEmail(String error) {
        editTextEmail.setError(error);
        editTextEmail.requestFocus();
    }

    public void setErrorEditTextPassword(String error) {
        editTextPassword.setError(error);
        editTextPassword.requestFocus();
    }



}