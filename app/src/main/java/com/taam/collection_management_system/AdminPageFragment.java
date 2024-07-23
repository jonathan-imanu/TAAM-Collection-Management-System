package com.taam.collection_management_system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

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

public class AdminPageFragment extends NavigationPageFragment {

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

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new MainPageFragment());
            }
        });
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new MainPageFragment());
            }
        });
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new MainPageFragment());
            }
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
            public void onClick(View v) { loadFragment(new DeleteItemFragment()); }
        });

        fillRecycler(view.findViewById(R.id.recyclerView));

        return view;
    }

}
