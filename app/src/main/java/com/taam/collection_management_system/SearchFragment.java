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
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private EditText etLotNumber, etName, etKeyword;
    private Spinner spinnerCategory, spinnerPeriod;
    private ArrayAdapter<String> categoryAdapter, periodAdapter;
    private List<String> categoryList, periodList;

    private DatabaseReference catReference, perReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etLotNumber = view.findViewById(R.id.etLotNumber);
        etName = view.findViewById(R.id.etName);
        etKeyword = view.findViewById(R.id.etKeyword);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerPeriod = view.findViewById(R.id.spinnerPeriod);
        Button btnSearch = view.findViewById(R.id.btnSearch);

        categoryList = new ArrayList<>();
        periodList = new ArrayList<>();

        // Add an empty item for default value
        categoryList.add("");
        periodList.add("");

        categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, periodList);
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCategory.setAdapter(categoryAdapter);
        spinnerPeriod.setAdapter(periodAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://taam-management-system-default-rtdb.firebaseio.com/");
        catReference = database.getReference("categories/");
        perReference = database.getReference("periods/");

        loadCategories();
        loadPeriods();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lotNumber = etLotNumber.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String category = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString().trim() : "";
                String period = spinnerPeriod.getSelectedItem() != null ? spinnerPeriod.getSelectedItem().toString().trim() : "";
                String keyword = etKeyword.getText().toString().trim();

                if (lotNumber.isEmpty() && name.isEmpty() && category.isEmpty() && period.isEmpty() && keyword.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill out at least one field.", Toast.LENGTH_SHORT).show();
                } else {
                    SearchResultFragment searchResultFragment = SearchResultFragment.newInstance(lotNumber, name, category, period, keyword);
                    loadFragment(searchResultFragment);
                }
            }
        });

        return view;
    }

    private void loadCategories() {
        catReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                // Add an empty item for default value
                categoryList.add("");
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String category = ds.child("category").getValue(String.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPeriods() {
        perReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                periodList.clear();
                // Add an empty item for default value
                periodList.add("");
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String period = ds.child("period").getValue(String.class);
                    if (period != null) {
                        periodList.add(period);
                    }
                }
                periodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load periods", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
