package com.example.nustsocialcircle.HelpingClasses;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewDecorator extends RecyclerView.ItemDecoration {

    private int topPadding;
    private int sidePadding;

    public RecyclerViewDecorator(int topPadding, int sidePadding) {
        this.topPadding = topPadding;
        this.sidePadding = sidePadding;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = outRect.bottom = topPadding;
        outRect.left = outRect.right = sidePadding;


    }

}
