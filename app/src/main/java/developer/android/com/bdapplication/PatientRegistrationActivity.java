package developer.android.com.bdapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientRegistrationActivity extends AppCompatActivity {

    //Earlier
    private TextView backButton;

    private CircleImageView profile_image;
    private TextInputEditText registerFullname, registerIdNumber, registerPhoneNumber,registerEmail, registerPassword;
    private Spinner specialty;
    private Button registerButton;

    private Uri resultUri;
    private ProgressDialog loader;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_registration);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientRegistrationActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        profile_image = findViewById(R.id.profile_image);
        registerFullname = findViewById(R.id.registerFullname);
        registerIdNumber = findViewById(R.id.registerIdNumber);
        registerPhoneNumber = findViewById(R.id.registerPhoneNumber);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        specialty = findViewById(R.id.specialty);
        registerButton = findViewById(R.id.registerButton);

        loader  = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Directs to gallery
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = registerEmail.getText().toString().trim();
                final String password = registerPassword.getText().toString().trim();
                final String fullname = registerFullname.getText().toString().trim();
                final String idnumber = registerIdNumber.getText().toString().trim();
                final String phoneNumber = registerPhoneNumber.getText().toString().trim();
                final String speciality = specialty.getSelectedItem().toString();

                if(TextUtils.isEmpty((email))){
                    registerEmail.setError("Email is Empty");
                    return;
                }

                if(TextUtils.isEmpty((password))){
                    registerPassword.setError("Password is Empty");
                    return;
                }

                if(TextUtils.isEmpty((fullname))){
                    registerFullname.setError("Fullname is Empty");
                    return;
                }

                if(TextUtils.isEmpty(idnumber)){
                    registerIdNumber.setError("ID is Empty");
                    return;
                }

                if(TextUtils.isEmpty((phoneNumber))){
                    registerPhoneNumber.setError("Phoene Number is Empty");
                    return;
                }

                if(speciality.equals("Select your specialization")){
                    Toast.makeText(PatientRegistrationActivity.this, "Select blood Group", Toast.LENGTH_SHORT).show();
                    return;
                }

                loader.setMessage("Registering ..... ");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            String error = task.getException().toString();
                            Toast.makeText(PatientRegistrationActivity.this, "Error" +error , Toast.LENGTH_SHORT).show();
                        }else{
                            String currentUserId = mAuth.getCurrentUser().getUid();
                            userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(currentUserId);

                            HashMap userInfo = new HashMap();
                            userInfo.put("id",currentUserId);
                            userInfo.put("name",fullname);
                            userInfo.put("email",email);
                            userInfo.put("phoneNumber",phoneNumber);
//                            userInfo.put("password", password);
                            userInfo.put("idnumber",idnumber);
                            userInfo.put("speciality",speciality);
                            userInfo.put("type","patient");
                            userInfo.put("search","patient"+speciality); //concat + filter

                            userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(PatientRegistrationActivity.this, "Data passed successful", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(PatientRegistrationActivity.this, "Error" +task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }

                                    finish();
                                    //loader.dismiss();
                                }
                            });

                            if(resultUri != null){
                                final StorageReference filePath = FirebaseStorage.getInstance().getReference()
                                        .child("Profile Images").child(currentUserId);
                                Bitmap bitmap = null;

                                try{
                                    bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);

                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
                                byte[] data = byteArrayOutputStream.toByteArray();
                                UploadTask uploadTask = filePath.putBytes(data);

                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PatientRegistrationActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        if(taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null){
                                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String imageUrl = uri.toString();
                                                    Map newImageMap = new HashMap();
                                                    newImageMap.put("profilepictureurl", imageUrl);

                                                    userDatabaseRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(PatientRegistrationActivity.this, "Image URL ADDED TO Database successfully", Toast.LENGTH_SHORT).show();
                                                            }else{
                                                                Toast.makeText(PatientRegistrationActivity.this, "Image URL ADDING FAILED", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                    finish();
                                                }
                                            });

                                        }
                                    }
                                });

                                Intent intent = new Intent(PatientRegistrationActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                loader.dismiss();



                            }
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1 && resultCode == RESULT_OK && data != null){
            resultUri = data.getData();
            profile_image.setImageURI(resultUri);
        }
    }

}