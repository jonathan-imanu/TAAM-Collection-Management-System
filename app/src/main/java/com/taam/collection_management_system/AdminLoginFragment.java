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
import androidx.core.content.ContextCompat;

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


import java.util.concurrent.Executor;


public class AdminLoginFragment extends Fragment {
    private EditText editTextEmail,editTextPassword;
    private Button buttonLogin;
    private CheckBox checkboxIsUsername;
    private  boolean isUsername = false;
    private Drawable drawableEmail;
    private Drawable drawablePerson;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_login, container, false);

        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        checkboxIsUsername = view.findViewById(R.id.checkboxIsUsername);
        drawableEmail = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_email_24);
        drawablePerson = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_person_24);


        mAuth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance("https://taam-management-system-default-rtdb.firebaseio.com/");
        itemsRef = db.getReference("users");

        checkboxIsUsername.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                editTextEmail.setHint("Username");
                editTextEmail.setCompoundDrawablesWithIntrinsicBounds(drawablePerson, null, null, null);
                isUsername=true;
            } else {
                editTextEmail.setHint("Email");
                editTextEmail.setCompoundDrawablesWithIntrinsicBounds(drawableEmail, null, null, null);
                isUsername=false;
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUsername)
                    adminUsernameLogin();
                else
                    adminEmailLogin();
            }
        });

        return view;
    }

    private void updateUI() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new AddItemFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void adminUsernameLogin() {
        String userUsername = editTextEmail.getText().toString().trim();
        String userPassword = editTextPassword.getText().toString().trim();

        if (userUsername.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out both fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        Query checkUserDatabase = itemsRef.orderByChild("user").equalTo(userUsername);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String passwordFromDB = null;
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        passwordFromDB = childSnapshot.child("password").getValue(String.class);
                        break;
                    }

                    if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                        updateUI();
                    } else {
                        editTextPassword.setError("Invalid Credentials");
                        editTextPassword.requestFocus();
                    }
                } else {
                    editTextEmail.setError("User does not exist");
                    editTextEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void adminEmailLogin() {
        String userEmail = editTextEmail.getText().toString().trim();
        String userPassword = editTextPassword.getText().toString().trim();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out both fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Authentication Success.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}