package com.example.nustsocialcircle.CompleteRegistrationFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nustsocialcircle.ModalClasses.SchoolSelecting;
import com.example.nustsocialcircle.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SchoolFragment extends Fragment {

    private RecyclerView SelectSchoolRV;
    private TextView SelectSchoolShowing;

    private List<SchoolSelecting> schoolSelectingList;
    private List<SingleSchoolViewHolder> viewList;

    private String selectedSchool;

    private SchoolSelectionShowing showingAdapter;
    private LinearLayoutManager layoutManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complete_profile_school_asking, null);

        initializeViews(view);
        initializeComponents(view);

        return view;
    }


    private void initializeViews(View view) {

        SelectSchoolRV = view.findViewById(R.id.RVFragmentSchoolAllSchoolsShowing);
        SelectSchoolShowing = view.findViewById(R.id.TVFragmentSchoolSelectShowing);

    }

    private void initializeComponents(View view) {

        schoolSelectingList = new ArrayList<>();
        viewList = new ArrayList<>();
        showingAdapter = new SchoolSelectionShowing();
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        SelectSchoolRV.setLayoutManager(layoutManager);
        SelectSchoolRV.setAdapter(showingAdapter);

        schoolSelectingList.add(new SchoolSelecting("Seecs", "www.image.com"));
        schoolSelectingList.add(new SchoolSelecting("NBS", "www.image.com"));
        schoolSelectingList.add(new SchoolSelecting("NICE", "www.image.com"));
        schoolSelectingList.add(new SchoolSelecting("SMME", "www.image.com"));
        schoolSelectingList.add(new SchoolSelecting("SADA", "www.image.com"));
        schoolSelectingList.add(new SchoolSelecting("S3H", "www.image.com"));

        showingAdapter.notifyDataSetChanged();


    }

    private void resetOtherSchoolsSelected() {

        for (int i = 0; i < viewList.size(); i++) {

            viewList.get(i).MainContainer.setBackgroundColor(getResources().getColor(R.color.gradientBlue, Objects.requireNonNull(getActivity()).getTheme()));
            schoolSelectingList.get(i).setmSelected(false);

        }


    }

    public String getSelectedSchool() {
        return selectedSchool;
    }

    public void highlightSelectSchool() {

        SelectSchoolShowing.setTextColor(getResources().getColor(R.color.darkOrange, Objects.requireNonNull(getActivity()).getTheme()));


        Thread td = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    SelectSchoolShowing.setTextColor(getResources().getColor(R.color.white, Objects.requireNonNull(getActivity()).getTheme()));

                }
            }
        };
        td.start();
    }


    class SchoolSelectionShowing extends RecyclerView.Adapter<SingleSchoolViewHolder> {

        @NonNull
        @Override
        public SingleSchoolViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_school_view, null);
            SingleSchoolViewHolder holder = new SingleSchoolViewHolder(view);
            viewList.add(holder);
            return holder;

        }

        @Override
        public void onBindViewHolder(@NonNull SingleSchoolViewHolder holder, int position) {

            holder.setData(position);

        }

        @Override
        public int getItemCount() {
            return schoolSelectingList.size();
        }
    }

    class SingleSchoolViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout MainContainer;

        private ImageView SchoolImage;
        private TextView SchoolName;

        private SingleSchoolViewHolder(@NonNull View itemView) {
            super(itemView);

            MainContainer = itemView.findViewById(R.id.RLRecyclerSchoolViewMainContainer);

            SchoolImage = itemView.findViewById(R.id.IVRecyclerSchoolViewSchoolImageShowing);
            SchoolName = itemView.findViewById(R.id.TVRecyclerSchoolViewSchoolNameShowing);

        }

        private void setData(final int position) {

            SchoolName.setText(schoolSelectingList.get(position).getmSchoolName());

//            Picasso.get().load(schoolSelectingList.get(position).getmImageUri())
//                    .resize(300, 300)
//                    .into(SchoolImage);

            MainContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    resetOtherSchoolsSelected();
                    selectedSchool = schoolSelectingList.get(position).getmSchoolName();
                    MainContainer.setBackgroundColor(getResources().getColor(R.color.golden, getActivity().getTheme()));
                    schoolSelectingList.get(position).setmSelected(true);

                }
            });


        }
    }


}
