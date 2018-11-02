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
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import ru.fmtk.khlystov.NewsApplication;
import ru.fmtk.khlystov.androidnews.about.AboutActivity;
import ru.fmtk.khlystov.utils.fashionutils.STDDateConverter;
import ru.fmtk.khlystov.AppConfig;
import ru.fmtk.khlystov.newsgetter.Article;
import ru.fmtk.khlystov.newsgetter.NewsGetter;
import ru.fmtk.khlystov.newsgetter.NewsResponse;

import static ru.fmtk.khlystov.utils.ContextUtils.isHorizontalOrientation;

public class NewsListActivity extends AppCompatActivity {

    @Nullable
    private RecyclerView recyclerView = null;

    @Nullable
    private Disposable disposableNewsGetter = null;

    @Nullable
    private AppConfig configuration;

    @Nullable
    private ProgressBar progressBar;

    @Nullable
    private NewsSectionsSpinner newsSectionsSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        recyclerView = findViewById(R.id.activity_news_list__rec_view);
        progressBar = findViewById(R.id.activity_news_list__progress_bar);
        configuration = new AppConfig(this);
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
                Log.e(NewsApplication.LOG_TAG, String.format("Unexpected option: %s", item.getTitle()));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (disposableNewsGetter != null) {
            disposableNewsGetter.dispose();
        }
        super.onDestroy();
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
            showProgress();
            Single<NewsResponse> newsObserver = NewsGetter.getNewsObserver(this,
                    configuration.getNews_section(),
                    configuration.isNeedFetchNewsFromOnlineFlag());
            if (newsObserver != null) {
                disposableNewsGetter = newsObserver.subscribe((@Nullable NewsResponse newsResponse) -> {
                            if (newsResponse != null) {
                                hideProgress();
                                updateNewsInAdapter(newsResponse.getArticles());
                            }
                        },
                        throwable -> {
                            Log.d(NewsApplication.LOG_TAG, "Error in news getting", throwable);
                            hideProgress();
                        });
            }
        }
    }

    private void hideProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
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
        String[] sections = getResources().getStringArray(R.array.nyt_sections);
        if (spinner != null) {
            newsSectionsSpinner = new NewsSectionsSpinner(this,
                    spinner,
                    sections,
                    configuration.getNews_section(),
                    (String newsSection) -> {
                        configuration.setNews_section(newsSection);
                        configuration.save();
                        updateNews();
                    }
            );
        }
    }
}
