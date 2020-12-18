package com.example.nustsocialcircle;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nustsocialcircle.HelpingClasses.CustomToast;
import com.example.nustsocialcircle.HelpingClasses.GeneralFileHandling;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GeneralUserAddImages extends AppCompatActivity {

    private static final String TAG = "AddImagesTAG";

    private RecyclerView SelectedImagesRecyclerRV;
    private GridView AllImagesShowingGV;
    private ImageView BackButtonIV;
    private ImageView SendNextIV;

    private ImageSelectedAdapter SelectedImageAdapter;

    private List<File> fileArrayList;
    private List<File> selectedFiles;
    private List<Uri> selectedURIs;

    private LinearLayoutManager linearLayoutManager;


    private boolean filesLloaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_user_add_images);

        new BackGroundImagesLoading(this).execute();

        initializeViews();
        initializeComponents();
        initializeListeners();
    }


    private void initializeViews() {

        SelectedImagesRecyclerRV = (RecyclerView) this.findViewById(R.id.RVGeneralUserAddImagesSelectedImagesShowing);
        AllImagesShowingGV = (GridView) this.findViewById(R.id.GVGeneralUserAddImagesAllImagesShowing);
        BackButtonIV = (ImageView) this.findViewById(R.id.IVGeneralUserAddImagesBackButton);
        SendNextIV = (ImageView) this.findViewById(R.id.IVGeneralUserAddImagesNextBuon);

        fileArrayList = new ArrayList<File>();
        selectedFiles = new ArrayList<File>();
        selectedURIs = new ArrayList<Uri>();

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        linearLayoutManager.setStackFromEnd(true);

        SelectedImagesRecyclerRV.setLayoutManager(linearLayoutManager);

        SelectedImageAdapter = new GeneralUserAddImages.ImageSelectedAdapter(this);


    }

    private void initializeComponents() {

        BackButtonIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomToast.makeToast_BG_PRIMARY_TXT_WHITE(GeneralUserAddImages.this, "Clicked", Gravity.BOTTOM);
                onBackPressed();
            }
        });
    }

    private void initializeListeners() {

        SendNextIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedFiles.size() > 0) {

                    Intent intent = new Intent(GeneralUserAddImages.this, GeneralUserImagesEditingForUploading.class);
                    intent.putExtra("ARRAY", (Serializable) selectedFiles);
                    startActivity(intent);

                } else {
                }

            }
        });


    }

    private void initializeAllImageViewsAfterFilesAvailable() {

        AllImagesShowingGV.setAdapter(new GeneralUserAddImages.GridViewAdapter(this));

        AllImagesShowingGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                addThisImageFilePositionToSelectedList(position);

            }
        });

        SelectedImagesRecyclerRV.setAdapter(SelectedImageAdapter);
    }

    private void addThisImageFilePositionToSelectedList(int position) {

        File currentFileFromOrigionalList = fileArrayList.get(position);

        for (File file : selectedFiles) {

            if (file.getName().equals(currentFileFromOrigionalList.getName())) {

                CustomToast.makeToast(GeneralUserAddImages.this, "Image Already selected", CustomToast.MODE_BG_DANGER_TXT_PRIMARY, CustomToast.ICON_WARNING, Gravity.BOTTOM);
                return;
            }
        }

        selectedFiles.add(fileArrayList.get(position));
        SelectedImageAdapter.notifyDataSetChanged();

    }


    //Adapter for recycler of Selected Images
    private class ImageSelectedAdapter extends RecyclerView.Adapter<ImageSelectedAdapter.SingleGrid> {

        private LayoutInflater layoutInflater;
        private Context context;

        private ImageSelectedAdapter(Context context) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public SingleGrid onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = layoutInflater.inflate(R.layout.grid_row_layout, null);
            return new SingleGrid(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SingleGrid holder, int position) {

            holder.setData(position);

        }

        @Override
        public int getItemCount() {
            return selectedFiles.size();

        }

        private class SingleGrid extends RecyclerView.ViewHolder {

            ImageView ImageIcon;
            RelativeLayout ContainerLayout;
            ImageView CancelView;

            private SingleGrid(@NonNull View itemView) {
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

        private LayoutInflater layoutInflater;
        private Context context;

        public GridViewAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

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

            if (convertView == null) {

                convertView = layoutInflater.inflate(R.layout.grid_row_layout, null);

            }
            final ImageView ImageShowingIcon = (ImageView) convertView.findViewById(R.id.IVGridRowSingleImage);
            Picasso.get().load(fileArrayList.get(position))
                    .resize(150, 150)
                    .into(ImageShowingIcon);
            return convertView;
        }
    }


    //Used to load all the image files
    private class BackGroundImagesLoading extends AsyncTask<Void, Void, Void> {

        Context context;

        public BackGroundImagesLoading(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {

            GeneralUserAddImages.this.filesLloaded = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            GeneralFileHandling generalFileHandling = new GeneralFileHandling(context);
            fileArrayList = generalFileHandling.getAllSortedImageFiles();
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {

            GeneralUserAddImages.this.filesLloaded = true;
            initializeAllImageViewsAfterFilesAvailable();

        }
    }


}
