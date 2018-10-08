package ru.fmtk.khlystov.androidnews;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import io.reactivex.disposables.Disposable;
import ru.fmtk.khlystov.androidnews.fashionutils.NYTDateConverter;
import ru.fmtk.khlystov.newsgetter.Article;
import ru.fmtk.khlystov.newsgetter.NewsGetter;
import ru.fmtk.khlystov.newsgetter.NewsResponse;

public class NewsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        if (savedInstanceState != null) {
            SharedPreferences shp = getPreferences(Context.MODE_PRIVATE);
            getOnlineNews = shp.getBoolean(CONFIG_GET_ONLINE_NEWS, true);
        } else {
            getOnlineNews = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNewsAdapter();
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
                setNewsAdapter();
                break;
            default:
                Log.e("NewsApp", String.format("Unexpected option: %s", item.getTitle()));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        SharedPreferences.Editor shp = getPreferences(Context.MODE_PRIVATE).edit();
        shp.putBoolean(CONFIG_GET_ONLINE_NEWS, getOnlineNews);
        shp.apply();
    }

    @Override
    protected void onDestroy() {
        if (disposableNewsGetter != null) {
            disposableNewsGetter.dispose();
        }
        super.onDestroy();
    }

    private void setNewsAdapter() {
        RecyclerView recyclerView = findViewById(R.id.activity_news_list__rec_view);
        NewsGetter newsGetter = new NewsGetter(getString(R.string.country_code), getOnlineNews);
        disposableNewsGetter = newsGetter.observeNews((NewsResponse newsResponse) -> {
                    recyclerView.setAdapter(
                            new NewsRecyclerAdapter(newsResponse.getArticles(),
                                    new NYTDateConverter(getApplicationContext()),
                                    this::onNewsItemClickHandler));
                    recyclerView.setLayoutManager(getLayoutManager(getOrientation()));
                    recyclerView.addItemDecoration(new SpaceItemDecoration(
                            getResources().getDimensionPixelSize(
                                    R.dimen.activity_news_list__space_between_items)));
                },
                throwable -> Log.d("NewsApp", "Error in news getting", throwable));
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

    @NonNull
    private static final String CONFIG_GET_ONLINE_NEWS = "NewsListActivity_GetOnlineNews";

    @Nullable
    private Disposable disposableNewsGetter = null;

    private boolean getOnlineNews;
}
