package com.taam.collection_management_system;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginModel {
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private FirebaseAuth mAuth;

    public AdminLoginModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance("https://taam-management-system-default-rtdb.firebaseio.com/");
        itemsRef = db.getReference("users");
    }

    public void authenticateUsername(AdminLoginPresenter presenter, String username, String password) {
        Query checkUserDatabase = itemsRef.orderByChild("user").equalTo(username);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String passwordFromDB = null;
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        passwordFromDB = childSnapshot.child("password").getValue(String.class);
                        break;
                    }

                    if (passwordFromDB != null && passwordFromDB.equals(password)) {
                        presenter.successfulAuthentication("Authentication Success");
                    } else {
                        presenter.unsuccesssfulAuthentication("Invalid Credentials");
                    }
                } else {
                    presenter.unsuccesssfulAuthentication("User does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                presenter.unsuccesssfulAuthentication("Database error: " + error.getMessage());
            }
        });
    }

    public void authenticateEmail(AdminLoginPresenter presenter, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            presenter.successfulAuthentication("Authentication Success");
                        } else {
                            presenter.unsuccesssfulAuthentication("User does not exist");
                        }
                    }
                });
    }

}