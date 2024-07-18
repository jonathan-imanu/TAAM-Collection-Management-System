package com.taam.collection_management_system;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
        itemsRef = db.getReference("users");

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminLogin();
            }
        });
    }

    private void adminLogin() {
        String userUsername = editTextUsername.getText().toString().trim();
        String userPassword = editTextPassword.getText().toString().trim();

        if (userUsername.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(AdminPopupActivity.this, "Please fill out both fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        Query checkUserDatabase = itemsRef.orderByChild("user").equalTo(userUsername);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String passwordFromDB = null;
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        if (childSnapshot.child("user").getValue(String.class).equals(userUsername)) {
                            passwordFromDB = childSnapshot.child("password").getValue(String.class);
                            break;
                        }
                    }

                    if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {

                        editTextUsername.setError(null);
                        String usernameFromDB = userUsername;
                        Intent intent = new Intent(AdminPopupActivity.this, MainActivity.class);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("password", passwordFromDB);
                        startActivity(intent);
                    } else {

                        editTextPassword.setError("Invalid Credentials");
                        editTextPassword.requestFocus();
                    }
                } else {

                    editTextUsername.setError("User does not exist" + userUsername);
                    editTextUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO!
                // EROROR: Handling

            }
        });

    }
}
