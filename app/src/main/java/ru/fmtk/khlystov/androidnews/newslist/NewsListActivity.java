package ru.fmtk.khlystov.androidnews.newslist;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ru.fmtk.khlystov.androidnews.NewsDetailesActivity;
import ru.fmtk.khlystov.androidnews.R;
import ru.fmtk.khlystov.androidnews.about.AboutActivity;
import ru.fmtk.khlystov.newsgetter.NewsSection;
import ru.fmtk.khlystov.newsgetter.NewsStorage;
import ru.fmtk.khlystov.utils.fashionutils.STDDateConverter;
import ru.fmtk.khlystov.AppConfig;
import ru.fmtk.khlystov.newsgetter.Article;

import static ru.fmtk.khlystov.utils.ContextUtils.isHorizontalOrientation;

public class NewsListActivity extends AppCompatActivity {

    @NonNull
    private static final String LOG_TAG = "NewsAppNewsListActivity";

    @Nullable
    private RecyclerView recyclerView = null;

    @Nullable
    private Disposable disposableUpdateNews = null;

    @Nullable
    private Disposable disposableReadNewsChanged = null;

    @Nullable
    private AppConfig configuration;

    @Nullable
    private ProgressBar progressBar;

    @Nullable
    private FloatingActionButton reloadFAB;

    @Nullable
    private TextView errorTextView;

    public static void startActivity(@NonNull Context parent) {
        Intent intent = new Intent(parent, NewsListActivity.class);
        parent.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        initViewsVariables();
        configuration = AppConfig.getAppConfig(this.getApplicationContext());
        if (reloadFAB != null) {
            reloadFAB.setOnClickListener(v -> this.updateNewsFromNYT());
        }
        setNewsSectionsSpinner();
        setRecyclerView();
        readNews();
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
            case R.id.main_menu__about_item:
                onMainMenuAboutItemClicked(item);
                break;
            default:
                Log.e(LOG_TAG, String.format("Unexpected option: %s", item.getTitle()));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        disposeSubscription(disposableUpdateNews);
        disposableUpdateNews = null;
        disposeSubscription(disposableReadNewsChanged);
        disposableReadNewsChanged = null;
        super.onStop();
    }

    private void initViewsVariables() {
        recyclerView = findViewById(R.id.activity_news_list__rec_view);
        progressBar = findViewById(R.id.activity_news_list__progress_bar);
        reloadFAB = findViewById(R.id.activity_news_list__reload_button);
        errorTextView = findViewById(R.id.activity_news_list__error_text);
    }

    private void onMainMenuAboutItemClicked(@NonNull MenuItem item) {
        AboutActivity.startActivity(this);
    }

    private void updateNewsFromNYT() {
        if (configuration != null) {
            showStartLoading();
            disposeSubscription(disposableUpdateNews);
            Completable completable = NewsStorage.updateNewsFromNYT(
                    this,
                    configuration.getNewsSection())
                    .observeOn(AndroidSchedulers.mainThread());
            disposableUpdateNews = completable.subscribe(
                    this::onLoadingComplete,
                    this::showErrorLoading);
        }
    }

    private void onLoadingComplete() {
        showCompletLoading();
        readNews();
    }

    private void readNews() {
        if (configuration != null) {
            disposeSubscription(disposableReadNewsChanged);
            Single<List<Article>> newsObserver = NewsStorage.getArticles(this)
                    .observeOn(AndroidSchedulers.mainThread());
            disposableReadNewsChanged = newsObserver.subscribe(
                    this::showNews,
                    this::showErrorLoading);
        }
    }

    private void showStartLoading() {
        setViewVisibility(errorTextView, View.GONE);
        setViewVisibility(progressBar, View.VISIBLE);
        setViewVisibility(reloadFAB, View.GONE);
    }

    private void showCompletLoading() {
        setViewVisibility(errorTextView, View.GONE);
        setViewVisibility(progressBar, View.GONE);
        setViewVisibility(reloadFAB, View.VISIBLE);
    }

    private void showNews(@Nullable List<Article> articles) {
        setViewVisibility(errorTextView, View.GONE);
        setViewVisibility(progressBar, View.GONE);
        setViewVisibility(reloadFAB, View.VISIBLE);
        if (articles != null) {
            updateNewsInAdapter(articles);
        }
    }

    private void showErrorLoading(@NonNull Throwable throwable) {
        Log.e(LOG_TAG, "Error in news getting", throwable);
        setViewVisibility(progressBar, View.GONE);
        setViewVisibility(errorTextView, View.VISIBLE);
        setViewVisibility(reloadFAB, View.VISIBLE);
    }

    private void setViewVisibility(@Nullable View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    private void updateNewsInAdapter(@Nullable List<Article> articlesList) {
        if (articlesList != null && recyclerView != null) {
            NewsRecyclerAdapter newsRecyclerAdapter = (NewsRecyclerAdapter) recyclerView.getAdapter();
            newsRecyclerAdapter.replaceData(articlesList);
        } else if (recyclerView != null) {
            Snackbar.make(recyclerView, R.string.news_list_activity__adapter_set_error, Snackbar.LENGTH_LONG).show();
        }
    }

    @NonNull
    private RecyclerView.LayoutManager getLayoutManager() {
        if (isHorizontalOrientation(this)) {
            return new GridLayoutManager(this, 2);
        }
        return new LinearLayoutManager(this);
    }

    private void handleOnNewsItemClick(@NonNull View view, @NonNull Article article) {
        NewsDetailesActivity.startActivity(this, article);
    }

    private void setRecyclerView() {
        if (recyclerView != null) {
            recyclerView.setAdapter(
                    new NewsRecyclerAdapter(new ArrayList<>(),
                            new STDDateConverter(getApplicationContext()),
                            this::handleOnNewsItemClick));
            recyclerView.setLayoutManager(getLayoutManager());
            recyclerView.addItemDecoration(new SpaceItemDecoration(
                    getResources().getDimensionPixelSize(
                            R.dimen.activity_news_list__space_between_items)));
        }
    }

    private void setNewsSectionsSpinner() {
        Spinner spinner = findViewById(R.id.activity_news_list__nyt_sections);
        if (spinner != null && configuration != null) {
            new NewsSectionsSpinner(this,
                    spinner,
                    configuration.getNewsSection(),
                    (NewsSection newsSection) -> {
                        configuration.setNewsSection(newsSection);
                        configuration.save();
                        updateNewsFromNYT();
                    }
            );
        }
    }

    private void disposeSubscription(@Nullable Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
