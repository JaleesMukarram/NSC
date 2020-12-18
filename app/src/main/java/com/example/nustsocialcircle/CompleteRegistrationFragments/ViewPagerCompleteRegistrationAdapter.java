package com.example.nustsocialcircle.CompleteRegistrationFragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.nustsocialcircle.GeneralUserCompleteRegistration;

public class ViewPagerCompleteRegistrationAdapter extends FragmentStatePagerAdapter {

    public static final int NAME_FRAGMENT = 0;
    public static final int PROFILE_FRAGMENT = 2;
    public static final int SCHOOL_FRAGMENT = 1;

    public GeneralUserCompleteRegistration registration;

    public NameFragment nameFragment;
    public ProfileImageFragment profileImageFragment;
    public SchoolFragment schoolFragment;


    public ViewPagerCompleteRegistrationAdapter(@NonNull FragmentManager fm, int behavior, GeneralUserCompleteRegistration registration) {
        super(fm, behavior);

        this.registration = registration;
        nameFragment = new NameFragment();
        profileImageFragment = new ProfileImageFragment(registration);
        schoolFragment = new SchoolFragment();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        switch (position) {

            case NAME_FRAGMENT:
                fragment = nameFragment;
                break;

            case SCHOOL_FRAGMENT:
                fragment = schoolFragment;
                break;

            case PROFILE_FRAGMENT:
                fragment = profileImageFragment;
                break;


            default:
                fragment = nameFragment;

        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
