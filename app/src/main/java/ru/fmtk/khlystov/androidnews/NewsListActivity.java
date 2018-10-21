package ru.fmtk.khlystov.androidnews;

import android.content.res.Configuration;
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

import java.util.List;

import io.reactivex.disposables.Disposable;
import ru.fmtk.khlystov.androidnews.fashionutils.STDDateConverter;
import ru.fmtk.khlystov.appconfig.AppConfig;
import ru.fmtk.khlystov.newsgetter.Article;
import ru.fmtk.khlystov.newsgetter.NewsGetter;
import ru.fmtk.khlystov.newsgetter.NewsResponse;

import static ru.fmtk.khlystov.androidnews.ContextUtils.isHorizontalOrientation;

public class NewsListActivity extends AppCompatActivity {

    @Nullable
    private Disposable disposableNewsGetter = null;

    @NonNull
    private AppConfig configuration;

    @Nullable
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        progressBar = findViewById(R.id.activity_news_list__progress_bar);
        configuration = new AppConfig(this);
        updateNews();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem getOnlineNewsMenuItem = menu.findItem(R.id.main_menu__get_online_news_item);
        if (getOnlineNewsMenuItem != null) {
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
                Log.e("NewsApp", String.format("Unexpected option: %s", item.getTitle()));
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
        configuration.setNeedFetchNewsFromOnlineFlag(!item.isChecked());
        item.setChecked(configuration.isNeedFetchNewsFromOnlineFlag());
        configuration.save();
        if (disposableNewsGetter != null) {
            disposableNewsGetter.dispose();
        }
        updateNews();
    }

    private void updateNews() {
        showProgress();
        NewsGetter newsGetter = new NewsGetter(getString(R.string.country_code), configuration.isNeedFetchNewsFromOnlineFlag());
        disposableNewsGetter = newsGetter.observeNews((@Nullable NewsResponse newsResponse) -> {
                    if (newsResponse != null) {
                        hideProgress();
                        setNewsAdapter(newsResponse.getArticles());
                    }
                },
                throwable -> Log.d("NewsApp", "Error in news getting", throwable));
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

    private void setNewsAdapter(@Nullable List<Article> articlesList) {
        RecyclerView recyclerView = findViewById(R.id.activity_news_list__rec_view);
        if (articlesList != null) {
            recyclerView.setAdapter(
                    new NewsRecyclerAdapter(articlesList,
                            new STDDateConverter(getApplicationContext()),
                            this::onNewsItemClickHandler));
            recyclerView.setLayoutManager(getLayoutManager());
            recyclerView.addItemDecoration(new SpaceItemDecoration(
                    getResources().getDimensionPixelSize(
                            R.dimen.activity_news_list__space_between_items)));
        } else {
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

    private void onNewsItemClickHandler(@NonNull View view, @NonNull Article article) {
        NewsDetailesActivity.startActivity(this, article);
    }
}
