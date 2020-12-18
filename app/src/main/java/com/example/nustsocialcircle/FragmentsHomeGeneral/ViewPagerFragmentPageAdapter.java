package com.example.nustsocialcircle.FragmentsHomeGeneral;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerFragmentPageAdapter extends FragmentStatePagerAdapter {


    public static final int HOME_VIEW = 0;
    public static final int FAVORITE_VIEW = 1;
    public static final int MESSAGE_VIEW = 2;
    public static final int NOTIFICATION_VIEW = 3;
    public static final int ACCOUNT_VIEW = 4;
    public static final int SETTINGS_VIEW = 5;

    private FragmentHomeGeneral fragmentHomeGeneral;
    private FragmentFavoriteGeneral fragmentFavoriteGeneral;
    private FragmentMessageGeneral fragmentMessageGeneral;
    private FragmentNotificationGeneral fragmentNotificationGeneral;
    private FragmentSettingsGeneral fragmentSettingsGeneral;
    private FragmentAccountGeneral fragmentAccountGeneral;


    public ViewPagerFragmentPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        fragmentHomeGeneral = new FragmentHomeGeneral();
        fragmentFavoriteGeneral = new FragmentFavoriteGeneral();
        fragmentMessageGeneral = new FragmentMessageGeneral();
        fragmentNotificationGeneral = new FragmentNotificationGeneral();
        fragmentSettingsGeneral = new FragmentSettingsGeneral();
        fragmentAccountGeneral = new FragmentAccountGeneral();

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        Fragment fragment;


        switch (position) {
            case HOME_VIEW:
                fragment = fragmentHomeGeneral;
                break;
            case FAVORITE_VIEW:
                fragment = fragmentFavoriteGeneral;
                break;

            case MESSAGE_VIEW:
                fragment = fragmentMessageGeneral;
                break;

            case NOTIFICATION_VIEW:
                fragment = fragmentNotificationGeneral;
                break;

            case SETTINGS_VIEW:
                fragment = fragmentSettingsGeneral;
                break;


            case ACCOUNT_VIEW:

                fragment = fragmentAccountGeneral;
                break;

            default:
                fragment = fragmentHomeGeneral;
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 6;
    }
}
