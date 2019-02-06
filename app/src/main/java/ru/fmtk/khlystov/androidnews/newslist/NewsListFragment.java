package ru.fmtk.khlystov.androidnews.newslist;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ru.fmtk.khlystov.AppConfig;
import ru.fmtk.khlystov.androidnews.R;
import ru.fmtk.khlystov.androidnews.databus.AppBusHolder;
import ru.fmtk.khlystov.androidnews.databus.AppMessages;
import ru.fmtk.khlystov.androidnews.databus.IDataBus;
import ru.fmtk.khlystov.androidnews.databus.IMessageReceiver;
import ru.fmtk.khlystov.newsgetter.NewsGetway;
import ru.fmtk.khlystov.newsgetter.model.Article;
import ru.fmtk.khlystov.newsgetter.model.ArticleIdentificator;
import ru.fmtk.khlystov.newsgetter.model.NewsSection;
import ru.fmtk.khlystov.utils.ContextUtils;
import ru.fmtk.khlystov.utils.IntentUtils;
import ru.fmtk.khlystov.utils.LoadStateControl;
import ru.fmtk.khlystov.utils.RxJavaUtils;
import ru.fmtk.khlystov.utils.fashionutils.STDDateConverter;

public class NewsListFragment extends Fragment
        implements IMessageReceiver<AppMessages, ArticleIdentificator> {

    @NonNull
    private static final String LOG_TAG = "NewsAppNewsListFrame";

    @Nullable
    private Context applicationContext = null;
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
    private IDataBus<AppMessages, ArticleIdentificator> dataBus = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_news_list, container, false);
        ContextUtils.doWithActivity(getActivity(), (Activity activity) -> {
            applicationContext = activity.getApplicationContext();
            if (applicationContext != null) {
                configuration = AppConfig.getAppConfig(applicationContext);
            }
            return null;
        });
        initDataBus();
        initViews(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        readNews();
    }

    @Override
    public void onStop() {
        RxJavaUtils.disposeIfNotNull(disposableUpdateNews);
        disposableUpdateNews = null;
        RxJavaUtils.disposeIfNotNull(disposableReadNewsChanged);
        disposableReadNewsChanged = null;
        super.onStop();
    }

    private void initDataBus() {
        if (dataBus == null) {
            dataBus = AppBusHolder.register(this);
        }
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.activity_news_list__rec_view);
        ProgressBar progressBar = view.findViewById(R.id.activity_news_list__progress_bar);
        TextView errorTextView = view.findViewById(R.id.activity_news_list__error_text);
        FloatingActionButton reloadFAB = view.findViewById(R.id.activity_news_list__reload_button);
        if (progressBar != null && errorTextView != null && reloadFAB != null) {
            loadStateControl = new LoadStateControl(
                    reloadFAB,
                    progressBar,
                    errorTextView);
        }
        if (reloadFAB != null) {
            reloadFAB.setOnClickListener(v -> this.retrieveOnlineNews());
        }
        setActionBarTitle(getString(R.string.app_name));
        setNewsSectionsSpinner(view);
        setRecyclerView();
    }

    void setActionBarTitle(@NonNull String title) {
        ContextUtils.doWithActivity((AppCompatActivity) getActivity(),
                (AppCompatActivity activity) -> {
                    ActionBar actionBar = activity.getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setTitle(title);
                    }
                    return null;
                });
    }

    private void retrieveOnlineNews() {
        if (configuration != null && applicationContext != null) {
            if (loadStateControl != null) {
                loadStateControl.showStartLoading();
            }
            RxJavaUtils.disposeIfNotNull(disposableUpdateNews);
            disposableUpdateNews = NewsGetway.retrieveOnlineNews(
                    applicationContext,
                    configuration.getNewsSection())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onLoadingComplete,
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
        if (configuration != null && applicationContext != null) {
            RxJavaUtils.disposeIfNotNull(disposableReadNewsChanged);
            Single<List<Article>> newsObserver = NewsGetway.getArticles(applicationContext)
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
        Log.e(LOG_TAG, getString(R.string.news_list_fragment__error_loading_news), throwable);
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
    private RecyclerView.LayoutManager getLayoutManager(@NonNull Context context) {
        return new LinearLayoutManager(context);
    }

    private void handleOnNewsItemClick(@NonNull Article article) {
        if (dataBus != null) {
            dataBus.sendToAll(this, AppMessages.OPEN, new ArticleIdentificator(article));
        }
    }

    private void setRecyclerView() {
        if (recyclerView != null && applicationContext != null) {
            recyclerView.setAdapter(
                    new NewsRecyclerAdapter(new ArrayList<>(),
                            new STDDateConverter(applicationContext),
                            (view, article) -> handleOnNewsItemClick(article)));
            recyclerView.setLayoutManager(getLayoutManager(applicationContext));
            recyclerView.addItemDecoration(new SpaceItemDecoration(
                    getResources().getDimensionPixelSize(
                            R.dimen.activity_news_list__space_between_items)));
        }
    }

    private void setNewsSectionsSpinner(View view) {
        Spinner spinner = view.findViewById(R.id.activity_news_list__nyt_sections);
        Activity activity = getActivity();
        if (spinner != null && configuration != null && activity != null) {
            new NewsSectionsSpinner(activity,
                    spinner,
                    configuration.getNewsSection(),
                    (NewsSection newsSection) -> {
                        configuration.setNewsSection(newsSection);
                        configuration.save();
                        retrieveOnlineNews();
                    }
            );
        }
    }

    @Override
    public int getReceiverId() {
        return R.layout.activity_news_list;
    }

    @Override
    public void onMessageSent(int senderId, @NonNull AppMessages message,
                              @Nullable ArticleIdentificator argument) {
        switch (message) {
            case UPDATE:
                readNews();
                break;
            case DELETE:
                readNews();
                break;
            default:
                break;
        }
    }
}
