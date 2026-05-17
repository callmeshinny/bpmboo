package com.example.bpmbooheartbeat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.ImageView;

import com.example.bpmbooheartbeat.R;

public class ProfileImageUtils {

    private static final String PREF_NAME = "bpmboo_profile";
    private static final String KEY_AVATAR_URI = "avatar_uri";

    public static void saveAvatarUri(Context context, Uri uri) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_AVATAR_URI, uri.toString()).apply();
    }

    public static String getAvatarUri(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_AVATAR_URI, null);
    }

    public static void loadAvatar(Context context, ImageView imageView) {
        String uriString = getAvatarUri(context);

        if (uriString != null) {
            imageView.setImageURI(Uri.parse(uriString));
        } else {
            imageView.setImageResource(R.drawable.ic_avatar);
        }
    }
}