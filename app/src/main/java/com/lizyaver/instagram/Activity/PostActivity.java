package com.lizyaver.instagram.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.lizyaver.instagram.R;
import com.lizyaver.instagram.cropper.CropImage;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    Uri imageUri;
    String myUri = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    ImageView close, added_image;
    TextView post;
    EditText description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close = findViewById(R.id.close);
        added_image = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);

        storageReference = FirebaseStorage.getInstance().getReference("posts");

        close.setOnClickListener(View -> {
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        });

        post.setOnClickListener(View -> {
            upLoadImage();
        });


        CropImage.activity()
                .setAspectRatio(1, 1)
                .start(PostActivity.this);


    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void upLoadImage() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting");
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference filerefrence = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = filerefrence.putFile(imageUri);
            uploadTask.continueWithTask(task -> {

                if (!task.isComplete()) {
                    throw task.getException();
                }

                return filerefrence.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {

                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    myUri = downloadUri.toString();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                    String postid = reference.push().getKey();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("postid", postid);
                    hashMap.put("postimage", myUri);
                    hashMap.put("description", description.getText().toString());
                    hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());


                    reference.child(postid).setValue(hashMap);

                    progressDialog.dismiss();

                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                    finish();

                } else {
                    Toast.makeText(this, "Posting Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(e ->
                    Toast.makeText(PostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No image is selected", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            imageUri = result.getUri();

            added_image.setImageURI(imageUri);

        }else {
            Toast.makeText(this, "something gone wrong", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }

    }
}