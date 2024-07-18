package com.taam.collection_management_system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddItemFragment extends Fragment {
    private EditText editTextName, editTextLot, editTextCategory, editTextPeriod, editTextDescription;
    private Spinner spinnerCategory;
    private Button buttonAdd;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        editTextName= view.findViewById(R.id.editTextName);
        editTextLot = view.findViewById(R.id.editTextLot);
        editTextCategory = view.findViewById(R.id.editTextCategory);
        editTextPeriod = view.findViewById(R.id.editTextPeriod);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        buttonAdd = view.findViewById(R.id.buttonAdd);

        db = FirebaseDatabase.getInstance("https://taam-management-system-default-rtdb.firebaseio.com/");

        // Set up the spinner with categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        return view;
    }

    private void addItem() {
        String name = editTextName.getText().toString().trim();
        String lot = editTextLot.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String category = editTextCategory.getText().toString().trim();
        String period = editTextPeriod.getText().toString().trim();
        //String category = spinnerCategory.getSelectedItem().toString().toLowerCase();
        //String period = spinnerCategory.getSelectedItem().toString().toLowerCase();
        //Upload file here, maybe a link?

        if (name.isEmpty() || lot.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference myRef = db.getReference();
        //myRef.child("collection_data").child("1").child("Category").setValue("a");
        itemsRef = db.getReference();
        String id = itemsRef.push().getKey();
        Item item = new Item(id, lot, name, category, period, description);

        itemsRef.child(id).setValue(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        });
    }
}