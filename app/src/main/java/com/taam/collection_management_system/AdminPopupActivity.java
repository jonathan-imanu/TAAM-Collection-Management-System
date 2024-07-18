package com.taam.collection_management_system;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminPopupActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login_popup);

        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUsername = findViewById(R.id.editTextUsername);
        buttonLogin = findViewById(R.id.buttonLogin);

        db = FirebaseDatabase.getInstance("https://taam-management-system-default-rtdb.firebaseio.com/");
        itemsRef = db.getReference("items");

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminLogin();
            }
        });
    }

    private void adminLogin() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if ( username.isEmpty() || password.isEmpty()) {
            Toast.makeText(AdminPopupActivity.this, "Please fill out both fields.", Toast.LENGTH_SHORT).show();
        }


    }
}
