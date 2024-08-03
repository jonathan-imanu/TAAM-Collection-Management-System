package com.taam.collection_management_system;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

public class AdminLoginPresenter {
    public AdminLoginView view;
    public AdminLoginModel model;
    public boolean isUsername;

    public AdminLoginPresenter(AdminLoginView view, AdminLoginModel model) {
        this.view = view;
        this.model = model;
        this.isUsername = false;
    }

    public void onUsernameCheckboxChanged(boolean isChecked) {
        isUsername = isChecked;
        if (isChecked) {
            view.setEmailHint("Username");
            view.setEmailDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.admin_login_person_icon));
        } else {
            view.setEmailHint("Email");
            view.setEmailDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.admin_login_email_icon));
        }
    }

    public void onLoginButtonClicked(String identifier, String password) {
        if (isUsername) {
            adminUsernameLogin(identifier, password);
        } else {
            adminEmailLogin(identifier, password);
        }
    }

    public void adminEmailLogin(String email, String password) {

        if (email.isEmpty() ||password.isEmpty()) {
            view.displayMessage("Email and password cannot be empty");
            return;
        }
        model.authenticateEmail(this, email, password);
    }

    public void adminUsernameLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            view.displayMessage("Username and password cannot be empty");
            return;
        }

        model.authenticateUsername(this, username, password);
    }

    public void successfulAuthentication(String message) {
        view.displayMessage("Success");
        view.goToAdminPage();
    }

    public void unsuccesssfulAuthentication(String message) {
        if(message.equals("Invalid Credentials")) {
            view.setErrorEditTextPassword(message);
        }
        else if(message.equals("User does not exist")) {
            view.setErrorEditTextEmail(message);
        }
        else {
            view.displayMessage(message);
        }
    }


}