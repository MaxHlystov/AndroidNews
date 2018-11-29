package ru.fmtk.khlystov.androidnews;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import ru.fmtk.khlystov.androidnews.about.AboutActivity;
import ru.fmtk.khlystov.newsgetter.NewsSection;
import ru.fmtk.khlystov.utils.fashionutils.STDDateConverter;
import ru.fmtk.khlystov.AppConfig;
import ru.fmtk.khlystov.newsgetter.Article;
import ru.fmtk.khlystov.newsgetter.NewsGetter;
import ru.fmtk.khlystov.newsgetter.NewsResponse;

import static ru.fmtk.khlystov.utils.ContextUtils.isHorizontalOrientation;

public class NewsListActivity extends AppCompatActivity {

    @NonNull
    private static final String LOG_TAG = "NewsAppNewsListActivity";

    @Nullable
    private RecyclerView recyclerView = null;

    @Nullable
    private Disposable disposableNewsGetter = null;

    @Nullable
    private AppConfig configuration;

    @Nullable
    private ProgressBar progressBar;

    @Nullable
    private Button reloadButton;

    @Nullable
    private TextView errorTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        initViewsVariables();
        configuration = new AppConfig(this.getApplicationContext());
        if (reloadButton != null) {
            reloadButton.setOnClickListener(v -> this.updateNews());
        }
        setNewsSectionsSpinner();
        setRecyclerView();
        updateNews();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem getOnlineNewsMenuItem = menu.findItem(R.id.main_menu__get_online_news_item);
        if (getOnlineNewsMenuItem != null && configuration != null) {
            getOnlineNewsMenuItem.setChecked(configuration.isNeedFetchNewsFromOnlineFlag());
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu__about_item:
                onMainMenuAboutItemClicked(item);
                break;
            case R.id.main_menu__get_online_news_item:
                onMainMenuGetOnlineNewsItemClicked(item);
                break;
            default:
                Log.e(LOG_TAG, String.format("Unexpected option: %s", item.getTitle()));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        if (disposableNewsGetter != null) {
            disposableNewsGetter.dispose();
        }
        super.onStop();
    }

    private void initViewsVariables() {
        recyclerView = findViewById(R.id.activity_news_list__rec_view);
        progressBar = findViewById(R.id.activity_news_list__progress_bar);
        reloadButton = findViewById(R.id.activity_news_list__reload_button);
        errorTextView = findViewById(R.id.activity_news_list__error_text);
    }

    private void onMainMenuAboutItemClicked(@NonNull MenuItem item) {
        AboutActivity.startActivity(this);
    }

    private void onMainMenuGetOnlineNewsItemClicked(@NonNull MenuItem item) {
        if (configuration != null) {
            configuration.setNeedFetchNewsFromOnlineFlag(!item.isChecked());
            item.setChecked(configuration.isNeedFetchNewsFromOnlineFlag());
            configuration.save();
        }
        if (disposableNewsGetter != null) {
            disposableNewsGetter.dispose();
        }
        updateNews();
    }

    private void updateNews() {
        if (configuration != null) {
            showStartLoading();
            Single<NewsResponse> newsObserver = NewsGetter.getNewsObserver(
                    this,
                    configuration.getNewsSection(),
                    configuration.isNeedFetchNewsFromOnlineFlag());
            if (newsObserver != null) {
                disposableNewsGetter = newsObserver.subscribe(
                        this::showCompletLoading,
                        this::showErrorLoading);
            }
        }
    }

    private void showStartLoading() {
        setViewVisibility(progressBar, View.VISIBLE);
        setViewVisibility(reloadButton, View.GONE);
        setViewVisibility(errorTextView, View.GONE);
    }

    private void showCompletLoading(@Nullable NewsResponse newsResponse) {
        setViewVisibility(errorTextView, View.GONE);
        if (newsResponse != null) {
            updateNewsInAdapter(newsResponse.getArticles());
        }
        setViewVisibility(progressBar, View.GONE);
    }

    private void showErrorLoading(@NonNull Throwable throwable) {
        Log.d(LOG_TAG, "Error in news getting", throwable);
        setViewVisibility(progressBar, View.GONE);
        setViewVisibility(reloadButton, View.VISIBLE);
        setViewVisibility(errorTextView, View.VISIBLE);
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
                        updateNews();
                    }
            );
        }
    }
}
