package ru.fmtk.khlystov.androidnews.intro;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.fmtk.khlystov.androidnews.R;

public class IntroFragment extends Fragment {

    @NonNull
    private static final String LAYOUT_ID = "IntroFragment_LayoutId";

    @NonNull
    public static IntroFragment newInstance(int layout) {
        IntroFragment introFragment = new IntroFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(LAYOUT_ID, layout);
        introFragment.setArguments(bundle);
        return introFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int layoutId = R.layout.fragment_intro__main_screen;
        if (bundle != null) {
            layoutId = bundle.getInt(LAYOUT_ID);
        }
        return inflater.inflate(layoutId, container, false);
    }
}
