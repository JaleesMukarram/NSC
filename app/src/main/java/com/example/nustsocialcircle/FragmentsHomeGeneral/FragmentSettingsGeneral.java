package com.example.nustsocialcircle.FragmentsHomeGeneral;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nustsocialcircle.ModalClasses.GeneralSettings;
import com.example.nustsocialcircle.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentSettingsGeneral extends Fragment {

    RecyclerView recyclerView;
    RecyclerSettingAdapter settingAdapter;
    List<GeneralSettings> settingsList;
    LinearLayoutManager linearLayoutManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = getLayoutInflater().inflate(R.layout.fragment_setting_general, null);

        initializeViews(view);
        initializeRecyclerComponenets(view);
        initializeComponents(view);
        initializeListeners(view);
        loadComponents();
        addSettings(view);


        return view;
    }


    private void initializeViews(View view) {

    }

    private void initializeRecyclerComponenets(View view) {

        recyclerView = view.findViewById(R.id.RVFragmentSetingsAllSettings);
        settingsList = new ArrayList<GeneralSettings>();
        settingAdapter = new RecyclerSettingAdapter(settingsList);


        linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        linearLayoutManager.setSmoothScrollbarEnabled(true);
//        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(settingAdapter);
        settingAdapter.notifyDataSetChanged();


    }

    private void addSettings(View view) {
        settingsList.add(new GeneralSettings("Edit Profile", "Edit the profile including profile picture, name etc..", R.drawable.circle_image));
        settingsList.add(new GeneralSettings("Privacy Setings", "Edit the privacy for your account including profile picture, name etc..", R.drawable.circle_image));
        settingsList.add(new GeneralSettings("Add Friends", "Edit the add friend rules for your account including profile picture, name etc..", R.drawable.circle_image));
        settingsList.add(new GeneralSettings("Post Setting", "Edit the post setting of all the future posts including profile picture, name etc..", R.drawable.circle_image));
        settingsList.add(new GeneralSettings("Notification Setting", "Edit the Notification settings for current device including profile picture, name etc..", R.drawable.circle_image));
        settingsList.add(new GeneralSettings("Log OUT", "Edit the logout options including profile picture, name etc..", R.drawable.circle_image));

        settingAdapter.notifyDataSetChanged();
    }

    private void initializeComponents(View view) {

    }

    private void initializeListeners(View view) {

    }

    private void loadComponents() {

    }

    class RecyclerSettingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<GeneralSettings> settings;

        public RecyclerSettingAdapter(List<GeneralSettings> settings) {
            this.settings = settings;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.general_settings_recycler, parent, false);
            return new SettingView(view);

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            SettingView view = (SettingView) holder;

            view.setData(settings.get(position));

        }

        @Override
        public int getItemCount() {
            return settings.size();
        }

        private class SettingView extends RecyclerView.ViewHolder {

            private ImageView SettingIcon;
            private TextView SettingName;
            private TextView SettingDescription;


            public SettingView(@NonNull View itemView) {
                super(itemView);

                SettingIcon = itemView.findViewById(R.id.IVGeneralSettingsIconShowing);
                SettingName = itemView.findViewById(R.id.TVGeneralSettingsNameShowing);
                SettingDescription = itemView.findViewById(R.id.TVGeneralSettingsDescriptionShowing);

            }

            private void setData(GeneralSettings settings) {

//                this.SettingIcon.setImageResource(settings.getmSettingIcon());
                this.SettingName.setText(settings.getmSettingName());
                this.SettingDescription.setText(settings.getmSettingDescription());

            }
        }
    }
}
