package com.example.nustsocialcircle.ModalClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nustsocialcircle.R;

import java.util.ArrayList;

public class BadgeAdapter extends ArrayAdapter<Badge> {


    public BadgeAdapter(@NonNull Context context, ArrayList<Badge> badges) {
        super(context, 0, badges);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.batch_select_spinner_view, parent, false);

            TextView BatchName = convertView.findViewById(R.id.TVBatchSelectSpinnerItemBatchName);

            Badge badge = getItem(position);

            if (badge != null) {
                BatchName.setText(badge.getmYear());
            }

        }
        return convertView;
    }
}
