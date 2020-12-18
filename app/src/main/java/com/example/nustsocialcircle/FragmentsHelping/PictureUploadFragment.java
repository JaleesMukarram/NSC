package com.example.nustsocialcircle.FragmentsHelping;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nustsocialcircle.HelpingClasses.CustomToast;
import com.example.nustsocialcircle.HelpingClasses.GeneralFileHandling;
import com.example.nustsocialcircle.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PictureUploadFragment extends Fragment {

    private static final String TAG = "PicUploadTAG";

    private RecyclerView SelectedImagesRecycler;
    private GridView AllImagesShowing;
    private ImageView BackButton;


    private List<File> fileArrayList;
    private List<File> selectedFiles;
    private ImageSelectedAdapter SelectedImageAdapter;
    private LinearLayoutManager LinearLayoutManager;

    private boolean filesLloaded;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "fragment onAttached called");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "fragment onCreate called");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_iamge, null);
        Log.d(TAG, "fragment onCreateView called");
        new BackGroundImagesLoading().execute();


        initializeViews(view);
        initializeComponents(view);

        return view;
    }

    private void initializeViews(View view) {

        SelectedImagesRecycler = view.findViewById(R.id.RV_SelectMultipleImageSelectedImages);
        AllImagesShowing = view.findViewById(R.id.GVdialogue_select_image_selection);
        BackButton = view.findViewById(R.id.IV_SelectMultipleImageBackButton);

        fileArrayList = new ArrayList<File>();
        selectedFiles = new ArrayList<File>();

        LinearLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        SelectedImagesRecycler.setLayoutManager(LinearLayoutManager);

        SelectedImageAdapter = new ImageSelectedAdapter();


        BackButton = view.findViewById(R.id.IV_SelectMultipleImageBackButton);

    }

    private void initializeComponents(View view) {

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                CustomToast.makeToast_BG_PRIMARY_TXT_WHITE(getContext(), "Clicked", Gravity.BOTTOM);

            }
        });
    }


    private void initializeAllImageViewsAfterFilesAvailable() {

        AllImagesShowing.setAdapter(new GridViewAdapter());

        AllImagesShowing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                addThisImageFilePositionToSelectedList(position);


            }
        });

        SelectedImagesRecycler.setAdapter(SelectedImageAdapter);
    }

    private void addThisImageFilePositionToSelectedList(int position) {

        File currentFileFromOrigionalList = fileArrayList.get(position);

        for (File file : selectedFiles) {

            if (file.getName().equals(currentFileFromOrigionalList.getName())) {

                CustomToast.makeToast(getContext(), "Image Already selected", CustomToast.MODE_BG_DANGER_TXT_PRIMARY, CustomToast.ICON_WARNING, Gravity.BOTTOM);
                return;
            }
        }

        selectedFiles.add(fileArrayList.get(position));
        SelectedImageAdapter.notifyDataSetChanged();

    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "fragment onStart called");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "fragment onResume called");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "fragment onPause called");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "fragment onStop called");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "fragment onDestroyView called");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "fragment onDestroy called");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "fragment onDetach called");

    }


    //Adapter for recycler of Selected Images
    private class ImageSelectedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = getLayoutInflater().inflate(R.layout.grid_row_layout, null);
            return new PictureUploadFragment.ImageSelectedAdapter.SingleGrid(view);

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            PictureUploadFragment.ImageSelectedAdapter.SingleGrid viewHolder = (PictureUploadFragment.ImageSelectedAdapter.SingleGrid) holder;
            viewHolder.setData(position);

        }

        @Override
        public int getItemCount() {
            return selectedFiles.size();

        }

        private class SingleGrid extends RecyclerView.ViewHolder {

            ImageView ImageIcon;
            RelativeLayout ContainerLayout;
            ImageView CancelView;

            public SingleGrid(@NonNull View itemView) {
                super(itemView);

                ImageIcon = (ImageView) itemView.findViewById(R.id.IVGridRowSingleImage);
                ContainerLayout = (RelativeLayout) itemView.findViewById(R.id.RLGridRowSingleImageContainer);
                CancelView = (ImageView) itemView.findViewById(R.id.IVGridRowSingleImageUnSelectImage);

            }

            void setData(int position) {

                Picasso.get().load(selectedFiles.get(position))
                        .resize(150, 150)
                        .into(ImageIcon);

                CancelView.setVisibility(View.VISIBLE);
                setListenerForRemovingFromList(position);
            }

            private void setListenerForRemovingFromList(final int position) {

                CancelView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selectedFiles.remove(position);
                        SelectedImageAdapter.notifyDataSetChanged();

                    }
                });

            }


        }
    }


    //Used for showing all images in the gridView
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
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.grid_row_layout, null);
            final ImageView ImageShowingIcon = (ImageView) view.findViewById(R.id.IVGridRowSingleImage);

            Picasso.get().load(fileArrayList.get(position))
                    .resize(150, 150)
                    .into(ImageShowingIcon);

            return view;
        }
    }


    //Used to load all the image files
    private class BackGroundImagesLoading extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            PictureUploadFragment.this.filesLloaded = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            GeneralFileHandling generalFileHandling = new GeneralFileHandling(getContext());
            fileArrayList = generalFileHandling.getAllSortedImageFiles();
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {

            PictureUploadFragment.this.filesLloaded = true;
            initializeAllImageViewsAfterFilesAvailable();

        }
    }


}
