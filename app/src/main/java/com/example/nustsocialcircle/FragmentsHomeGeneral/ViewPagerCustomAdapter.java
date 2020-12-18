package com.example.nustsocialcircle.FragmentsHomeGeneral;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerCustomAdapter extends FragmentStateAdapter {

    public static final int HOME_VIEW = 0;
    public static final int FAVORITE_VIEW = 1;
    public static final int MESSAGE_VIEW = 2;
    public static final int NOTIFICATION_VIEW = 3;
    public static final int ACCOUNT_VIEW = 4;
//    public static final int SETTINGS_VIEW = 5;

    private FragmentHomeGeneral fragmentHomeGeneral;
    private FragmentFavoriteGeneral fragmentFavoriteGeneral;
    private FragmentMessageGeneral fragmentMessageGeneral;
    private FragmentNotificationGeneral fragmentNotificationGeneral;
    private FragmentMore fragmentMore;

    public ViewPagerCustomAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        fragmentHomeGeneral = new FragmentHomeGeneral();
        fragmentFavoriteGeneral = new FragmentFavoriteGeneral();
        fragmentMessageGeneral = new FragmentMessageGeneral();
        fragmentNotificationGeneral = new FragmentNotificationGeneral();
        fragmentMore = new FragmentMore();

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

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

            case ACCOUNT_VIEW:

                fragment = fragmentMore;
                break;

            default:
                fragment = fragmentHomeGeneral;
                break;
        }

        return fragment;    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
