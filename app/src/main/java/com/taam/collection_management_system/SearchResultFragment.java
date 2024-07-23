package com.taam.collection_management_system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

public class SearchResultFragment extends Fragment {
    private static final String ARG_LOT_NUMBER = "lot_number";
    private static final String ARG_NAME = "name";
    private static final String ARG_CATEGORY = "category";
    private static final String ARG_PERIOD = "period";
    private static final String ARG_KEYWORD = "keyword";

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private String lotNumber;
    private String name;
    private String category;
    private String period;
    private String keyword;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private TextView searchResultTextView;

    public static SearchResultFragment newInstance(String lotNumber, String name, String category, String period, String keyword) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LOT_NUMBER, lotNumber);
        args.putString(ARG_NAME, name);
        args.putString(ARG_CATEGORY, category);
        args.putString(ARG_PERIOD, period);
        args.putString(ARG_KEYWORD, keyword);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lotNumber = getArguments().getString(ARG_LOT_NUMBER, "");
            name = getArguments().getString(ARG_NAME, "");
            category = getArguments().getString(ARG_CATEGORY, "");
            period = getArguments().getString(ARG_PERIOD, "");
            keyword = getArguments().getString(ARG_KEYWORD, "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        searchResultTextView = view.findViewById(R.id.searchResultTextView);
        Button buttonBack = view.findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new SearchFragment());
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        fillRecycler(recyclerView);
        updateSearchResultTextView();

        return view;
    }

    private void fetchItemsFromDatabase() {
        itemsRef = db.getReference("collection_data/");
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null &&
                            (lotNumber.isEmpty() || item.getLot().equals(lotNumber)) &&
                            (name.isEmpty() || item.getName().contains(name)) &&
                            (category.isEmpty() || item.getCategory().equals(category)) &&
                            (period.isEmpty() || item.getPeriod().equals(period)) &&
                            (keyword.isEmpty() || item.getDescription().contains(keyword))) {
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

    private void fillRecycler(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);

        db = FirebaseDatabase.getInstance("https://taam-management-system-default-rtdb.firebaseio.com/");
        if (isAnySearchParameterFilled()) {
            fetchItemsFromDatabase();
        } else {
            itemList.clear();
            itemAdapter.notifyDataSetChanged();
        }
    }

    private boolean isAnySearchParameterFilled() {
        return !lotNumber.isEmpty() || !name.isEmpty() || !category.isEmpty() || !period.isEmpty() || !keyword.isEmpty();
    }

    private void updateSearchResultTextView() {
        String lotText = lotNumber.isEmpty() ? "_" : lotNumber;
        String nameText = name.isEmpty() ? "_" : name;
        String categoryText = category.isEmpty() ? "_" : category;
        String periodText = period.isEmpty() ? "_" : period;
        String keywordText = keyword.isEmpty() ? "_" : keyword;

        String resultText = String.format("Result based on\nLot#%s  Name:%s  Category:%s  Period:%s  Keyword:%s",
                lotText, nameText, categoryText, periodText, keywordText);

        searchResultTextView.setText(resultText);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
