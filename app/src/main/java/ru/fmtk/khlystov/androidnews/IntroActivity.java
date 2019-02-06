package ru.fmtk.khlystov.androidnews;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ru.fmtk.khlystov.AppConfig;
import ru.fmtk.khlystov.androidnews.newslist.NewsListActivity;

public class IntroActivity extends AppCompatActivity {

    private static final int SWITCH_TO_THE_SECOND_ACTIVITY_SECONDS_DELAY = 3;

    @Nullable
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (needToShowIntro()) {
            showThisActivity();
        } else {
            switchToTheSecondActivity();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
    }

    private boolean needToShowIntro() {
        AppConfig appConfig = AppConfig.getAppConfig(getApplicationContext());
        return appConfig.isNeedToShowIntroActivityFlag();
    }

    private void showThisActivity() {
        setContentView(R.layout.activity_intro);
        Disposable disposable = Completable.complete()
                .delay(SWITCH_TO_THE_SECOND_ACTIVITY_SECONDS_DELAY, TimeUnit.SECONDS)
                .subscribe(this::switchToTheSecondActivity);
        if (compositeDisposable != null) {
            compositeDisposable.add(disposable);
        }

        View view = findViewById(R.id.activity_intro__main_view);
        view.setOnClickListener((View v) -> switchToTheSecondActivity());
    }

    private void switchToTheSecondActivity() {
        toggleFlagShowIntroActivity();
        NewsListActivity.startActivity(this);
        finish();
    }

    private void toggleFlagShowIntroActivity() {
        AppConfig appConfig = AppConfig.getAppConfig(getApplicationContext());
        synchronized (AppConfig.class) {
            appConfig.setNeedToShowIntroActivityFlag(
                    !appConfig.isNeedToShowIntroActivityFlag());
            appConfig.save();
        }
    }
}
