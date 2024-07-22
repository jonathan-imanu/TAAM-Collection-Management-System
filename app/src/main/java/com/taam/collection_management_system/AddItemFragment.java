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
import android.app.Dialog;



import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.DeterminateDrawable;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import com.google.android.material.button.MaterialButton;
import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
    private Button buttonDelete;
    private DatabaseReference itemsRef;

    private EditText editLotNumber;

    private FirebaseDatabase db;

    private Button btnSelect;
    private ImageView imageView;
    private Uri video;
    private StorageReference storageReference;
    private LinearProgressIndicator progressIndicator;
    private MaterialButton selectVideo;


    //DELETE FOLLOWING LINES BELOW WHEN INTEGRATING DELETE TO MAIN
    Dialog dialog;
    Button buttonCancel, buttonDel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);
        View del_view = inflater.inflate(R.layout.fragment_delete_item, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextLot = view.findViewById(R.id.editTextLot);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerPeriod = view.findViewById(R.id.spinnerPeriod);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        imageView = view.findViewById(R.id.imageView);
        progressIndicator = view.findViewById(R.id.process);
        selectVideo = view.findViewById(R.id.selectVideo);
        buttonDelete = view.findViewById(R.id.buttonDelete);




        db = FirebaseDatabase.getInstance("https://taam-management-system-default-rtdb.firebaseio.com/");

        FirebaseApp.initializeApp(requireContext());
        storageReference = FirebaseStorage.getInstance().getReference();

        //DEL BELOW
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.fragment_delete_item);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        editLotNumber = dialog.findViewById(R.id.editLotNumber);

        buttonDel = dialog.findViewById(R.id.buttonDel);
        buttonCancel = dialog.findViewById(R.id.buttonCancel);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItemByTitle();
            }
        });



        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

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

        /*buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new DeleteItemFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });*/

        // Set up the spinner with periods
        ArrayAdapter<CharSequence> adapter_p = ArrayAdapter.createFromResource(getContext(),
                R.array.period_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriod.setAdapter(adapter_p);

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
                        String galleryUrl = video.toString();

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
        private void deleteItemByTitle() {
            String lot = editLotNumber.getText().toString().trim();

            if (lot.isEmpty()) {
                Toast.makeText(getContext(), "Please enter lot number", Toast.LENGTH_SHORT).show();
                return;
            }

            itemsRef = db.getReference("collection_data/");
            itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean itemFound = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Item item = snapshot.getValue(Item.class);
                        if (item != null && item.getLot().equalsIgnoreCase(lot)) {
                            snapshot.getRef().removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
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

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
}