package com.example.bpmbooheartbeat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.example.bpmbooheartbeat.R;
import com.example.bpmbooheartbeat.data.api.RetrofitClient;

import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ProfileImageUtils {

    private static final String PREF_NAME = "bpmboo_profile";
    private static final String KEY_AVATAR_URL = "avatar_url";

    public static String convertImageToBase64(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            byte[] imageBytes = byteBuffer.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveAvatarUrl(Context context, String avatarUrl) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_AVATAR_URL, avatarUrl).apply();
    }

    public static String getAvatarUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_AVATAR_URL, null);
    }

    public static void loadAvatar(Context context, ImageView imageView, String avatarBase64) {
        if (avatarBase64 != null && !avatarBase64.isEmpty()) {
            try {
                byte[] imageBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
                Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                imageView.setImageResource(R.drawable.ic_avatar);
            }
        } else {
            imageView.setImageResource(R.drawable.ic_avatar);
        }
    }

    public static void loadAvatar(Context context, ImageView imageView) {
        String avatarUrl = getAvatarUrl(context);
        loadAvatar(context, imageView, avatarUrl);
    }
}