package com.example.nustsocialcircle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.nustsocialcircle.FirebaseHelper.PostImageUploadTask;
import com.example.nustsocialcircle.FragmentsHelping.ImageEditingFragment;
import com.example.nustsocialcircle.HelpingClasses.BackgroundUploadService;
import com.example.nustsocialcircle.HelpingClasses.CustomToast;
import com.example.nustsocialcircle.Interfaces.AuthStateUpdatingEnabled;
import com.example.nustsocialcircle.PostModalClasses.ImagePost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.nustsocialcircle.HelpingClasses.CustomIntentNamesClass.AUTHENTICATION_STATE_CHANGE_INTENT;

public class GeneralUserImagesEditingForUploading extends AppCompatActivity implements AuthStateUpdatingEnabled {

    private static final String TAG = "ImageEdit4UpTAG";
    private ImageView DeleteIV;
    private ImageView UploadIV;

    private ViewPager2 viewPager;
    private ViewPager2Adapter AdapterViewPager;

    private RecyclerView BottomImageRecycler;
    private ImagesBottomAdapter AdapterImagesBottom;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private BroadcastReceiver AuthStateReceiver;

    private List<ImageEditingFragment> ImageFragments;
    private ArrayList<File> selectedImageFiles;

    private boolean viewPagerCreated;
    private int previousPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_editing_for_uploading);

        initializeViews();
        initializeComponents();
        initializeListeners();

    }

    private void initializeViews() {


        viewPager = this.findViewById(R.id.VPImagesEditingForUploadingImagesShowing);
        DeleteIV = this.findViewById(R.id.IVGeneralUserAddImagesDeleteButton);
        UploadIV = this.findViewById(R.id.IVGeneralUserAddImagesUploadButton);
        BottomImageRecycler = this.findViewById(R.id.RVGeneralUserAddImagesUploadImagesDownPreview);


        Intent ReceivingIntent = getIntent();

        ImageFragments = new ArrayList<ImageEditingFragment>();

        FragmentManager fragmentManager = getSupportFragmentManager();

        previousPosition = -1;

        try {

            selectedImageFiles = (ArrayList<File>) ReceivingIntent.getSerializableExtra("ARRAY");

        } catch (Exception ex) {

            ex.printStackTrace();

        }

        if (selectedImageFiles.size() > 0) {

            createViewPager();
        }
    }

    private void initializeComponents() {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

    }

    private void initializeListeners() {

        DeleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewPagerCreated) {

                    disableSelectedFragment(viewPager.getCurrentItem());
                }
            }
        });

        UploadIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadTheseImages();

            }
        });


    }

    private void createViewPager() {

        for (File file : selectedImageFiles) {

            ImageFragments.add(new ImageEditingFragment(file, ImageFragments.size() + 1));

        }


        AdapterViewPager = new ViewPager2Adapter(this);
        viewPager.setAdapter(AdapterViewPager);


        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                simulatePageChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        viewPager.setOffscreenPageLimit(ImageFragments.size());

        viewPager.setSaveFromParentEnabled(false);
        viewPagerCreated = true;

        createRecyclerView();

    }

    private void createRecyclerView() {

        AdapterImagesBottom = new ImagesBottomAdapter();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        BottomImageRecycler.setLayoutManager(manager);

        BottomImageRecycler.setAdapter(AdapterImagesBottom);


    }


    private void disableSelectedFragment(int position) {

        viewPager.setAdapter(null);
        AdapterViewPager = null;

        ImageFragments.get(0).setSelected();
        ImageFragments.remove(position);

        AdapterImagesBottom.notifyDataSetChanged();

        AdapterViewPager = new ViewPager2Adapter(this);
        viewPager.setAdapter(AdapterViewPager);


        if (ImageFragments.size() == 0) {

            finish();

        } else {

            // Moved to previous position
            if (position < ImageFragments.size()) {

                viewPager.setCurrentItem(position);
                BottomImageRecycler.scrollToPosition(position);
                previousPosition = position;
                ImageFragments.get(position).setSelected();
                Toast.makeText(this, "Replaced to position " + position, Toast.LENGTH_SHORT).show();

            } else {

                viewPager.setCurrentItem(position - 1);
                BottomImageRecycler.scrollToPosition(position - 1);
                ImageFragments.get(position - 1).setSelected();
                previousPosition = position - 1;
                Toast.makeText(this, "Moved back to position " + (position - 1), Toast.LENGTH_SHORT).show();

            }

        }

        AdapterImagesBottom.notifyDataSetChanged();

    }

    private void simulatePageChange(int position) {

        try {

            ImageFragments.get(position).setSelected();

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        unSelectThisFragment(position + 1);
        unSelectThisFragment(position - 1);
        unSelectThisFragment(previousPosition);
        BottomImageRecycler.scrollToPosition(position);
        AdapterImagesBottom.notifyDataSetChanged();
        previousPosition = position;
    }

    private void unSelectThisFragment(int position) {

        try {

            ImageFragments.get(position).setUnSelected();

        } catch (Exception ex) {

            ex.printStackTrace();

        }

    }

    private void uploadTheseImages() {

        UploadIV.setEnabled(false);

        List<PostImageUploadTask> list = new ArrayList<PostImageUploadTask>();

        for (ImageEditingFragment current : ImageFragments) {

            if (!current.isDisabled()) {

                ImagePost imagePost = new ImagePost(mUser.getUid(), current.getTypedDescription());

                list.add(new PostImageUploadTask(imagePost, current.getImageFile()));

            }
        }

        if (list.size() > 0) {

            Intent intent = new Intent(GeneralUserImagesEditingForUploading.this, BackgroundUploadService.class);

            intent.putExtra("ARRAY", (Serializable) list);

            startService(intent);

        }


        Intent intent = new Intent(GeneralUserImagesEditingForUploading.this, HomeScreenGeneral.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(intent);
        CustomToast.make_toast_LIGHT(this, "Uploading...", Gravity.BOTTOM);
        finish();

    }


    @Override
    protected void onStart() {
        super.onStart();
        startListeningToInternetState();
        startListeningToUserStateChange();
    }

    @Override
    protected void onStop() {
        super.onStop();
        startListeningToInternetState();
        startListeningToUserStateChange();
    }

    //Auth state listeners
    @Override
    public void respondToUserStateChange(Intent intent) {

        mUser = mAuth.getCurrentUser();

        if (mUser == null) {

            Log.d(TAG, "In response to state changed, the user is null");

            startActivity(new Intent(GeneralUserImagesEditingForUploading.this, SignUp.class));

        } else {

            Log.d(TAG, "In response to state changed, the user is available");
        }
    }

    @Override
    public void startListeningToUserStateChange() {

        IntentFilter intentFilter = new IntentFilter(AUTHENTICATION_STATE_CHANGE_INTENT);
        AuthStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d(TAG, "Broadcast related to user state received");

                if (AUTHENTICATION_STATE_CHANGE_INTENT.equals(intent.getAction())) {
                    respondToUserStateChange(intent);
                }
            }
        };

        registerReceiver(AuthStateReceiver, intentFilter);
        Log.d(TAG, "Auth Broadcast registered");

    }

    @Override
    public void stopListeningUserToStateChange() {

        if (AuthStateReceiver != null) {
            unregisterReceiver(AuthStateReceiver);

        }
        Log.d(TAG, "Auth Broadcast unRegistered");


    }


    //Internet state listeners
    @Override
    public void onInternetConnectionLost() {

    }

    @Override
    public void onInternetConnectionResume() {

    }

    @Override
    public void startListeningToInternetState() {

    }

    @Override
    public void onInernetConnectionStateChanged(Intent intent) {

    }

    @Override
    public void stopListeningToInternetState() {

    }

    @Override
    public void askInternetConnectionAsync() {

    }


    //Classes for internal use
    private class ViewPager2Adapter extends FragmentStateAdapter {


        private ViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return ImageFragments.get(position);
        }


        @Override
        public int getItemCount() {

            return ImageFragments.size();
        }


    }

    private class ImagesBottomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.small_image_recycler, null);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ImageViewHolder viewHolder = (ImageViewHolder) holder;
            viewHolder.setData(position);

        }

        @Override
        public int getItemCount() {
            return ImageFragments.size();
        }

        private class ImageViewHolder extends RecyclerView.ViewHolder {

            private ImageView ImageShowIV;
            private LinearLayout MainContainer;


            private ImageViewHolder(@NonNull View itemView) {
                super(itemView);

                ImageShowIV = itemView.findViewById(R.id.IVSmallImageImage);
                MainContainer = itemView.findViewById(R.id.RLSmallImageMainContainer);

            }

            private void setData(final int position) {

                Picasso.get().load(ImageFragments.get(position).getImageFile())
                        .resize(50, 70)
                        .into(ImageShowIV);

                if (ImageFragments.get(position).isSelected()) {

                    MainContainer.setBackground(getResources().getDrawable(R.drawable.btn_rect_golden_10, getTheme()));

                } else {

                    MainContainer.setBackground(getResources().getDrawable(R.drawable.btn_rect_primary_dark_10, getTheme()));

                }

                MainContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(position);
                    }
                });
            }
        }

    }


}
