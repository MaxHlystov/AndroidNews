package ru.fmtk.khlystov.androidnews.newsdetails;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ru.fmtk.khlystov.androidnews.R;
import ru.fmtk.khlystov.androidnews.databus.AppBusHolder;
import ru.fmtk.khlystov.androidnews.databus.AppMessages;
import ru.fmtk.khlystov.androidnews.databus.IDataBus;
import ru.fmtk.khlystov.androidnews.databus.IMessageReceiver;
import ru.fmtk.khlystov.newsgetter.NewsGetway;
import ru.fmtk.khlystov.newsgetter.model.Article;
import ru.fmtk.khlystov.newsgetter.model.ArticleIdentificator;
import ru.fmtk.khlystov.utils.Consumer;
import ru.fmtk.khlystov.utils.ContextUtils;
import ru.fmtk.khlystov.utils.IntentUtils;
import ru.fmtk.khlystov.utils.LoadStateControl;
import ru.fmtk.khlystov.utils.RxJavaUtils;
import ru.fmtk.khlystov.utils.fashionutils.EditDateConverter;
import ru.fmtk.khlystov.utils.fashionutils.IDateConverter;
import ru.fmtk.khlystov.utils.fashionutils.STDDateConverter;

import static android.text.TextUtils.isEmpty;

