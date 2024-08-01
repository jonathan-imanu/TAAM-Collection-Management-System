package com.taam.collection_management_system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

abstract public class TablePageFragment extends Fragment {
    protected static final String ARG_LOT_NUMBER = "lot_number";
    protected static final String ARG_NAME = "name";
    protected static final String ARG_CATEGORY = "category";
    protected static final String ARG_PERIOD = "period";
    protected static final String ARG_KEYWORD = "keyword";
    protected static final String ARG_SIGNAL = "signal";

    protected RecyclerView recyclerView;
    protected ItemAdapter itemAdapter;
    protected List<Item> itemList;
    protected List<CheckBox> checkBoxList;
    protected Item queryItem;
    protected FirebaseDatabase db;
    protected DatabaseReference itemsRef;

    @Nullable
    @Override
    abstract public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    protected void fetchItemsFromDatabase() {
        itemsRef = db.getReference("collection_data/");
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                checkBoxList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null && queryItem == null ||
                            (queryItem.getLot().isEmpty() || item.getLot().toLowerCase().equals(queryItem.getLot().toLowerCase())) &&
                            (queryItem.getName().isEmpty() || item.getName().toLowerCase().contains(queryItem.getName().toLowerCase())) &&
                            (queryItem.getCategory().isEmpty() || item.getCategory().toLowerCase().equals(queryItem.getCategory().toLowerCase())) &&
                            (queryItem.getPeriod().isEmpty() || item.getPeriod().toLowerCase().equals(queryItem.getPeriod().toLowerCase())) &&
                            (queryItem.getDescription().isEmpty() || item.getDescription().toLowerCase().contains(queryItem.getDescription().toLowerCase()))) {
                        itemList.add(item);
                    }
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    protected void fillRecycler( RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();
        checkBoxList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList, checkBoxList);
        recyclerView.setAdapter(itemAdapter);

        db = FirebaseDatabase.getInstance("https://taam-management-system-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = db.getReference();
        fetchItemsFromDatabase();
    }

    protected void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected List<String> getSelectedLot() {
        List<String> selectedLot = new ArrayList<String>();
        for (int i = 0; i < itemList.size(); i++) {
            if (checkBoxList.get(i).isChecked()) {
                selectedLot.add(itemList.get(i).getLot());
            }
        }
        return selectedLot;
    }
}
