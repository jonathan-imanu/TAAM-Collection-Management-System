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

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    private Button buttonSubmit;

    private EditText editTextLotNumber, editTextName, editTextCategory, editTextCategory2,
            editTextPeriod, editTextPeriod2;
    private String lot, name, category, category2, period, period2;

    protected ReportAdapter reportAdapter;
    private ReportResultFragment reportResultFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_page, container, false);


        //initialize checkboxes
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        checkBoxList = new ArrayList<>();
        checkBoxList.add(view.findViewById(R.id.checkLot));
        checkBoxList.add(view.findViewById(R.id.checkName));
        checkBoxList.add(view.findViewById(R.id.checkCat));
        checkBoxList.add(view.findViewById(R.id.checkCatDp));
        checkBoxList.add(view.findViewById(R.id.checkPeriod));
        checkBoxList.add(view.findViewById(R.id.checkPeriodDp));
        checkBoxList.add(view.findViewById(R.id.checkAll));
        checkBoxList.add(view.findViewById(R.id.checkAllDp));

        //initialize text editors
        editTextLotNumber = view.findViewById(R.id.editTextLotNumber);
        editTextName = view.findViewById(R.id.editTextName);
        editTextCategory = view.findViewById(R.id.editTextCategory);
        editTextCategory2 = view.findViewById(R.id.editTextCategory2);
        editTextPeriod = view.findViewById(R.id.editTextPeriod);
        editTextPeriod2 = view.findViewById(R.id.editTextPeriod2);
        name = editTextName.getText().toString().trim();
        category = editTextCategory.getText().toString().trim();
        category2 = editTextCategory2.getText().toString().trim();
        period = editTextPeriod.getText().toString().trim();
        period2 = editTextPeriod2.getText().toString().trim();



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

    @Override
    protected void fillRecycler(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemList = new ArrayList<>();
        reportAdapter = new ReportAdapter(itemList);
        recyclerView.setAdapter(reportAdapter);

        db = FirebaseDatabase.getInstance("https://taam-management-system-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = db.getReference();
        fetchItemsFromDatabase();
    }

    @Override
    protected void fetchItemsFromDatabase() {
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
                reportAdapter.notifyDataSetChanged();
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
                        category = editTextCategory.getText().toString().trim();
                        if (category.isEmpty()) {
                            Toast.makeText(getContext(), "Please fill out the field.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        signal = 1;
                        break;
                    case 3:
                        //desc pic only category
                        break;
                    case 4:
                        period = editTextPeriod.getText().toString().trim();
                        if (period.isEmpty()) {
                            Toast.makeText(getContext(), "Please fill out the field.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        signal = 1;
                        break;
                    case 5:
                        //desc pic only period
                        break;
                    case 6:
                        signal = 1;
                        break;
                    case 7:
                        //desc pic only all
                        break;

                }
                if (signal == 1) {
                    reportResultFragment = ReportResultFragment.newInstance(lot, name, category, period);
                    loadFragment(reportResultFragment);
                } else if (signal == 2) {
                    //run desc and pic only
                }
                break;
            }
        }
    }
}
