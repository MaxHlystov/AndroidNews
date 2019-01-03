package ru.fmtk.khlystov.utils;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadStateControl {

    @NonNull
    private final View mainView;

    @NonNull
    private final ProgressBar progressBar;

    @NonNull
    private final TextView errorTextView;

    public LoadStateControl(@NonNull View mainView,
                            @NonNull ProgressBar progressBar, 
                            @NonNull TextView errorTextView) {
        this.mainView = mainView;
        this.progressBar = progressBar;
        this.errorTextView = errorTextView;
    }

    public void showStartLoading() {
        errorTextView.setVisibility( View.GONE);
        progressBar.setVisibility( View.VISIBLE);
        mainView.setVisibility( View.GONE);
    }

    public void showCompletLoading() {
        errorTextView.setVisibility( View.GONE);
        progressBar.setVisibility( View.GONE);
        mainView.setVisibility( View.VISIBLE);
    }

    public void showErrorLoading(@NonNull String error) {
        errorTextView.setText(error);
        progressBar.setVisibility( View.GONE);
        errorTextView.setVisibility( View.VISIBLE);
        mainView.setVisibility( View.VISIBLE);
    }

    @NonNull
    public View getMainView() {
        return mainView;
    }

    @NonNull
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @NonNull
    public TextView getErrorTextView() {
        return errorTextView;
    }
}

