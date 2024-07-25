package com.taam.collection_management_system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Dialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdminPageFragment extends TablePageFragment {

    //for Delete pop-up
    private Dialog dialog;
    private Button buttonCancel, buttonDel;
    private TextView selectedLots;
    private List<String> selected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_page, container, false);

        // Three Main Screen Entries
        ImageButton buttonView = view.findViewById(R.id.buttonView);
        ImageButton buttonSearch = view.findViewById(R.id.buttonSearch);
        ImageButton buttonHome = view.findViewById(R.id.buttonHome);

        // Three Admin Functionalities
        ImageButton buttonDelete = view.findViewById(R.id.buttonDelete);
        ImageButton buttonAdd = view.findViewById(R.id.buttonAdd);
        ImageButton buttonReport = view.findViewById(R.id.buttonReport);


        //for Delete Popup
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.fragment_delete_item);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        selectedLots = dialog.findViewById(R.id.selectedLots);

        buttonDel = dialog.findViewById(R.id.buttonDel);
        buttonCancel = dialog.findViewById(R.id.buttonCancel);

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { loadFragment(new MainPageFragment()); }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { loadFragment(new SearchFragment()); }
        });
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { loadFragment(new MainPageFragment()); }
        });
        buttonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { loadFragment(new MainPageFragment()); }
        });
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { loadFragment(new AddItemFragment()); }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = getSelectedLot();
                if (selected.size() == 0) {
                    Toast.makeText(getContext(), "You must select an item.", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.show();
                    StringBuilder selectedItems = new StringBuilder("Lot# of records selected:\n");
                    for (String item : selected) {
                        selectedItems.append(item).append("\n");
                    }
                    selectedLots.setText(selectedItems.toString());
                }
            }
        });
        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItemByTitle(selected);
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        fillRecycler(view.findViewById(R.id.recyclerView));

        return view;
    }


    private void deleteItemByTitle(List<String> selectedItems) {

        itemsRef = db.getReference("collection_data/");
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < selectedItems.size(); i++) {
                    boolean itemFound = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Item item = snapshot.getValue(Item.class);
                        if (item != null && item.getLot().equalsIgnoreCase(selectedItems.get(i))) {
                            snapshot.getRef().removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getContext(), "Failed to delete item", Toast.LENGTH_SHORT).show();
                                }
                            });
                            itemFound = true;
                            break;
                        }
                    }
                    if (!itemFound) {
                        Toast.makeText(getContext(), "Item not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
