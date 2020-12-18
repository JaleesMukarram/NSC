package com.example.nustsocialcircle.HelpingClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nustsocialcircle.R;

public class CustomToast {

    public static final int MODE_BG_DANGER_TXT_PRIMARY = 1;
    public static final int MODE_BG_DANGER_TXT_WHITE = 2;

    public static final int MODE_BG_OK_TXT_PRIMARY = 3;
    public static final int MODE_BG_OK_TXT_WHITE = 4;

    public static final int MODE_BG_PRIMARY_TXT_WHITE = 5;
    public static final int MODE_BG_WHITE_TXT_PRIMARY = 6;

    public static final int MODE_BG_WHITE_TXT_WHITE = 7;

    public static final int MODE_BG_PRIMARY_DARK = 8;
    public static final int MODE_BG_WHITE = 9;

    public static final int ICON_OK = 100;
    public static final int ICON_WARNING = 200;
    public static final int ICON_DANGER = 300;

    public static void makeToast(Context context, String message, int mode, int icon, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);

        TextView messageView = view.findViewById(R.id.TVCustomToastMessage);
        ImageView messageIcon = view.findViewById(R.id.IVCustomToastIcon);
        LinearLayout layout = view.findViewById(R.id.TTCustomToastContainer);

        if (mode == MODE_BG_DANGER_TXT_PRIMARY) {

            layout.setBackground(context.getResources().getDrawable(R.drawable.bg_danger, context.getTheme()));
            messageView.setTextColor(context.getResources().getColor(R.color.colorPrimary, context.getTheme()));

        } else if (mode == MODE_BG_DANGER_TXT_WHITE) {

            layout.setBackground(context.getResources().getDrawable(R.drawable.bg_danger, context.getTheme()));
            messageView.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));

        } else if (mode == MODE_BG_OK_TXT_PRIMARY) {

            layout.setBackground(context.getResources().getDrawable(R.drawable.bg_ok, context.getTheme()));
            messageView.setTextColor(context.getResources().getColor(R.color.colorPrimary, context.getTheme()));

        } else if (mode == MODE_BG_OK_TXT_WHITE) {

            layout.setBackground(context.getResources().getDrawable(R.drawable.bg_ok, context.getTheme()));
            messageView.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));

        } else if (mode == MODE_BG_PRIMARY_TXT_WHITE) {

            layout.setBackground(context.getResources().getDrawable(R.drawable.bg_primary, context.getTheme()));
            messageView.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));

        } else if (mode == MODE_BG_WHITE_TXT_PRIMARY) {

            layout.setBackground(context.getResources().getDrawable(R.drawable.bg_white, context.getTheme()));
            messageView.setTextColor(context.getResources().getColor(R.color.colorPrimary, context.getTheme()));

        } else if (mode == MODE_BG_WHITE_TXT_WHITE) {

            layout.setBackground(context.getResources().getDrawable(R.drawable.bg_white, context.getTheme()));
            messageView.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));

        } else if (mode == MODE_BG_PRIMARY_DARK) {

            layout.setBackground(context.getResources().getDrawable(R.drawable.bg_primary_dark_stroke, context.getTheme()));
            messageView.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));

        } else if (mode == MODE_BG_WHITE) {

            layout.setBackground(context.getResources().getDrawable(R.drawable.bg_grey_dark_stroke, context.getTheme()));
            messageView.setTextColor(context.getResources().getColor(R.color.colorPrimary, context.getTheme()));

        }


        if (icon == ICON_OK) {

            messageIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mood_good_24, context.getTheme()));

        } else if (icon == ICON_WARNING) {

            messageIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mood_bad_24, context.getTheme()));

        } else if (icon == ICON_DANGER) {

            messageIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_error_primary_24, context.getTheme()));

        }

        messageView.setText(message);
//        messageIcon.setImageDrawable(resourse);
        Toast toast = new Toast(context);
        toast.setGravity(position, 10, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();

    }

    public static void makeToast_BG_WHITE_TXT_WHITE(Context context, String message, int position) {

        makeToast(context, message, MODE_BG_WHITE_TXT_WHITE, CustomToast.ICON_OK, position);

    }

    public static void makeToast_BG_PRIMARY_TXT_WHITE(Context context, String message, int position) {

        makeToast(context, message, MODE_BG_PRIMARY_TXT_WHITE, CustomToast.ICON_OK, position);

    }

    public static void make_toast_DARK(Context context, String message, int position) {

        makeToast(context, message, MODE_BG_PRIMARY_DARK, CustomToast.ICON_OK, position);

    }

    public static void make_toast_LIGHT(Context context, String message, int position) {

        makeToast(context, message, MODE_BG_WHITE, CustomToast.ICON_OK, position);

    }

}
