package com.taam.collection_management_system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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

public class SearchResultFragment extends TablePageFragment {
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
            queryItem = new Item();
            queryItem.setLot(getArguments().getString(ARG_LOT_NUMBER, ""));
            queryItem.setName(getArguments().getString(ARG_NAME, ""));
            queryItem.setCategory(getArguments().getString(ARG_CATEGORY, ""));
            queryItem.setPeriod(getArguments().getString(ARG_PERIOD, ""));
            queryItem.setDescription(getArguments().getString(ARG_KEYWORD, ""));
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

    private boolean isAnySearchParameterFilled() {
        return (!queryItem.getLot().isEmpty() || !queryItem.getName().isEmpty() ||
                !queryItem.getCategory().isEmpty() || !queryItem.getPeriod().isEmpty()) ||
                !queryItem.getDescription().isEmpty();
    }

    private void updateSearchResultTextView() {
        String lotText = queryItem.getLot().isEmpty() ? "_" : queryItem.getLot();
        String nameText = queryItem.getName().isEmpty() ? "_" : queryItem.getName();
        String categoryText = queryItem.getCategory().isEmpty() ? "_" : queryItem.getCategory();
        String periodText = queryItem.getPeriod().isEmpty() ? "_" : queryItem.getPeriod();
        String keywordText = queryItem.getDescription().isEmpty() ? "_" : queryItem.getDescription();

        String resultText = String.format("Result based on\nLot#%s  Name:%s  Category:%s  Period:%s  Keyword:%s",
                lotText, nameText, categoryText, periodText, keywordText);

        searchResultTextView.setText(resultText);
    }

}
