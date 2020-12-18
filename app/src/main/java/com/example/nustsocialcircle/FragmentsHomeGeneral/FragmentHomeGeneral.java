package com.example.nustsocialcircle.FragmentsHomeGeneral;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nustsocialcircle.FirebaseHelper.FirebaseCompletePostImageForHomeLoader;
import com.example.nustsocialcircle.FirebaseHelper.FirebaseCustomReferences;
import com.example.nustsocialcircle.GeneralUserAddImages;
import com.example.nustsocialcircle.HelpingClasses.RecyclerViewDecorator;
import com.example.nustsocialcircle.HomeShowing.HomeGeneralImagePost;
import com.example.nustsocialcircle.Interfaces.FirebaseDatabaseDownloadListener;
import com.example.nustsocialcircle.PostModalClasses.ImagePost;
import com.example.nustsocialcircle.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentHomeGeneral extends Fragment {

    private static final String TAG = "HomeFragmentTAG";
    private RecyclerView PostsRecyclerView;
    private AllPostsAdapter PostAdater;
    private LinearLayoutManager linearLayoutManager;
    private Button logout;
    private List<Object> postsList;
    private Dialog TextPostDialogue;

    private List<String> imagePostIDsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_general, container, false);


        initializeViews(view);
        initializeComponents(view);

        return view;

    }

    private void initializeViews(View view) {

        PostsRecyclerView = view.findViewById(R.id.RVFragmentHomePostsShowing);

        logout = view.findViewById(R.id.LogoutBTN);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();

            }
        });

        postsList = new ArrayList<Object>();
        imagePostIDsList = new ArrayList<String>();

        PostAdater = new AllPostsAdapter();
        linearLayoutManager = new LinearLayoutManager(getContext());

        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        PostsRecyclerView.setLayoutManager(linearLayoutManager);
        PostsRecyclerView.setAdapter(PostAdater);
        PostsRecyclerView.addItemDecoration(new RecyclerViewDecorator(20, 1));

        postsList.add("ImagePosts");

        getAllPostsToShow();

        PostAdater.notifyDataSetChanged();

    }

    private void getAllPostsToShow() {

        getAllPostsFromImagePosts();
    }

    private void getAllPostsFromImagePosts() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(FirebaseCustomReferences.GeneralUserPOSTs.GENERAL_USER_IMAGE_POSTS_REFERENCE);
                reference.addValueEventListener(new GetAllImagePosts());
                Log.d(TAG, "Started getting all imagePosts");

            }
        });

        thread.start();

    }

    private void onAllIDsLoaded() {

        Log.d(TAG, "AllIDs loaded and now sending command");

        for (String id : imagePostIDsList) {


            FirebaseCompletePostImageForHomeLoader loader = new FirebaseCompletePostImageForHomeLoader(id);
            loader.setListener(new FirebaseDatabaseDownloadListener() {
                @Override
                public void onSuccess(Object object) {

                    HomeGeneralImagePost homeGeneralImagePost = (HomeGeneralImagePost) object;
                    postsList.add(homeGeneralImagePost);
                    PostAdater.notifyDataSetChanged();

                }

                @Override
                public void onFailure() {

                }
            });


        }
    }

    private void initializeComponents(View view) {

    }

    private class GetAllImagePosts implements ValueEventListener {


        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    for (DataSnapshot attributes : child.getChildren()) {

                        if ("postID".equals(attributes.getKey())) {

                            imagePostIDsList.add(attributes.getValue(String.class));
                            Log.d(TAG, "ImagePosts added ID: " + attributes.getValue(String.class));


                        }
                    }
                }

                if (imagePostIDsList.size() > 0) {
                    Log.d(TAG, "List size OK");
                    onAllIDsLoaded();
                }
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }

    private class AllPostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_ADD_OPTIONS = 10;
        private static final int VIEW_IMAGE_SHOW = 20;


        @Override
        public int getItemViewType(int position) {

            if (postsList.get(position) instanceof HomeGeneralImagePost) {

                return VIEW_IMAGE_SHOW;

            } else if (postsList.get(position) instanceof String) {

                return VIEW_ADD_OPTIONS;

            }

            return super.getItemViewType(position);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view;

            if (viewType == VIEW_ADD_OPTIONS) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_post_view, parent, false);
                return new NewPostViewHolder(view);


            } else if (viewType == VIEW_IMAGE_SHOW) {

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_image_post_showing_view, parent, false);
                return new ImagePostViewHolder(view);

            } else {

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_post_view, parent, false);
                return new NewPostViewHolder(view);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof ImagePostViewHolder) {

                ImagePostViewHolder viewHolder = (ImagePostViewHolder) holder;
                ((ImagePostViewHolder) holder).setData((HomeGeneralImagePost) postsList.get(position));
            }

        }

        @Override
        public int getItemCount() {

            return postsList.size();
        }

        private class NewPostViewHolder extends RecyclerView.ViewHolder {

            CardView AddImage;
            CardView AddText;


            private NewPostViewHolder(@NonNull View itemView) {
                super(itemView);
                AddImage = itemView.findViewById(R.id.CVPostViewAddImage);
                AddText = itemView.findViewById(R.id.CVPostViewAddText);

                initializeTheComponenets();

            }

            private void initializeTheComponenets() {

                AddImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), GeneralUserAddImages.class));
                    }
                });

                AddText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }
        }

        private class ImagePostViewHolder extends RecyclerView.ViewHolder {


            private ImageView UserImageIV, ThreeDotsOptionsIV;
            private ImageView MainImageShowingIV;
            private ImageView LikePostIV, DislikePostIV, SharePostIV;

            private TextView UsernameTV, TimeTV;
            private TextView DescriptionTV, SeeMoreTV;

            private TextView LikesCounterTV, DisLikesCounterTV, ShareCounterTV;

            private HomeGeneralImagePost post;


            private ImagePostViewHolder(@NonNull View itemView) {
                super(itemView);

                UserImageIV = itemView.findViewById(R.id.IVHomeImagePostShowingViewUserImageShowing);
                ThreeDotsOptionsIV = itemView.findViewById(R.id.IVHomeImagePostShowingViewUserThreeDotsShowing);

                MainImageShowingIV = itemView.findViewById(R.id.IVHomeImagePostShowingViewUserMainImageShowing);

                LikePostIV = itemView.findViewById(R.id.IVHomeImagePostShowingViewLike);
                DislikePostIV = itemView.findViewById(R.id.IVHomeImagePostShowingViewDisLike);
                SharePostIV = itemView.findViewById(R.id.IVHomeImagePostShowingViewShare);

                UsernameTV = itemView.findViewById(R.id.TVHomeImagePostShowingViewUserNameShowing);
                TimeTV = itemView.findViewById(R.id.TVHomeImagePostShowingViewUserTimeShowing);

                DescriptionTV = itemView.findViewById(R.id.TVHomeImagePostShowingViewDescriptionDescription);
                SeeMoreTV = itemView.findViewById(R.id.TVHomeImagePostShowingViewDescriptionSeeMore);


                LikesCounterTV = itemView.findViewById(R.id.TVHomeImagePostShowingViewLikeCounter);
                DisLikesCounterTV = itemView.findViewById(R.id.TVHomeImagePostShowingViewDisLikeCounter);
                ShareCounterTV = itemView.findViewById(R.id.TVHomeImagePostShowingViewShareCounter);

            }

            private void setData(HomeGeneralImagePost post) {

                this.post = post;
                setUserDetails();
                setImageDetails();
                setLikesDetails();


            }

            private void setUserDetails() {

                Picasso.get().load(post.getUser().getmProfileUri())
                        .resize(70, 70)
                        .into(UserImageIV);

                UsernameTV.setText(post.getUser().getmName());
                TimeTV.setText(post.getImagePost().getUploadDate().toString());


            }

            private void setImageDetails() {

                Picasso.get().load(post.getImagePost().getImageURL())
                        .into(MainImageShowingIV);

                if (post.getImagePost().getImageDescription().equals(ImagePost.DEFAULT_DESCRIPTION)) {

                    DescriptionTV.setVisibility(View.GONE);

                } else {

                    final String text = post.getImagePost().getImageDescription();

                    if (text.length() > 200) {

                        String smallText = text.substring(0, 200) + "...";

                        DescriptionTV.setText(smallText);
                        SeeMoreTV.setVisibility(View.VISIBLE);

                        SeeMoreTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                DescriptionTV.setText(text);
                                SeeMoreTV.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        DescriptionTV.setText(post.getImagePost().getImageDescription());
                    }

                }

            }

            private void setLikesDetails() {

                String likes = post.getImagePost().getLikes() + "";
                String disLikes = post.getImagePost().getDislikes() + "";


                LikesCounterTV.setText(likes);
                DisLikesCounterTV.setText(disLikes);

            }

            private void setBottomListeners() {

                LikePostIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                DislikePostIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }


        }


    }

}
