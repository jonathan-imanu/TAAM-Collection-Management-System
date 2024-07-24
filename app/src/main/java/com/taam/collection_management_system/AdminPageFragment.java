package com.taam.collection_management_system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdminPageFragment extends TablePageFragment {

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
            public void onClick(View v) { loadFragment(new DeleteItemFragment()); }
        });

        fillRecycler(view.findViewById(R.id.recyclerView));

        return view;
    }

}
