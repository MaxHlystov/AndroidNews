package ru.fmtk.khlystov.androidnews.newslist;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import ru.fmtk.khlystov.newsgetter.ArticleIdentificator;
import ru.fmtk.khlystov.newsgetter.NewsSection;
import ru.fmtk.khlystov.newsgetter.NewsGetway;
import ru.fmtk.khlystov.utils.IntentUtils;
import ru.fmtk.khlystov.utils.LoadStateControl;
import ru.fmtk.khlystov.utils.RxJavaUtils;
import ru.fmtk.khlystov.utils.fashionutils.STDDateConverter;
import ru.fmtk.khlystov.AppConfig;
import ru.fmtk.khlystov.newsgetter.Article;

import static ru.fmtk.khlystov.utils.ContextUtils.isHorizontalOrientation;

public class NewsListActivity extends AppCompatActivity {

    @NonNull
    private static final String LOG_TAG = "NewsAppNewsListActivity";

    @Nullable
    private LoadStateControl loadStateControl = null;

    @Nullable
    private RecyclerView recyclerView = null;

    @Nullable
    private Disposable disposableUpdateNews = null;

    @Nullable
    private Disposable disposableReadNewsChanged = null;

    @Nullable
    private AppConfig configuration;

    @Nullable
    private FloatingActionButton reloadFAB;

    public static void startActivity(@NonNull Context parent) {
        Intent intent = new Intent(parent, NewsListActivity.class);
        parent.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        configuration = AppConfig.getAppConfig(this.getApplicationContext());
        initViewsVariables();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
                AboutActivity.startActivity(this);
                break;
            default:
                Log.e(LOG_TAG, String.format("Unexpected option: %s", item.getTitle()));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        RxJavaUtils.dispose(disposableUpdateNews);
        disposableUpdateNews = null;
        RxJavaUtils.dispose(disposableReadNewsChanged);
        disposableReadNewsChanged = null;
        super.onStop();
    }

    private void initViewsVariables() {
        recyclerView = findViewById(R.id.activity_news_list__rec_view);
        ProgressBar progressBar = findViewById(R.id.activity_news_list__progress_bar);
        TextView errorTextView = findViewById(R.id.activity_news_list__error_text);
        reloadFAB = findViewById(R.id.activity_news_list__reload_button);
        if (progressBar != null && errorTextView != null && reloadFAB != null) {
            loadStateControl = new LoadStateControl(
                    reloadFAB,
                    progressBar,
                    errorTextView);
        }
        if (reloadFAB != null) {
            reloadFAB.setOnClickListener(v -> this.updateNewsFromNYT());
        }
        setNewsSectionsSpinner();
        setRecyclerView();
    }

    private void updateNewsFromNYT() {
        if (configuration != null) {
            if (loadStateControl != null) {
                loadStateControl.showStartLoading();
            }
            RxJavaUtils.dispose(disposableUpdateNews);
            Completable completable = NewsGetway.updateNewsFromNYT(
                    this,
                    configuration.getNewsSection())
                    .observeOn(AndroidSchedulers.mainThread());
            disposableUpdateNews = completable.subscribe(
                    this::onLoadingComplete,
                    this::showErrorLoading);
        }
    }

    private void onLoadingComplete() {
        if (loadStateControl != null) {
            loadStateControl.showCompletLoading();
        }
        readNews();
    }

    private void readNews() {
        if (configuration != null) {
            RxJavaUtils.dispose(disposableReadNewsChanged);
            Single<List<Article>> newsObserver = NewsGetway.getArticles(this)
                    .observeOn(AndroidSchedulers.mainThread());
            disposableReadNewsChanged = newsObserver.subscribe(
                    this::showNews,
                    this::showErrorLoading);
        }
    }

    private void showNews(@Nullable List<Article> articles) {
        if (loadStateControl != null) {
            loadStateControl.showCompletLoading();
        }
        if (articles != null) {
            updateNewsInAdapter(articles);
        }
    }

    private void showErrorLoading(@NonNull Throwable throwable) {
        Log.e(LOG_TAG, "Error in news getting", throwable);
        if (loadStateControl != null) {
            loadStateControl.showErrorLoading(
                    getString(R.string.activity_news_list__error_loading_message));
        }
    }

    private void updateNewsInAdapter(@Nullable List<Article> articlesList) {
        if (articlesList != null && recyclerView != null) {
            NewsRecyclerAdapter newsRecyclerAdapter = (NewsRecyclerAdapter) recyclerView.getAdapter();
            newsRecyclerAdapter.replaceData(articlesList);
        } else if (recyclerView != null) {
            IntentUtils.showSnackbar(recyclerView,
                    getString(R.string.news_list_activity__adapter_set_error));
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
        NewsDetailesActivity.startViewActivity(this, new ArticleIdentificator(article));
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
}
