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

public class ReportResultFragment extends ReportFragment {
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private TextView searchResultTextView;

    public static ReportResultFragment newInstance(String lotNumber, String name, String category, String period, int signal) {
        ReportResultFragment fragment = new ReportResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LOT_NUMBER, lotNumber);
        args.putString(ARG_NAME, name);
        args.putString(ARG_CATEGORY, category);
        args.putString(ARG_PERIOD, period);
        args.putInt(ARG_SIGNAL, signal);
        fragment.setArguments(args);
        return fragment;
    }

    public static ReportResultFragment newInstance() {
        ReportResultFragment fragment = new ReportResultFragment();
        fragment.setArguments(null);
        return fragment;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            queryItem = new Item();
            queryItem.setLot(getArguments().getString(ARG_LOT_NUMBER, ""));
            queryItem.setName(getArguments().getString(ARG_NAME, ""));
            queryItem.setCategory(getArguments().getString(ARG_CATEGORY, ""));
            queryItem.setPeriod(getArguments().getString(ARG_PERIOD, ""));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        int signal = getArguments().getInt(ARG_SIGNAL);
        if (signal == 1) {
            view = inflater.inflate(R.layout.fragment_report_result, container, false);
        } else {
            view = inflater.inflate(R.layout.report_desc_img_only, container, false);
        }

        searchResultTextView = view.findViewById(R.id.searchResultTextView);
        Button buttonBack = view.findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new ReportFragment());
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        fillRecycler(recyclerView, signal);

        return view;
    }
}
