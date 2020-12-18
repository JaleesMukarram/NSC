package com.example.nustsocialcircle.CompleteRegistrationFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nustsocialcircle.ModalClasses.Badge;
import com.example.nustsocialcircle.ModalClasses.BadgeAdapter;
import com.example.nustsocialcircle.R;

import java.util.ArrayList;

public class NameFragment extends Fragment {

    private EditText NameAskingET, SectionAskingET;
    private Spinner BadgeAskingSPR;
    private ArrayList<Badge> badgeArrayList;

    private String selectedBadge;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complete_profile_name_asking, null);

        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {

        NameAskingET = view.findViewById(R.id.ETFragmentNameNameAsking);
        SectionAskingET = view.findViewById(R.id.ETFragmentNameSectionAsking);
        BadgeAskingSPR = view.findViewById(R.id.SPRFragmentNameBadgeAsking);

        badgeArrayList = new ArrayList<>();
        badgeArrayList.add(new Badge("2012"));
        badgeArrayList.add(new Badge("2013"));
        badgeArrayList.add(new Badge("2014"));
        badgeArrayList.add(new Badge("2015"));
        badgeArrayList.add(new Badge("2016"));
        badgeArrayList.add(new Badge("2017"));
        badgeArrayList.add(new Badge("2018"));
        badgeArrayList.add(new Badge("2019"));
        badgeArrayList.add(new Badge("2020"));

        Context context = getContext();

        if (context != null) {

            BadgeAskingSPR.setAdapter(new BadgeAdapter(context, badgeArrayList));
        }

        BadgeAskingSPR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Badge badge = (Badge) parent.getSelectedItem();
                selectedBadge = badge.getmYear();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public String[] getNameFragmentInfo() {

        String name = NameAskingET.getText().toString().trim();
        String section = SectionAskingET.getText().toString().trim();

        return new String[]{name, section, selectedBadge};

    }

    public void highlightName() {

        NameAskingET.setBackground(getResources().getDrawable(R.drawable.btn_round_orange_stroke, getActivity().getTheme()));

        Thread td = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    NameAskingET.setBackground(getResources().getDrawable(R.drawable.btn_round_white_stroke, getActivity().getTheme()));

                }
            }
        };
        td.start();


    }

    public void highlightSection() {

        SectionAskingET.setBackground(getResources().getDrawable(R.drawable.btn_round_orange_stroke, getActivity().getTheme()));


        Thread td = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    SectionAskingET.setBackground(getResources().getDrawable(R.drawable.btn_round_white_stroke, getActivity().getTheme()));

                }
            }
        };
        td.start();


    }

}
