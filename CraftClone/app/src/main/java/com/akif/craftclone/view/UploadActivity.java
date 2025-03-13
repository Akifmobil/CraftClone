package com.akif.craftclone.view;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.akif.craftclone.Manifest;
import com.akif.craftclone.R;
import com.akif.craftclone.databinding.ActivityUploadBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

import MediaStore.Images;

public class UploadActivity extends AppCompatActivity {

    private FirebaseStorage firebaseStorage ;
    private FirebaseAuth auth;

    private FirebaseFirestore firebaseFirestore;

    private StorageReference storageReference;

    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<Intent> permissionLauncher;
    private ActivityUploadBinding binding;




    private Object MediaStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }




    public void uploadButtonClicked(View view){

        UUID uuid = UUID.randomUUID();
        String imageName = "images /" + uuid +".jpg";






        if(imageData != null){

            storageReference.child("/images").putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    StorageReference newReferance = firebaseStorage.getReference();
                    newReferance.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();

                            String comment = binding.commentText.getText().toString();

                            FirebaseUser user = auth.getCurrentUser();
                            String email = user.getEmail();

                            HashMap<String, Object> postData = new HashMap<>();
                            postData.put("useremail ", email);
                            postData.put("downloadurl", downloadUrl);
                            postData.put("comment", comment);
                            postData.put("date", FieldValue.serverTimestamp());

                            FirebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    return;

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });


        }

    }
    public void selectImage(View view, Object result){

        if (ContextCompat.checkSelfPermission(this, UploadActivity.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)){
                Snackbar.make(view, "permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("give permission", new View.OnClickListener() {
                    class READ_EXTERNAL_STORAGE {
                    }

                    @Override
                    public void onClick(View view) {

                        permissionLauncher.launch(READ_EXTERNAL_STORAGE);



                    }
                }).show();
            }
            else{
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);

                activityResultLauncher.launch(intentToGallery );
            }
            else {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI);

            }

        }

        private void registerLauncher()
    {

            activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {


                    if (result.hashCode() == RESULT_OK) {
                        Intent intentFromResult = result.getClass();
                        if (intentFromResult != null) {

                            imageData = intentFromResult.getData();
                            binding.imageView.setImageURI(imageData);


                        }

                    }
                });

                permissionLauncher = new SpinnerAdapter
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<ActivityResult>()

                {
                    public void onActivityResult((Boolean) result) {
                    if (result) {

                        Object MediaStorage;
                        Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStorage.Images.Media.EXTERNAL_CONTENT_URI);
                        activityResultLauncher.launch(intentToGallery);

                    } else {
                        Toast.makeText(UploadActivity.this, "permission needed!", Toast.LENGTH_LONG).show();
                    }


                }
                }
                });

            }
        }

    private class Media {
        public interface EXTERNAL_CONTENT_URI {
        }
    }

    private class READ_EXTERNAL_STORAGE {
    }
}