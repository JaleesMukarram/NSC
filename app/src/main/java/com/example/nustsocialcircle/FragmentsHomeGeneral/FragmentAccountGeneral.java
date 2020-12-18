package com.example.nustsocialcircle.FragmentsHomeGeneral;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.nustsocialcircle.GeneralUserCompleteRegistration;
import com.example.nustsocialcircle.FirebaseHelper.FirebaseCustomReferences;
import com.example.nustsocialcircle.HelpingClasses.CustomSharedPreferencesPaths;
import com.example.nustsocialcircle.HomeScreenGeneral;
import com.example.nustsocialcircle.ModalClasses.GeneralUser;
import com.example.nustsocialcircle.R;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class FragmentAccountGeneral extends Fragment {

    private static final String TAG = "FragmentAccTAG";

    private final String[] schools = {"SEECS", "SMME", "NICE", "NBS", "SADA", "S3H", "SCME", "IEAC", "OTHER"};


    private EditText NameET, SectionET;
    private Spinner SchoolsSPR;
    private TextView NameShowingTV, SchoolShowingTV, SectionShowingTV;
    private ImageView ProfilePicIV, SelectImageIV, EditNameIV, EditSchoolIV, EditSectionIV;
    private ProgressBar ProfileUpdateProgress;
    private Button LogOutBTN;


    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String childReference;
    private FirebaseUser mUser;

    private SharedPreferences preferences;
    private SharedPreferences.Editor preferenceEditor;

    private GeneralUser user;


    private AlertDialog ImageSelectionDialogue;

    private ArrayList<File> fileArrayList;

    private File selectedProfileFile;

    private boolean filesLloaded;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account_general, null);

        initializeViews(view);
        initializeComponents(view);
        return view;

    }

    private void initializeViews(View view) {

        NameShowingTV = (TextView) view.findViewById(R.id.TVFragmentAccountGeneralNameShowing);
        SchoolShowingTV = (TextView) view.findViewById(R.id.TVFragmentAccountGeneralSchoolShowing);
        SectionShowingTV = (TextView) view.findViewById(R.id.TVFragmentAccountGeneralSectionShowing);

        NameET = (EditText) view.findViewById(R.id.ETFragmentAccountGeneralNameAsking);
        SectionET = (EditText) view.findViewById(R.id.ETFragmentAccountGeneralSectionAsking);
        SchoolsSPR = (Spinner) view.findViewById(R.id.SPRFragmentAccountGeneralSectionsList);

        EditNameIV = (ImageView) view.findViewById(R.id.IVFragmentAccountGeneralEditName);
        EditSchoolIV = (ImageView) view.findViewById(R.id.IVFragmentAccountGeneralEditSchool);
        EditSectionIV = (ImageView) view.findViewById(R.id.IVFragmentAccountGeneralEditSection);

        LogOutBTN = (Button) view.findViewById(R.id.BTNFragmentAccountGeneralLogOut);


        ProfileUpdateProgress = view.findViewById(R.id.IPFragmentAccountGeneralImageProgress);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, schools);
        SchoolsSPR.setAdapter(adapter);


        ProfilePicIV = (ImageView) view.findViewById(R.id.IVFragmentAccountGeneralProfilePic);
        SelectImageIV = (ImageView) view.findViewById(R.id.IVFragmentAccountGeneralSelectImage);


    }

    private void initializeComponents(View view) {

        preferences = getActivity().getSharedPreferences(CustomSharedPreferencesPaths.FragmentAccountGeneralPreferences.DEFAULT_PATH, Context.MODE_PRIVATE);
        preferenceEditor = preferences.edit();

        new BackGroundImagesLoading().execute();

        mUser = HomeScreenGeneral.mAuth.getCurrentUser();

        loadLocalComponents();
        updateAllViewsFromFirebase();


    }

    private void loadLocalComponents() {

        String name = preferences.getString(CustomSharedPreferencesPaths.FragmentAccountGeneralPreferences.NAME, CustomSharedPreferencesPaths.STRING_NOT_FOUND);
        String school = preferences.getString(CustomSharedPreferencesPaths.FragmentAccountGeneralPreferences.SCHOOL, CustomSharedPreferencesPaths.STRING_NOT_FOUND);
        String section = preferences.getString(CustomSharedPreferencesPaths.FragmentAccountGeneralPreferences.SECTION, CustomSharedPreferencesPaths.STRING_NOT_FOUND);

        if (!CustomSharedPreferencesPaths.STRING_NOT_FOUND.equals(name)) {
            NameShowingTV.setText(name);
            Log.d(TAG, "Name exists" + name);
        }
        if (!CustomSharedPreferencesPaths.STRING_NOT_FOUND.equals(school)) {
            SchoolShowingTV.setText(school);
            Log.d(TAG, "School exists" + school);

        }
        if (!CustomSharedPreferencesPaths.STRING_NOT_FOUND.equals(section)) {
            SectionShowingTV.setText(section);
            Log.d(TAG, "Section exists" + section);

        }
    }

    private void initializeListeners(View view) {

        EditNameIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableNameEditing(v);
                EditNameIV.setVisibility(View.INVISIBLE);
            }
        });

        EditSchoolIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableSchoolEditing(v);
                EditSchoolIV.setVisibility(View.INVISIBLE);
            }
        });

        EditSectionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableSectionEditing(v);
                EditSectionIV.setVisibility(View.INVISIBLE);
            }
        });


        LogOutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HomeScreenGeneral.mAuth.signOut();

            }
        });

        SelectImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FragmentAccountGeneral.this.filesLloaded) {

                    showProfileImageSelectorDialogue();

                } else {

                    Toast.makeText(getContext(), "loading images....wait.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        NameET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                updateUserInfoInFirebaseDatabase(FirebaseCustomReferences.UPDATE_GENERAL_USER_NAME, v.getText().toString().trim());
                return false;
            }
        });


        SchoolsSPR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUserInfoInFirebaseDatabase(FirebaseCustomReferences.UPDATE_GENERAL_USER_SCHOOL, schools[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SectionET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                updateUserInfoInFirebaseDatabase(FirebaseCustomReferences.UPDATE_GENERAL_USER_SECTION, v.getText().toString().trim());
                return false;
            }
        });

    }

    private void sortImageFiles() {

        Collections.sort(fileArrayList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified());
            }
        });
    }

    private void updateAllViewsFromFirebase() {


        if (mUser.getPhotoUrl() != null) {

            Picasso.get().load(mUser.getPhotoUrl()).into(ProfilePicIV);

        }

        databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GENERAL_USER_ACCOUNT_REFERENCE).child(mUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    initializeListeners(getView());

                    Log.d(TAG, "Snapshot for the user exists for updating views from firebase");

                    user = dataSnapshot.getValue(GeneralUser.class);
                    Log.d(TAG, user.toString());

                    if (user.isUserValid()) {

                        preferenceEditor.putString(CustomSharedPreferencesPaths.FragmentAccountGeneralPreferences.NAME, user.getmName()).apply();
                        preferenceEditor.putString(CustomSharedPreferencesPaths.FragmentAccountGeneralPreferences.SCHOOL, user.getmSchool()).apply();
                        preferenceEditor.putString(CustomSharedPreferencesPaths.FragmentAccountGeneralPreferences.SECTION, user.getmSection()).apply();

                        NameShowingTV.setText(user.getmName());
                        SchoolShowingTV.setText(user.getmSchool());
                        SectionShowingTV.setText(user.getmSection());

                    }

                } else {

                    Log.d(TAG, "Snapshot doesnt exist");
                    startActivity(new Intent(getContext(), GeneralUserCompleteRegistration.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }

    private void updateUserInfoInFirebaseDatabase(int mode, String newData) {

        if (mode == FirebaseCustomReferences.UPDATE_GENERAL_USER_NAME) {

            NameET.setVisibility(View.INVISIBLE);
            NameShowingTV.setVisibility(View.VISIBLE);
            EditNameIV.setVisibility(View.VISIBLE);
            databaseReference.child("mName").setValue(newData)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                        }
                    });


        } else if (mode == FirebaseCustomReferences.UPDATE_GENERAL_USER_SCHOOL) {

            SchoolsSPR.setVisibility(View.INVISIBLE);
            SchoolShowingTV.setVisibility(View.VISIBLE);
            EditSchoolIV.setVisibility(View.VISIBLE);
            databaseReference.child("mSchool").setValue(newData)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, e.toString());
                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                        }
                    });


        } else if (mode == FirebaseCustomReferences.UPDATE_GENERAL_USER_SECTION) {

            SectionET.setVisibility(View.INVISIBLE);
            SectionShowingTV.setVisibility(View.VISIBLE);
            EditSectionIV.setVisibility(View.VISIBLE);
            databaseReference.child("mSection").setValue(newData)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, e.toString());
                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                        }
                    });

        }
    }

    private void enableSectionEditing(View v) {

        SectionShowingTV.setVisibility(View.INVISIBLE);
        SectionET.setVisibility(View.VISIBLE);
        SectionET.setText(SectionShowingTV.getText());

    }

    private void enableSchoolEditing(View v) {

        SchoolShowingTV.setVisibility(View.INVISIBLE);
        SchoolsSPR.setVisibility(View.VISIBLE);

    }

    private void enableNameEditing(View v) {

        NameShowingTV.setVisibility(View.INVISIBLE);
        NameET.setVisibility(View.VISIBLE);
        NameET.setText(NameShowingTV.getText());

    }

    private ArrayList<File> getAllImageFiles(File externalStorageDirectory) {

        ArrayList<File> b = new ArrayList<>();

        File[] files = externalStorageDirectory.listFiles();

        if (files != null) {

            for (int i = 0; i < files.length; i++) {

                if (files[i].isDirectory()) {

                    b.addAll(getAllImageFiles(files[i]));

                } else {

                    if (files[i].getName().endsWith(".jpg")) {

                        b.add(files[i]);
                    }
                }
            }

        } else {

            Toast.makeText(getContext(), "No image in phone:)", Toast.LENGTH_SHORT).show();
        }


        return b;
    }

    private void showProfileImageSelectorDialogue() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = FragmentAccountGeneral.this.getLayoutInflater().inflate(R.layout.select_image_view, null);

        GridView gridView = view.findViewById(R.id.GVdialogue_select_image_selection);
        gridView.setAdapter(new GridViewAdapter());

        builder.setView(view);

        ImageSelectionDialogue = builder.create();
        ImageSelectionDialogue.show();

    }

    private void updateProfilePicLocal() {

        if (selectedProfileFile != null) {

            childReference = mUser.getUid() + "/" + selectedProfileFile.getName();

            storageReference = FirebaseStorage.getInstance().getReference(FirebaseCustomReferences.GENERAL_USER_PROFILE_STORAGE);
            storageReference.child(childReference).putFile(Uri.fromFile(selectedProfileFile))

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            updateProfileLinkForUser();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (double) taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount() * 100;
                            ProfileUpdateProgress.setProgress((int) progress);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                        }
                    })

            ;
        }

    }

    private void updateProfileLinkForUser() {

        storageReference.child(childReference).getDownloadUrl()

                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        addImageUriToDatabase(uri.toString());

                        ProfileUpdateProgress.setProgress(95);

                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build();

                        mUser.updateProfile(request)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        ProfileUpdateProgress.setProgress(100);
                                        ProfilePicIV.setImageURI(Uri.parse(selectedProfileFile.toString()));
                                        ProfileUpdateProgress.setVisibility(View.INVISIBLE);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                })
                                .addOnCanceledListener(new OnCanceledListener() {
                                    @Override
                                    public void onCanceled() {

                                    }
                                });


                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                })

                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {

                    }
                });


    }

    private void addImageUriToDatabase(String uri) {

        databaseReference.child("mProfileUri").setValue(uri)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                })

                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {

                    }
                });
    }

    private class GridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return fileArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return fileArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.grid_row_layout, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.IVGridRowSingleImage);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectedProfileFile = fileArrayList.get(position);
                    ImageSelectionDialogue.cancel();
                    ProfileUpdateProgress.setVisibility(View.VISIBLE);
                    updateProfilePicLocal();

                }
            });

            Picasso.get().load(fileArrayList.get(position))
                    .resize(100, 100)
                    .into(imageView);

            return view;
        }
    }

    private class BackGroundImagesLoading extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            FragmentAccountGeneral.this.filesLloaded = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            fileArrayList = getAllImageFiles(Environment.getExternalStorageDirectory());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            FragmentAccountGeneral.this.filesLloaded = true;
            FragmentAccountGeneral.this.sortImageFiles();

        }
    }

}
