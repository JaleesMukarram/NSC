package com.example.nustsocialcircle.CompleteRegistrationFragments;

import android.app.Dialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.nustsocialcircle.GeneralUserCompleteRegistration;
import com.example.nustsocialcircle.HelpingClasses.CustomToast;
import com.example.nustsocialcircle.HelpingClasses.GeneralFileHandling;
import com.example.nustsocialcircle.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileImageFragment extends Fragment {


    private ImageView ProfileIV;
    private TextView UserNameTV, SchoolTV, SectionTV, BadgeTV;
    private List<File> fileArrayList;
    private File selectedProfileFile;
    private boolean isProfileUploading;

    private boolean imagesLoaded;


    private GeneralUserCompleteRegistration registration;

    private Dialog ImageSelectionDialogue;

    public ProfileImageFragment(GeneralUserCompleteRegistration registration) {
        this.registration = registration;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complete_profile_profile_image_asking, null);

        new BackGroundImageLoading().execute();

        initializeViews(view);
        initializeListeners(view);

        return view;
    }

    private void initializeViews(View view) {


        ProfileIV = (ImageView) view.findViewById(R.id.IVProfileImageFragmentProfilePic);
        UserNameTV = view.findViewById(R.id.TVProfileImageFragmentNameShowing);
        SchoolTV = view.findViewById(R.id.TVProfileImageFragmentSchoolShowing);
        SectionTV = view.findViewById(R.id.TVProfileImageFragmentSectionShowing);
        BadgeTV = view.findViewById(R.id.TVProfileImageFragmentBadgeShowing);


        fileArrayList = new ArrayList<>();


    }

    private void initializeListeners(View view) {

        ProfileIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isProfileUploading) {

                    if (imagesLoaded) {

                        if (fileArrayList.size() == 0) {

                            CustomToast.make_toast_LIGHT(getContext(), "No image found", Gravity.BOTTOM);
                        }

                        showProfileImageSelectorDialogue();
                    } else {
                        CustomToast.make_toast_LIGHT(getContext(), imagesLoaded + "", Gravity.BOTTOM);

                    }
                } else {

                    CustomToast.make_toast_LIGHT(getContext(), "Profile picture already uploading", Gravity.CENTER);
                }

            }
        });


    }

    private void showProfileImageSelectorDialogue() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        View view = Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.select_image_view, null);

        GridView gridView = view.findViewById(R.id.GVdialogue_select_image_selection);
        gridView.setAdapter(new GridViewAdapter());


        builder.setView(view);

        ImageSelectionDialogue = builder.create();
        ImageSelectionDialogue.show();

    }

    private void askToUploadImage() {

        registration.uploadUserProfileImageToStorage(selectedProfileFile);

    }

    public void imageUploaded() {

        ProfileIV.setImageURI(Uri.fromFile(selectedProfileFile));
        isProfileUploading = false;

    }

    public void setData(String[] nameFragmentInfoArray, String selectedSchool) {

        String name = nameFragmentInfoArray[0];
        String section = nameFragmentInfoArray[1];
        String badge = nameFragmentInfoArray[2];

        if (!"".equals(name)) {
            UserNameTV.setText(name);
        }
        if (!"".equals(section)) {

            SectionTV.setText(section);
        }

        if (!"".equals(badge)) {

            BadgeTV.setText(badge);
        }

        if (!"".equals(selectedSchool)) {

            SchoolTV.setText(selectedSchool);
        }
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

            View view = getActivity().getLayoutInflater().inflate(R.layout.grid_row_layout, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.IVGridRowSingleImage);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectedProfileFile = fileArrayList.get(position);
                    ImageSelectionDialogue.cancel();
                    isProfileUploading = true;
                    askToUploadImage();

                }
            });


            Picasso.get().load(fileArrayList.get(position))
                    .resize(100, 100)
                    .into(imageView);

            return view;
        }
    }


    private class BackGroundImageLoading extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            GeneralFileHandling generalFileHandling = new GeneralFileHandling(getContext());

            try {

                fileArrayList = generalFileHandling.getAllSortedImageFiles();
                imagesLoaded = true;
                imagesLoaded = true;

            } catch (Exception ex) {

            }


            return null;
        }


    }


}
