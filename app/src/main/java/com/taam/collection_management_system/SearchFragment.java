package com.taam.collection_management_system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class SearchFragment extends Fragment {
    private EditText etLotNumber, etName, etCategory, etPeriod, etKeyword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etLotNumber = view.findViewById(R.id.etLotNumber);
        etName = view.findViewById(R.id.etName);
        etCategory = view.findViewById(R.id.etCategory);
        etPeriod = view.findViewById(R.id.etPeriod);
        etKeyword = view.findViewById(R.id.etKeyword);
        Button btnSearch = view.findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lotNumber = etLotNumber.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String category = etCategory.getText().toString().trim();
                String period = etPeriod.getText().toString().trim();
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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
