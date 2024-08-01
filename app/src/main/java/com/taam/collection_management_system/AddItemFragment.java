package com.taam.collection_management_system;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import androidx.appcompat.app.AppCompatActivity;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import com.google.android.material.button.MaterialButton;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.Activity;



import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;


public class AddItemFragment extends Fragment {
    private EditText editTextName, editTextLot, editTextDescription;
    private Spinner spinnerCategory;
    private Spinner spinnerPeriod;
    private Button buttonAdd;

    private FirebaseDatabase db;

    private Button btnSelect;
    private ImageView imageView;
    private Uri video;
    private StorageReference storageReference;
    private LinearProgressIndicator progressIndicator;
    private MaterialButton selectVideo, uploadVideo;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextLot = view.findViewById(R.id.editTextLot);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerPeriod = view.findViewById(R.id.spinnerPeriod);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        imageView = view.findViewById(R.id.imageView);
        progressIndicator = view.findViewById(R.id.process);
        selectVideo = view.findViewById(R.id.selectVideo);

        db = FirebaseDatabase.getInstance("https://taam-management-system-default-rtdb.firebaseio.com/");

        FirebaseApp.initializeApp(requireContext());
        storageReference = FirebaseStorage.getInstance().getReference();

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        // Set up the spinner with categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        // Set up the spinner with periods
        ArrayAdapter<CharSequence> adapter_p = ArrayAdapter.createFromResource(getContext(),
                R.array.period_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriod.setAdapter(adapter_p);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        //set up Image Select

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {video = result.getData().getData();
                            if (video != null) {
                                Glide.with(getContext()).load(video).into(imageView);
                            } else {
                                Toast.makeText(getContext(), "No video selected.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Please select image/video.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        selectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("*/*");
                activityResultLauncher.launch(intent);
            }
        });



        return view;
    }


    private void addItem() {
        String name = editTextName.getText().toString().trim();
        String lot = editTextLot.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString().toLowerCase();
        String period = spinnerPeriod.getSelectedItem().toString().toLowerCase();

        if (name.isEmpty() || lot.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!lot.matches("-?\\d+")) {
            Toast.makeText(getContext(), "Lot must be a number!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (video == null) {
            Toast.makeText(getContext(), "Must upload photo/video!~", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference myRef = db.getReference("/collection_data");

        myRef.orderByChild("lot").equalTo(lot).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getContext(), "Lot number already exists.", Toast.LENGTH_SHORT).show();
                    return;
                }

                StorageReference reference = storageReference.child("gallery/" +
                        UUID.randomUUID().toString());
                reference.putFile(video).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String[] completeUrl = reference.toString().split("/");
                        String galleryUrl = completeUrl[4];

                        Item item = new Item(lot, name, category, period, description, galleryUrl);

                        myRef.child(lot).setValue(item).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to upload~", Toast.LENGTH_SHORT).show();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        progressIndicator.setVisibility(View.VISIBLE);
                        progressIndicator.setMax(Math.toIntExact(snapshot.getTotalByteCount()));
                        progressIndicator.setProgress(Math.toIntExact(snapshot.getBytesTransferred()));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}