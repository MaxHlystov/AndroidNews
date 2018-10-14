package ru.fmtk.khlystov.androidnews;

import android.content.Context;
import android.content.SharedPreferences;
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
import ru.fmtk.khlystov.androidnews.fashionutils.NYTDateConverter;
import ru.fmtk.khlystov.newsgetter.Article;
import ru.fmtk.khlystov.newsgetter.NewsGetter;
import ru.fmtk.khlystov.newsgetter.NewsResponse;

public class NewsListActivity extends AppCompatActivity {

    @NonNull
    private static final String ALTERNATIVE_CONFIG = "NewsListActivity_AltConfig";
    @NonNull
    private static final String CONFIG_GET_ONLINE_NEWS = "NewsListActivity_GetOnlineNews";

    @Nullable
    private Disposable disposableNewsGetter = null;

    private boolean getOnlineNews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        if (savedInstanceState != null) {
            SharedPreferences shp = getSharedPreferences(ALTERNATIVE_CONFIG, Context.MODE_PRIVATE);
            getOnlineNews = shp.getBoolean(CONFIG_GET_ONLINE_NEWS, true);
        } else {
            getOnlineNews = true;
        }
        updateNews();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem getOnlineNewsMenuItem = menu.findItem(R.id.main_menu__get_online_news);
        if (getOnlineNewsMenuItem != null) {
            getOnlineNewsMenuItem.setChecked(getOnlineNews);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu__about_item:
                AboutActivity.startActivity(this);
                break;
            case R.id.main_menu__get_online_news:
                getOnlineNews = !item.isChecked();
                item.setChecked(getOnlineNews);
                if (disposableNewsGetter != null) {
                    disposableNewsGetter.dispose();
                }
                saveConiguration();
                updateNews();
                break;
            default:
                Log.e("NewsApp", String.format("Unexpected option: %s", item.getTitle()));
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

    private void saveConiguration() {
        SharedPreferences.Editor shp = getSharedPreferences(ALTERNATIVE_CONFIG,
                Context.MODE_PRIVATE).edit();
        shp.putBoolean(CONFIG_GET_ONLINE_NEWS, getOnlineNews);
        shp.apply();
    }

    private void updateNews() {
        ProgressBar progressBar = findViewById(R.id.activity_news_list__progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        NewsGetter newsGetter = new NewsGetter(getString(R.string.country_code), getOnlineNews);
        disposableNewsGetter = newsGetter.observeNews((@Nullable NewsResponse newsResponse) -> {
                    progressBar.setVisibility(View.GONE);
                    setNewsAdapter(newsResponse != null ? newsResponse.getArticles() : null);
                },
                throwable -> Log.d("NewsApp", "Error in news getting", throwable));
    }

    private void setNewsAdapter(List<Article> articlesList) {
        RecyclerView recyclerView = findViewById(R.id.activity_news_list__rec_view);
        if (articlesList != null) {
            recyclerView.setAdapter(
                    new NewsRecyclerAdapter(articlesList,
                            new NYTDateConverter(getApplicationContext()),
                            this::onNewsItemClickHandler));
            recyclerView.setLayoutManager(getLayoutManager(getOrientation()));
            recyclerView.addItemDecoration(new SpaceItemDecoration(
                    getResources().getDimensionPixelSize(
                            R.dimen.activity_news_list__space_between_items)));
        } else {
            Snackbar.make(recyclerView, "dfg", Snackbar.LENGTH_LONG).show();
        }
    }

    private RecyclerView.LayoutManager getLayoutManager(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return new GridLayoutManager(this, 2);
        }
        return new LinearLayoutManager(this);
    }

    private void onNewsItemClickHandler(@NonNull View view, @NonNull Article article) {
        NewsDetailesActivity.startActivity(this, article);
    }

    private int getOrientation() {
        return getResources().getConfiguration().orientation;
    }
}