public class NewsDetailsFragment extends Fragment
        implements IMessageReceiver<AppMessages, ArticleIdentificator> {

    @NonNull
    private static final String LOG_TAG = "NewsAppNewsDetails";

    @NonNull
    private static final String ARTICLE_ID = "NewsDetailsActivity_ARTICLE_ID";
    @NonNull
    private static final String ARTICLE_OBJECT = "NewsDetailsActivity_ARTICLE_OBJECT";

    @NonNull
    private static final String EDIT_MODE = "NewsDetailsActivity_EDIT_MODE";

    private boolean editMode = false;

    @Nullable
    private IDataBus<AppMessages, ArticleIdentificator> dataBus = null;

    @Nullable
    private ArticleIdentificator articleId = null;

    @Nullable
    private Article article = null;

    @Nullable
    private View mainView = null;

    @Nullable
    private LoadStateControl loadStateControl = null;

    @Nullable
    private Disposable disposableReadNewsChanged = null;

    @Nullable
    private Disposable disposableDeletedNews = null;

    @Nullable
    private Disposable disposableUpdateNews = null;

    @NonNull
    public static NewsDetailsFragment getViewFragment(@Nullable ArticleIdentificator articleId) {
        NewsDetailsFragment fragment = new NewsDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARTICLE_ID, articleId);
        bundle.putBoolean(EDIT_MODE, false);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    public static NewsDetailsFragment getEditFragment(@NonNull ArticleIdentificator articleId) {
        NewsDetailsFragment fragment = new NewsDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARTICLE_ID, articleId);
        bundle.putBoolean(EDIT_MODE, true);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        restoreState(savedInstanceState);
        mainView = inflater.inflate(getLayoutId(), container, false);
        subscribeDataBus();
        if (mainView != null) {
            initViewsVariables(mainView);
        }
        if (articleId == null) {
            showErrorLoading(getString(R.string.activity_news_detailes_layout__error_loading_message),
                    new Throwable("Article identifier is not received with intent."));
        } else {
            if (article == null) {
                readArticle(articleId);
            } else {
                fillContent(mainView);
            }
        }
        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setBackButtonVisible(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (articleId != null) {
            outState.putParcelable(ARTICLE_ID, articleId);
        }
        if (article != null && editMode) {
            outState.putParcelable(ARTICLE_OBJECT, article);
        }
        outState.putBoolean(EDIT_MODE, editMode);
    }

    @Override
    public void onStop() {
        RxJavaUtils.disposeIfNotNull(disposableReadNewsChanged);
        disposableReadNewsChanged = null;
        RxJavaUtils.disposeIfNotNull(disposableDeletedNews);
        disposableDeletedNews = null;
        RxJavaUtils.disposeIfNotNull(disposableUpdateNews);
        disposableUpdateNews = null;
        setBackButtonVisible(false);
        if (editMode && dataBus != null && articleId != null) {
            dataBus.sendToAll(this, AppMessages.OPEN, articleId);
        }
        unsubscribeDataBus();
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(@Nullable Menu menu, @Nullable MenuInflater inflater) {
        if (menu != null && inflater != null) {
            inflater.inflate(R.menu.detailed_news_menu, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        setMenuAppearance(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detailed_news_menu__share:
                goToArticleLink();
                break;
            case R.id.detailed_news_menu__save_item:
                saveArticle();
                break;
            case R.id.detailed_news_menu__edit_item:
                editArticle();
                break;
            case R.id.detailed_news_menu__delete_item:
                deleteArticle();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getLayoutId() {
        if (editMode) {
            return R.layout.activity_edit_news;
        }
        return R.layout.activity_news_details;
    }

    private void subscribeDataBus() {
        if (dataBus == null) {
            dataBus = AppBusHolder.register(this);
        }
    }

    private void unsubscribeDataBus() {
        if (dataBus != null) {
            dataBus.sendToAll(this, AppMessages.CLOSE, articleId);
            AppBusHolder.unregister(this);
            dataBus = null;
        }
    }

    private void setMenuAppearance(@NonNull Menu menu) {
        MenuItem saveItem = menu.findItem(R.id.detailed_news_menu__save_item);
        MenuItem shareLinkItem = menu.findItem(R.id.detailed_news_menu__share);
        MenuItem editItem = menu.findItem(R.id.detailed_news_menu__edit_item);
        setMenuItemVisibility(saveItem,
                article != null && editMode);
        setMenuItemVisibility(shareLinkItem,
                article != null && !isEmpty(article.getUrl()));
        setMenuItemVisibility(editItem,
                article != null && !editMode);
    }

    private void restoreState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                articleId = bundle.getParcelable(ARTICLE_ID);
                editMode = bundle.getBoolean(EDIT_MODE, false);
            }
        } else {
            articleId = savedInstanceState.getParcelable(ARTICLE_ID);
            editMode = savedInstanceState.getBoolean(EDIT_MODE, false);
            if (editMode) {
                // Save edited article
                article = savedInstanceState.getParcelable(ARTICLE_OBJECT);
            }

        }
    }

    private void initViewsVariables(@NonNull View view) {
        loadStateControl = new LoadStateControl(
                view.findViewById(R.id.activity_news_details_layout__article),
                view.findViewById(R.id.activity_news_details_layout__progress_bar),
                view.findViewById(R.id.activity_news_details_layout__error_text));
    }

    private void readArticle(@NonNull ArticleIdentificator articleId) {
        if (loadStateControl != null) {
            loadStateControl.showStartLoading();
        }
        RxJavaUtils.disposeIfNotNull(disposableReadNewsChanged);
        Context context = getActivity();
        if (context != null) {
            disposableReadNewsChanged = NewsGetway.getArticleById(context, articleId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::onArticleLoad,
                            (Throwable throwable) -> this.showErrorLoading(
                                    getString(R.string.activity_news_detailes_layout__error_loading_message),
                                    throwable));
        }
    }

    private void editArticle() {
        ContextUtils.popFragment(getActivity());
        if (articleId != null && dataBus != null) {
            dataBus.sendToAll(this, AppMessages.EDIT, articleId);
        }
    }

    private void deleteArticle() {
        if (articleId != null) {
            RxJavaUtils.disposeIfNotNull(disposableDeletedNews);
            ContextUtils.doWithActivity(getActivity(), (Activity activity) -> {
                disposableDeletedNews = NewsGetway.deleteArticleById(activity, articleId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onArticleDeleted,
                                (Throwable throwable) -> this.showErrorLoading(
                                        getString(R.string.activity_news_detailes_layout__error_delete_article),
                                        throwable));
                return null;
            });
        }
    }

    private void saveArticle() {
        if (editMode && articleId != null && article != null) {
            updateArticleFromActivity();
            RxJavaUtils.disposeIfNotNull(disposableUpdateNews);
            ContextUtils.doWithActivity(getActivity(), (Activity activity) -> {
                disposableUpdateNews = NewsGetway.updateArticle(activity, articleId, article)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::onArticleSaved,
                                (Throwable throwable) -> this.showErrorLoading(
                                        getString(R.string.news_detailes_activity__error_saving_article),
                                        throwable)
                        );
                return null;
            });
        }
    }

    private void onArticleLoad(@Nullable Article article) {
        this.article = article;
        if (loadStateControl != null) {
            if (article != null) {
                fillContent(mainView);
                loadStateControl.showCompletLoading();
                ContextUtils.doWithActivity(getActivity(), (Activity activity) -> {
                    activity.invalidateOptionsMenu();
                    return null;
                });
            } else {
                loadStateControl.showErrorLoading(
                        getString(R.string.activity_news_detailes_layout__error_loading_message));
            }
        }
    }

    private void onArticleSaved() {
        Log.d(LOG_TAG, "onArticleSaved");
        if (articleId != null && dataBus != null) {
            dataBus.sendToAll(this, AppMessages.UPDATE, articleId);
        }
        if (article != null) {
            articleId = new ArticleIdentificator(article);
        }
        if (mainView != null) {
            final View articleView = mainView.findViewById(R.id.activity_news_details_layout__article);
            if (articleView != null) {
                IntentUtils.showSnackbar(articleView, getString(R.string.activity_news_details__article_saved));
            }
        }
    }

    private void onArticleDeleted() {
        ContextUtils.popFragment(getActivity());
        if (articleId != null && dataBus != null) {
            dataBus.sendToAll(this, AppMessages.DELETE, articleId);
        }
    }

    private void fillContent(View view) {
        if (article != null) {
            String urlToImage = article.getUrlToImage();
            ImageView imageView = view.findViewById(R.id.activity_news_details_layout__image);
            if (imageView != null) {
                if (isEmpty(urlToImage)) {
                    imageView.setVisibility(View.GONE);
                } else {
                    imageView.setVisibility(View.VISIBLE);
                    Picasso.get().load(urlToImage).into(imageView);
                }
            }

            TextView authorTextView = view.findViewById(R.id.activity_news_details_layout__author);
            String author = article.getAuthor();
            if (authorTextView != null) {
                authorTextView.setText(author);
            }

            String title = article.getTitle();
            if (!isEmpty(title)) {
                ContextUtils.doWithActivity((AppCompatActivity) getActivity(), (AppCompatActivity activity) -> {
                    ActionBar actionBar = activity.getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setTitle(title);
                    }
                    return null;
                });
            }
            TextView titleTextView = view.findViewById(R.id.activity_news_details_layout__title);
            if (titleTextView != null) {
                titleTextView.setText(title);
            }

            Date publishedAt = article.getPublishedAt();
            TextView publishedAtTextView = view.findViewById(R.id.activity_news_details_layout__published);
            if (publishedAtTextView != null) {
                publishedAtTextView.setText(dateToString(publishedAt));
            }

            if (editMode) {
                String description = article.getContent();
                TextView descriptionTextView = view.findViewById(R.id.activity_news_details_layout__description);
                if (!isEmpty(description)) {
                    descriptionTextView.setVisibility(View.VISIBLE);
                    descriptionTextView.setText(description);
                } else {
                    descriptionTextView.setVisibility(View.GONE);
                }
            }

            String content = article.getContent();
            TextView contentTextView = view.findViewById(R.id.activity_news_details_layout__content);
            if (!isEmpty(content)) {
                contentTextView.setVisibility(View.VISIBLE);
                contentTextView.setText(content);
            } else {
                contentTextView.setVisibility(View.GONE);
            }
        }
    }

    private void showErrorLoading(@NonNull String errorMessage,
                                  @NonNull Throwable throwable) {
        Log.e(LOG_TAG, errorMessage, throwable);
        if (loadStateControl != null) {
            loadStateControl.showErrorLoading(
                    errorMessage);
        }
    }

    private void goToArticleLink() {
        if (article != null && mainView != null) {
            ContextUtils.doWithActivity(getActivity(), (Activity activity) -> {
                String url = article.getUrl();
                if (url != null) {
                    IntentUtils.showIntent(activity,
                            mainView.findViewById(R.id.activity_news_details_layout__content),
                            IntentUtils.getBrowserIntent(url),
                            getString(R.string.no_browser_error));
                }
                return null;
            });
        }
    }

    private void updateArticleFromActivity() {
        if (!editMode || article == null) {
            return;
        }
        Article.Builder builder = new Article.Builder(article);
        applyByTextFromField(R.id.activity_news_details_layout__author, builder::setAuthor);
        applyByTextFromField(R.id.activity_news_details_layout__title, builder::setTitle);
        applyByTextFromField(R.id.activity_news_details_layout__content, builder::setContent);
        applyByTextFromField(R.id.activity_news_details_layout__description, builder::setDescription);
        applyByTextFromField(R.id.activity_news_details_layout__published,
                (String text) -> {
                    Date date = EditDateConverter.unconvert(text, null);
                    if (date != null) {
                        builder.setPublishedAt(date);
                    }
                });
        article = builder.build();
    }

    private void applyByTextFromField(int fieldId, Consumer<String> consumer) {
        if (mainView != null) {
            TextView textView = mainView.findViewById(fieldId);
            if (textView != null) {
                consumer.apply(textView.getText().toString());
            }
        }
    }

    @NonNull
    private String dateToString(Date publishedAt) {
        IDateConverter dateConverter = ContextUtils.doWithActivity(getActivity(),
                (Activity activity) -> {
                    if (editMode) {
                        return new EditDateConverter(activity.getApplicationContext());
                    } else {
                        return new STDDateConverter(activity.getApplicationContext());
                    }
                });
        if (dateConverter != null) {
            return dateConverter.convert(publishedAt);
        }
        return publishedAt.toString();
    }


    private void setMenuItemVisibility(@Nullable MenuItem item, boolean visible) {
        if (item != null) {
            item.setVisible(visible);
        }
    }

    @Override
    public int getReceiverId() {
        return getLayoutId();
    }

    @Override
    public void onMessageSent(int senderId, @NonNull AppMessages message,
                              @Nullable ArticleIdentificator argument) {
        switch (message) {
            case UPDATE:
                Log.d(LOG_TAG, "onMessageSent: " + message.toString());
                if (!editMode && Objects.equals(articleId, argument)) {
                    readArticle(articleId);
                }
                break;
            default:
                break;
        }
    }

    private void setBackButtonVisible(boolean b) {
        ContextUtils.doWithActivity((AppCompatActivity) getActivity(), (AppCompatActivity activity) -> {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(b);
            }
            return null;
        });
    }
}
