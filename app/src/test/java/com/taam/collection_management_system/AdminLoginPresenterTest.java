package com.taam.collection_management_system;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdminLoginPresenterTest {

    @Mock
    private AdminLoginView view;

    @Mock
    private AdminLoginModel model;

    @Mock
    private Context context;

    @Mock
    private Resources resources;

    @Mock
    private Drawable drawable;

    @Test
    public void testOnUsernameCheckboxChanged_Checked() {
        // Avoid NPE from
        when(context.getResources()).thenReturn(resources);
        when(view.getContext()).thenReturn(context);

        // Mockito complains about unused stubs BUT if I remove the unused stubs I get a NPE
        lenient().when(ContextCompat.getDrawable(context, R.drawable.admin_login_person_icon)).thenReturn(drawable);
        lenient().when(ContextCompat.getDrawable(context, R.drawable.admin_login_email_icon)).thenReturn(drawable);
        lenient().when(resources.getDrawable(R.drawable.admin_login_person_icon, null)).thenReturn(drawable);
        AdminLoginPresenter presenter = new AdminLoginPresenter(view, model);


        presenter.onUsernameCheckboxChanged(true);


        verify(view).setEmailHint("Username");
        verify(view).setEmailDrawable(drawable);

    }

    @Test
    public void testOnUsernameCheckboxChanged_Unchecked() {
        // Avoid NPE from
        when(context.getResources()).thenReturn(resources);
        when(view.getContext()).thenReturn(context);

        // Mockito complains about unused stubs BUT if I remove the unused stubs I get a NPE
        lenient().when(ContextCompat.getDrawable(context, R.drawable.admin_login_person_icon)).thenReturn(drawable);
        lenient().when(ContextCompat.getDrawable(context, R.drawable.admin_login_email_icon)).thenReturn(drawable);
        lenient().when(resources.getDrawable(R.drawable.admin_login_email_icon, null)).thenReturn(drawable);

        AdminLoginPresenter presenter = new AdminLoginPresenter(view, model);


        presenter.onUsernameCheckboxChanged(false);


        verify(view).setEmailHint("Email");
        verify(view).setEmailDrawable(drawable);

    }


    @Test
    public void testOnLoginButtonClicked_WithUsername() {
        AdminLoginPresenter presenter = new AdminLoginPresenter(view, model);
        presenter.isUsername = true;
        String username = "testuser";
        String password = "testpass";

        presenter.onLoginButtonClicked(username, password);

        verify(model).authenticateUsername(presenter, username, password);
    }

    @Test
    public void testOnLoginButtonClicked_WithEmail() {
        AdminLoginPresenter presenter = new AdminLoginPresenter(view, model);
        presenter.isUsername = false;
        String email = "test@example.com";
        String password = "testpass";

        presenter.onLoginButtonClicked(email, password);

        verify(model).authenticateEmail(presenter, email, password);
    }

    @Test
    public void testAdminEmailLogin_BothFieldsEmpty() {
        AdminLoginPresenter presenter = new AdminLoginPresenter(view, model);
        presenter.adminEmailLogin("", "");

        verify(view).displayMessage("Email and password cannot be empty");
    }

    @Test
    public void testAdminEmailLogin_PasswordEmpty() {
        AdminLoginPresenter presenter = new AdminLoginPresenter(view, model);
        presenter.adminEmailLogin("johndoe@yahoo.com", "");

        verify(view).displayMessage("Email and password cannot be empty");
    }

    @Test
    public void testAdminUsernameLogin_BothFieldsEmpty() {
        AdminLoginPresenter presenter = new AdminLoginPresenter(view, model);
        presenter.adminUsernameLogin("", "");

        verify(view).displayMessage("Username and password cannot be empty");
    }

    @Test
    public void testAdminUsernameLogin_PasswordEmpty() {
        AdminLoginPresenter presenter = new AdminLoginPresenter(view, model);
        presenter.adminUsernameLogin("John Doe", "");

        verify(view).displayMessage("Username and password cannot be empty");
    }

    @Test
    public void testAdminEmailLogin_NonEmptyFields() {
        String email = "test@example.com";
        String password = "testpass";
        AdminLoginPresenter presenter = new AdminLoginPresenter(view, model);

        presenter.adminEmailLogin(email, password);

        verify(model).authenticateEmail(presenter, email, password);
    }

    @Test
    public void testAdminUsernameLogin_NonEmptyFields() {
        String username = "testuser";
        String password = "testpass";
        AdminLoginPresenter presenter = new AdminLoginPresenter(view, model);
        presenter.adminUsernameLogin(username, password);

        verify(model).authenticateUsername(presenter, username, password);
    }

    @Test
    public void testSuccessfulAuthentication() {
        AdminLoginPresenter presenter = new AdminLoginPresenter(view, model);
        presenter.successfulAuthentication("Authentication Success");

        verify(view).displayMessage("Success");
        verify(view).goToAdminPage();
    }

    @Test
    public void testUnsuccessfulAuthentication_InvalidCredentials() {
        AdminLoginPresenter presenter = new AdminLoginPresenter(view, model);
        presenter.unsuccesssfulAuthentication("Invalid Credentials");

        verify(view).setErrorEditTextPassword("Invalid Credentials");
    }

    @Test
    public void testUnsuccessfulAuthentication_UserDoesNotExist() {
        AdminLoginPresenter presenter = new AdminLoginPresenter(view, model);
        presenter.unsuccesssfulAuthentication("User does not exist");

        verify(view).setErrorEditTextEmail("User does not exist");
    }

    @Test
    public void testUnsuccessfulAuthentication_OtherMessage() {
        String message = "Some other error";
        AdminLoginPresenter presenter = new AdminLoginPresenter(view, model);
        presenter.unsuccesssfulAuthentication(message);

        verify(view).displayMessage(message);
    }
}