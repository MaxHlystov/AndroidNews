package ru.fmtk.khlystov.androidnews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import ru.fmtk.khlystov.androidnews.about.AboutActivity;
import ru.fmtk.khlystov.androidnews.databus.AppBusHolder;
import ru.fmtk.khlystov.androidnews.databus.AppMessages;
import ru.fmtk.khlystov.androidnews.databus.IDataBus;
import ru.fmtk.khlystov.androidnews.databus.IMessageReceiver;
import ru.fmtk.khlystov.androidnews.newsdetails.NewsDetailsFragment;
import ru.fmtk.khlystov.androidnews.newslist.NewsListFragment;
import ru.fmtk.khlystov.newsgetter.model.ArticleIdentificator;

import static ru.fmtk.khlystov.utils.ContextUtils.isHorizontalOrientation;

public class MainActivity extends AppCompatActivity
        implements IMessageReceiver<AppMessages, ArticleIdentificator> {

    @NonNull
    private static final String LOG_TAG = "MainActivity";

    @NonNull
    private static final String NEWS_LIST_FRAGMENT_TAG = "MainActivity:NEWS_LIST_FRAGMENT_TAG";

    @NonNull
    private static final String NEWS_DETAILS_FRAGMENT_TAG = "MainActivity:NEWS_DETAILS_FRAGMENT_TAG";

    @NonNull
    private static final String EDIT_NEWS_FRAGMENT_TAG = "MainActivity:EDIT_NEWS_FRAGMENT_TAG";

    @Nullable
    private IDataBus<AppMessages, ArticleIdentificator> dataBus = null;

    private boolean isTwoPanels = false;

    public static void startActivity(@NonNull Context parent) {
        Intent intent = new Intent(parent, MainActivity.class);
        parent.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDataBus();
        initTwoPanelsFlag();
        if (savedInstanceState == null) {
            startNewsListFragment(new NewsListFragment());
        } else {
            resetFragments();
        }
    }

    private void initDataBus() {
        if (dataBus == null) {
            dataBus = AppBusHolder.register(this);
        }
    }

    private void initTwoPanelsFlag() {
        isTwoPanels = (findViewById(R.id.activity_main__details_frame) != null)
                && isHorizontalOrientation(this);
    }

    private void resetFragments() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            moveNewsListFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.main_menu__about_item:
                AboutActivity.startActivity(this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (isTwoPanels && fragmentManager != null) {
            int count = fragmentManager.getBackStackEntryCount();
            if (count <= 1) {
                finish();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public int getReceiverId() {
        return R.layout.activity_main;
    }

    @Override
    public void onMessageSent(int senderId, @NonNull AppMessages message,
                              @Nullable ArticleIdentificator articleId) {
        switch (message) {
            case OPEN:
                if (articleId != null) {
                    startDetailsFragment(NewsDetailsFragment.getViewFragment(articleId));
                }
                break;
            case EDIT:
                if (articleId != null) {
                    startEditNewsFragment(NewsDetailsFragment.getEditFragment(articleId));
                }
                break;
            default:
                break;
        }
    }

    private int getFrameId(boolean isTwoPanels) {
        if (isTwoPanels) {
            return R.id.activity_main__details_frame;
        }
        return R.id.activity_main__frame;
    }

    private void startNewsListFragment(@NonNull NewsListFragment fragment) {
        doReplaceFragmentTransaction(this,
                fragment,
                R.id.activity_main__frame,
                NEWS_LIST_FRAGMENT_TAG,
                false);
    }

    private void moveNewsListFragment() {
        FragmentManager fManager = getSupportFragmentManager();
        if (fManager != null) {
            Fragment editFragment = fManager.findFragmentByTag(EDIT_NEWS_FRAGMENT_TAG);
            Fragment detailsFragment = fManager.findFragmentByTag(NEWS_DETAILS_FRAGMENT_TAG);
            fManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            if (detailsFragment instanceof NewsDetailsFragment) {
                fManager.beginTransaction().remove(detailsFragment).commit();
                fManager.executePendingTransactions();
                startDetailsFragment((NewsDetailsFragment) detailsFragment);
            }
            if (editFragment instanceof NewsDetailsFragment) {
                fManager.beginTransaction().remove(editFragment).commit();
                fManager.executePendingTransactions();
                startEditNewsFragment((NewsDetailsFragment) editFragment);
            }
        }
    }

    private void startDetailsFragment(@NonNull NewsDetailsFragment fragment) {
        doReplaceFragmentTransaction(this,
                fragment,
                getFrameId(isTwoPanels),
                NEWS_DETAILS_FRAGMENT_TAG,
                true);
    }

    private void startEditNewsFragment(@NonNull NewsDetailsFragment fragment) {
        doReplaceFragmentTransaction(this,
                fragment,
                getFrameId(isTwoPanels),
                EDIT_NEWS_FRAGMENT_TAG,
                true);
    }

    private static void doReplaceFragmentTransaction(@NonNull AppCompatActivity activity,
                                                     @NonNull Fragment fragment,
                                                     int frameId,
                                                     @Nullable String fragmentTag,
                                                     boolean addToBackStack) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (addToBackStack) {
                transaction.addToBackStack(fragment.getClass().getName());
            }
            transaction.replace(frameId, fragment, fragmentTag)
                    .commit();
        }
    }
}
