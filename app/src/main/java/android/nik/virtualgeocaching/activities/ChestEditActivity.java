package android.nik.virtualgeocaching.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nik.virtualgeocaching.R;
import android.nik.virtualgeocaching.model.Chest;
import android.nik.virtualgeocaching.support.RealPathUtil;
import android.nik.virtualgeocaching.support.StringUtils;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class ChestEditActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView chestIDTextView;
    private TextView chestRadiusTextView;
    private TextView chestListedTextView;
    private TextView openToEditTextView;
    private Chest chest;
    private static final int FILE_SELECT_CODE = 101;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference dbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest_edit);
        Chest selectedChest = (Chest) getIntent().getParcelableExtra("selectedChest");
        this.chest = selectedChest;

        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference();

        //VIEWS
        chestIDTextView = (TextView) findViewById(R.id.chestIDText);
        chestRadiusTextView = (TextView) findViewById(R.id.radiusText);
        chestListedTextView = (TextView) findViewById(R.id.listedText);
        openToEditTextView = (TextView) findViewById(R.id.opentoEditText);

        chestIDTextView.setText(chest.getChestID());
        chestRadiusTextView.setText("Radius: "+Float.toString(chest.getRadius()));
        chestListedTextView.setText("Listed: "+String.valueOf(chest.isHidden()));
        openToEditTextView.setText("Open to edit:  "+String.valueOf(chest.isOpentoEdit()));
        //BUTTONS
        if(!chest.isOpentoEdit() && !chest.getAdventurerID().equalsIgnoreCase(firebaseUser.getDisplayName()))
            findViewById(R.id.fileUploadButton).setVisibility(View.GONE);
        else
            findViewById(R.id.fileUploadButton).setOnClickListener(this);
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String realPath;
                    if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (shouldShowRequestPermissionRationale(
                                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            // Explain to the user why we need to read the contacts
                        }
                        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 201);
                        return;
                    }
                        // SDK < API11
                    if (Build.VERSION.SDK_INT < 11)
                        realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, uri);
                        // SDK >= 11 && SDK < 19
                    else if (Build.VERSION.SDK_INT < 19)
                        realPath = RealPathUtil.getRealPathFromURI_API11to18(this, uri);
                        // SDK > 19 (Android 4.4)
                    else
                        realPath = RealPathUtil.getRealPathFromURI_API19(this, uri);

                    //this.chestIDTextView.setText(StringUtils.getFileName(realPath));
                    UploadFromStream(realPath);
                }
                break;
        }
    }

    private void UploadFromStream(final String realPath) {
        String fileName = StringUtils.getFileName(realPath);
        try {
            InputStream inputStream = new FileInputStream(new File(realPath));
            //chestID folder
            StorageReference folderRef = storage.getReference()
                    .child(chest.getChestID());
            final StorageReference fileRef = folderRef.child(fileName);

            UploadTask uploadTask = fileRef.putStream(inputStream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChestEditActivity.this, "Could not upload file.",Toast.LENGTH_SHORT).show();
                }
                //adding downloadURL request and waiting for callback with URL
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //downloadurl is saved in database
                            String fileName = StringUtils.getFileName(realPath);
                            String fileNameNoExtension = StringUtils.cutExtension(fileName);
                            DatabaseReference downloadRef = dbRef.child("download").child(chest.getChestID()).child(fileNameNoExtension);
                            downloadRef.setValue(uri.toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            });
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Could not find file.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 201: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                } else {
                    Toast.makeText(this, "You can't upload files without granting read external storage permission.",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.fileUploadButton){
            String displayName = firebaseUser.getDisplayName();
            //chest is not open to edit and user is not the creator of the chest
            if(!chest.isOpentoEdit()&& !chest.getAdventurerID().equalsIgnoreCase(displayName))
                return;
            else
                showFileChooser();
        }
    }
}
