package com.taam.collection_management_system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReportFragment extends TablePageFragment {
    private EditText etLotNumber, etName, etKeyword;
    private Spinner spinnerCategory, spinnerCategory2, spinnerPeriod, spinnerPeriod2;
    private ArrayAdapter<String> categoryAdapter, categoryAdapter2, periodAdapter, periodAdapter2;
    private List<String> categoryList, categoryList2, periodList, periodList2;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    private Button buttonSubmit;

    private EditText editTextLotNumber, editTextName, editTextCategory, editTextCategory2,
            editTextPeriod, editTextPeriod2;
    private String lot, name, category, category2, period, period2;

    protected ReportAdapter reportAdapter;
    protected ReportDescAdapter reportDescAdapter;
    private ReportResultFragment reportResultFragment;

    private DatabaseReference catReference, perReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_page, container, false);

        buttonSubmit = view.findViewById(R.id.buttonSubmit);

        //initialize checkboxes
        checkBoxList = new ArrayList<>();
        checkBoxList.add(view.findViewById(R.id.checkLot));
        checkBoxList.add(view.findViewById(R.id.checkName));
        checkBoxList.add(view.findViewById(R.id.checkCat));
        checkBoxList.add(view.findViewById(R.id.checkCatDP));
        checkBoxList.add(view.findViewById(R.id.checkPeriod));
        checkBoxList.add(view.findViewById(R.id.checkPeriodDP));
        checkBoxList.add(view.findViewById(R.id.checkAll));
        checkBoxList.add(view.findViewById(R.id.checkAllDp));

        //initialize text editors
        editTextLotNumber = view.findViewById(R.id.editTextLotNumber);
        editTextName = view.findViewById(R.id.editTextName);
        lot = "";
        name = "";

        //initialize spinners
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerCategory2 = view.findViewById(R.id.spinnerCategory2);
        spinnerPeriod = view.findViewById(R.id.spinnerPeriod);
        spinnerPeriod2 = view.findViewById(R.id.spinnerPeriod2);

        //initialize spinners choice list
        categoryList = new ArrayList<>();
        categoryList2 = new ArrayList<>();
        periodList = new ArrayList<>();
        periodList2 = new ArrayList<>();
        categoryList.add("");
        categoryList2.add("");
        periodList.add("");
        periodList2.add("");

        categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryAdapter2 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryList2);
        categoryAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        periodAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, periodList);
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodAdapter2 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, periodList2);
        periodAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory2.setAdapter(categoryAdapter2);
        spinnerPeriod.setAdapter(periodAdapter);
        spinnerPeriod2.setAdapter(periodAdapter2);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://taam-management-system-default-rtdb.firebaseio.com/");
        catReference = database.getReference("categories/");
        perReference = database.getReference("periods/");

        loadCategories();
        loadPeriods();

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getChecked() > 1 || getChecked() == 0) {
                    Toast.makeText(getContext(), "You must check only one box.", Toast.LENGTH_SHORT).show();
                } else {
                    generateReport();
                }
            }
        });
        return view;

    }


    protected void fillRecycler(RecyclerView recyclerView, int signal) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemList = new ArrayList<>();

        if (signal == 1) {
            reportAdapter = new ReportAdapter(itemList);
            recyclerView.setAdapter(reportAdapter);
        } else {
            reportDescAdapter = new ReportDescAdapter(itemList);
            recyclerView.setAdapter(reportDescAdapter);
        }

        db = FirebaseDatabase.getInstance("https://taam-management-system-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = db.getReference();
        fetchItemsFromDatabase(signal);
    }

    protected void fetchItemsFromDatabase(int signal) {
        itemsRef = db.getReference("collection_data/");
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null && queryItem == null ||
                            (queryItem.getLot().isEmpty() || item.getLot().toLowerCase().equals(queryItem.getLot().toLowerCase())) &&
                                    (queryItem.getName().isEmpty() || item.getName().toLowerCase().contains(queryItem.getName().toLowerCase())) &&
                                    (queryItem.getCategory().isEmpty() || item.getCategory().toLowerCase().equals(queryItem.getCategory().toLowerCase())) &&
                                    (queryItem.getPeriod().isEmpty() || item.getPeriod().toLowerCase().equals(queryItem.getPeriod().toLowerCase()))) {
                        itemList.add(item);
                    }
                }
                if (signal == 1) {
                    reportAdapter.notifyDataSetChanged();
                } else {
                    reportDescAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private int getChecked() {
        int counter = 0;
        for (CheckBox checkBox : checkBoxList) {
            if ((checkBox.isChecked())) {
                counter++;
            }
        }
        return counter;
    }

    private void loadCategories() {
        catReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                categoryList2.clear();
                // Add an empty item for default value
                categoryList.add("");
                categoryList2.add("");
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String category = ds.child("category").getValue(String.class);
                    if (category != null) {
                        categoryList.add(category);
                        categoryList2.add(category);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
                categoryAdapter2.notifyDataSetChanged();
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
                periodList2.clear();
                // Add an empty item for default value
                periodList.add("");
                periodList2.add("");
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String period = ds.child("period").getValue(String.class);
                    if (period != null) {
                        periodList.add(period);
                        periodList2.add(period);
                    }
                }
                periodAdapter.notifyDataSetChanged();
                periodAdapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load periods", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateReport() {
        int signal = 0;
        for (int i = 0; i < checkBoxList.size(); i++) {
            if (checkBoxList.get(i).isChecked()) {
                switch (i) {
                    case 0:
                        lot = editTextLotNumber.getText().toString().trim();
                        if (lot.isEmpty()) {
                            Toast.makeText(getContext(), "Please fill out the field.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        signal = 1;
                        break;
                    case 1:
                        name = editTextName.getText().toString().trim();
                        if (name.isEmpty()) {
                            Toast.makeText(getContext(), "Please fill out the field.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        signal = 1;
                        break;
                    case 2:
                        category = spinnerCategory.getSelectedItem().toString().trim();
                        if (category.isEmpty()) {
                            Toast.makeText(getContext(), "Please select a category.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        signal = 1;
                        break;
                    case 3:
                        category = spinnerCategory2.getSelectedItem().toString().trim();
                        if (category.isEmpty()) {
                            Toast.makeText(getContext(), "Please select a category.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        signal = 2;
                        break;
                    case 4:
                        period = spinnerPeriod.getSelectedItem().toString().trim();
                        if (period.isEmpty()) {
                            Toast.makeText(getContext(), "Please select a period.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        signal = 1;
                        break;
                    case 5:
                        period = spinnerPeriod2.getSelectedItem().toString().trim();
                        if (period.isEmpty()) {
                            Toast.makeText(getContext(), "Please select a period.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        signal = 2;
                        break;
                    case 6:
                        signal = 1;
                        break;
                    case 7:
                        signal = 2;
                        break;
                }
                if (signal != 0) {
                    reportResultFragment = ReportResultFragment.newInstance(lot, name, category, period, signal);
                    loadFragment(reportResultFragment);
                } else {
                    Toast.makeText(getContext(), "Error.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

}
