package com.taam.collection_management_system;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Iterator;

public class ViewPageFragment extends Fragment {
    private static final String ARG_LOT_LIST = "lot_list";

    private TextView textViewLot, textViewName, textViewCategory, textViewPeriod, textViewDescription;
    private ImageView imageView;
    private VideoView videoView;
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private ArrayList<String> lotList;
    private ArrayList<Item> itemList;

    public static ViewPageFragment newInstance(ArrayList<String> lotList) {
        ViewPageFragment fragment = new ViewPageFragment();

        Bundle args = new Bundle();
        args.putStringArrayList(ARG_LOT_LIST, lotList);
        fragment.setArguments(args);

        return fragment;
    }

    public static ViewPageFragment newInstance() {
        ViewPageFragment fragment = new ViewPageFragment();
        fragment.setArguments(null);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lotList = getArguments().getStringArrayList(ARG_LOT_LIST);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_page, container, false);

        textViewLot = view.findViewById(R.id.textViewLot);
        textViewName = view.findViewById(R.id.textViewName);
        textViewCategory = view.findViewById(R.id.textViewCategory);
        textViewPeriod = view.findViewById(R.id.textViewPeriod);
        textViewDescription = view.findViewById(R.id.textViewDescription);

        imageView = view.findViewById(R.id.imageView);

        ImageButton buttonClose = view.findViewById(R.id.buttonClose);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        Spinner spinner = view.findViewById(R.id.spinnerItem);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, getStringRange(1, lotList.size() + 1));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                fillItemViews(itemList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView){}

        });

        ((TextView) view.findViewById(R.id.textViewItemCount)).setText(String.format(Locale.US, "%d", lotList.size()));

        fetchItemsFromDatabase();

        return view;
    }

    private void createSpinner() {

    }

    private void fetchItemsFromDatabase() {
        itemList = new ArrayList<>();

        db = FirebaseDatabase.getInstance("https://taam-management-system-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = db.getReference();
        itemsRef = db.getReference("collection_data/");

        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<String> lotIterator = lotList.iterator();
                String lot = lotIterator.next();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null && item.getLot().equalsIgnoreCase(lot)) {
                        itemList.add(item);
                        if (!lotIterator.hasNext()) { return; }
                        lot = lotIterator.next();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillItemViews(Item item) {
        textViewLot.setText(item.getLot());
        textViewName.setText(item.getName());
        textViewCategory.setText(item.getCategory());
        textViewPeriod.setText(item.getPeriod());
        textViewDescription.setText(item.getDescription());

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://taam-management-system.appspot.com/");
        StorageReference storageRef = storage.getReference().child("gallery/" + item.getGalleryUrl());

        storageRef.getDownloadUrl().addOnSuccessListener(url -> {
            Glide.with(getContext())
                    .load(url)
                    .into(imageView);
        }).addOnFailureListener(e -> {
            // Handle errors here
        });
    }

    private List<String> getStringRange(int start, int end) {
        List<String> range = new ArrayList<>();
        for (int i = start; i < end; i++) {
            range.add(" " + Integer.toString(i) + " ");
        }
        return range;
    }
}