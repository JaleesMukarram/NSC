package com.example.nustsocialcircle.FragmentsHelping;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nustsocialcircle.R;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ImageEditingFragment extends Fragment {

    private File ImageFile;
    private int FragmentIdentifier;
    private boolean selected;
    private boolean disabled;
    private String typedDescription;

    private ImageView ImageShowingIV;
    private EditText ImageDescriptionET;
    private TextView DisableTV;


    public ImageEditingFragment(File imageFile, int fragmentIdentifier) {
        ImageFile = imageFile;
        FragmentIdentifier = fragmentIdentifier;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.image_editing_view, null);


        initializeViews(view);
        initializeComponents(view);
        initializeListeners();


        return view;
    }

    private void initializeViews(View view) {

        ImageShowingIV = view.findViewById(R.id.IVImageEditingViewShowImage);
        ImageDescriptionET = view.findViewById(R.id.ETImageEditingViewImageDescription);
        DisableTV = view.findViewById(R.id.TVImageEditingViewEnableDisableText);


        Picasso.get().load(ImageFile)
                .into(ImageShowingIV);

    }

    private void initializeComponents(View view) {

    }

    private void initializeListeners() {

        DisableTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnabled();
            }
        });

        ImageDescriptionET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                typedDescription = s.toString();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public File getImageFile() {
        return ImageFile;
    }

    public void setImageFile(File imageFile) {
        ImageFile = imageFile;
    }

    public int getFragmentIdentifier() {
        return FragmentIdentifier;
    }

    public void setFragmentIdentifier(int fragmentIdentifier) {
        FragmentIdentifier = fragmentIdentifier;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled() {
        this.disabled = false;
        DisableTV.setVisibility(View.VISIBLE);
        ImageShowingIV.setVisibility(View.INVISIBLE);
        ImageDescriptionET.setVisibility(View.INVISIBLE);
    }

    public void setEnabled() {

        this.disabled = false;
        DisableTV.setVisibility(View.INVISIBLE);
        ImageShowingIV.setVisibility(View.VISIBLE);
        ImageDescriptionET.setVisibility(View.VISIBLE);

    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected() {

        this.selected = true;
    }

    public void setUnSelected() {

        this.selected = false;


    }

    public String getTypedDescription() {
        return typedDescription;
    }

}
