package android.nik.virtualgeocaching.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nik.virtualgeocaching.R;
import android.nik.virtualgeocaching.adapters.FolderAdapter;
import android.nik.virtualgeocaching.model.Chest;
import android.nik.virtualgeocaching.support.RealPathUtil;
import android.nik.virtualgeocaching.support.StringUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private List<String> urlList;
    private ListView chestContentList;
    private FolderAdapter folderAdapter;

    private ProgressDialog mProgressDialog;


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

        //progressdialog
        mProgressDialog = new ProgressDialog(ChestEditActivity.this);
        mProgressDialog.setMessage("Download");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

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
        //Populating listview and fetching folder content
        urlList = new ArrayList<String>();
        getFolderContent();
    }

    private void getFolderContent() {
        //fetching downloadurls in chest from database
        dbRef.child("download").child(chest.getChestID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                urlList.clear();
                //populating urllist
                for(DataSnapshot urlDataSnapshot: dataSnapshot.getChildren()){
                    String downloadURL = urlDataSnapshot.getValue().toString();
                    urlList.add(downloadURL);
                }
                ListView folderContentList = (ListView)findViewById(R.id.chestFilesListView);
                chestContentList = folderContentList;
                chestContentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //fetching downloadURL and initiating download
                        String downloadLink = parent.getItemAtPosition(position).toString();
                        downloadFromStorageURL(downloadLink);
                    }
                });
                folderAdapter = new FolderAdapter(urlList);
                chestContentList.setAdapter(folderAdapter);
                reloadURLList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void reloadURLList() {
        folderAdapter.setList(urlList);
        folderAdapter.notifyDataSetChanged();
    }

    private void downloadFromStorageURL(String downloadURL) {
        final DownloadTask downloadTask = new DownloadTask(ChestEditActivity.this);
        downloadTask.execute(downloadURL);


        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
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

    class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(new File(context.getFilesDir(),StringUtils.getFileNameFromDownloadURL(url.toString())));

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
        }
    }
}


