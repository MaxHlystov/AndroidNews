package ru.fmtk.khlystov.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import ru.fmtk.khlystov.androidnews.R;

public class AssetsReader {

    @NonNull
    private static final String LOG_TAG = "NewsAppAssetsReader";

    private AssetsReader() {
        throw new IllegalAccessError("AssetsReader's constructor invocation.");
    }

    @Nullable
    public static String readFromAssetFile(@RawRes int rawId, Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().openRawResource(rawId);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line;
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error reading asset file", e);
            return null;
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
                if (fIn != null) {
                    fIn.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (Exception e2) {
                Log.e(LOG_TAG, "Error closing asset file", e2);
            }
        }
        return returnString.toString();
    }


}
