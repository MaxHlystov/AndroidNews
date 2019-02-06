package ru.fmtk.khlystov.androidnews.intro;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import me.relex.circleindicator.CircleIndicator;
import ru.fmtk.khlystov.AppConfig;
import ru.fmtk.khlystov.androidnews.MainActivity;
import ru.fmtk.khlystov.androidnews.R;

public class IntroActivity extends FragmentActivity {

    private static final int SWITCH_TO_THE_SECOND_ACTIVITY_SECONDS_DELAY = 500;

    private static final int NUMBER_PAGES = 3;

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
        ViewPager viewPager = findViewById(R.id.activity_intro__pager);
        PagerAdapter pagerAdapter = new IntroStatePagerAdapter(getSupportFragmentManager());
        if (viewPager != null) {
            viewPager.setAdapter(pagerAdapter);
        }
        CircleIndicator indicator = findViewById(R.id.activiti_intro__indicator);
        indicator.setViewPager(viewPager);

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
        MainActivity.startActivity(this);
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

    public class IntroStatePagerAdapter extends FragmentStatePagerAdapter {

        public IntroStatePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            int layoutId = getLayoutIdByPosition(position);
            return IntroFragment.newInstance(layoutId);
        }

        @Override
        public int getCount() {
            return NUMBER_PAGES;
        }

        private int getLayoutIdByPosition(int position) {
            switch (position) {
                case 0:
                    return R.layout.fragment_intro__main_screen;
                case 1:
                    return R.layout.fragment_intro__details_screen;
                case 2:
                    return R.layout.fragment_intro__about_screen;
                default:
                    return R.layout.fragment_intro__main_screen;
            }
        }
    }

}
