package ru.fmtk.khlystov.androidnews;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ru.fmtk.khlystov.newsgetter.Article;
import ru.fmtk.khlystov.newsgetter.ArticleIdentificator;
import ru.fmtk.khlystov.newsgetter.NewsGetway;
import ru.fmtk.khlystov.utils.IntentUtils;
import ru.fmtk.khlystov.utils.LoadStateControl;
import ru.fmtk.khlystov.utils.RxJavaUtils;
import ru.fmtk.khlystov.utils.fashionutils.EditDateConverter;
import ru.fmtk.khlystov.utils.fashionutils.IDateConverter;
import ru.fmtk.khlystov.utils.fashionutils.STDDateConverter;

import static android.text.TextUtils.isEmpty;

public class NewsDetailesActivity extends AppCompatActivity {

    @NonNull
    private static final String LOG_TAG = "NewsAppNewsDetailes";

    @NonNull
    private static final String ARTICLE_ID = "NewsDetailesActivity_ARTICLE_ID";
    @NonNull
    private static final String ARTICLE_OBJECT = "NewsDetailesActivity_ARTICLE_OBJECT";

    @NonNull
    private static final String EDIT_MODE = "NewsDetailesActivity_EDIT_MODE";

    private boolean editMode = false;

    @Nullable
    private ArticleIdentificator articleId = null;

    @Nullable
    private Article article = null;

    @Nullable
    private LoadStateControl loadStateControl = null;

    @Nullable
    private Disposable disposableReadNewsChanged = null;

    @Nullable
    private Disposable disposableDeletedNews = null;

    @Nullable
    private Disposable disposableUpdateNews = null;

    public static void startViewActivity(@NonNull Context parent,
                                         @NonNull ArticleIdentificator articleId) {
        Intent intent = new Intent(parent, NewsDetailesActivity.class);
        intent.putExtra(ARTICLE_ID, articleId);
        intent.putExtra(EDIT_MODE, false);
        parent.startActivity(intent);
    }

    public static void startEditActivity(@NonNull Context parent,
                                         @NonNull ArticleIdentificator articleId,
                                         @NonNull Article article) {
        Intent intent = new Intent(parent, NewsDetailesActivity.class);
        intent.putExtra(ARTICLE_ID, articleId);
        intent.putExtra(ARTICLE_OBJECT, article);
        intent.putExtra(EDIT_MODE, true);
        parent.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        restoreState(savedInstanceState);

        if (editMode) {
            setContentView(R.layout.activity_edit_news);
        } else {
            setContentView(R.layout.activity_news_detailes);
        }

        initToolbar();
        initViewsVariables();
        if (articleId == null) {
            showErrorLoading(getString(R.string.activity_news_detailes_layout__error_loading_message),
                    new Throwable("Article identifier is not received with intent."));
        } else {
            if (article == null) {
                readArticle(articleId);
            } else {
                fillContent();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (articleId != null) {
            outState.putParcelable(ARTICLE_ID, articleId);
        }
        if (article != null) {
            outState.putParcelable(ARTICLE_OBJECT, article);
        }
        outState.putBoolean(EDIT_MODE, editMode);
    }

    @Override
    protected void onStop() {
        RxJavaUtils.dispose(disposableReadNewsChanged);
        disposableReadNewsChanged = null;
        RxJavaUtils.dispose(disposableDeletedNews);
        disposableDeletedNews = null;
        RxJavaUtils.dispose(disposableUpdateNews);
        disposableUpdateNews = null;
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.detailed_news_menu, menu);
        setMenuAppearance(menu);
        return true;
    }

    private void restoreState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            articleId = intent.getParcelableExtra(ARTICLE_ID);
            editMode = intent.getBooleanExtra(EDIT_MODE, false);
            if (editMode) {
                article = intent.getParcelableExtra(ARTICLE_OBJECT);
            }
        } else {
            articleId = savedInstanceState.getParcelable(ARTICLE_ID);
            editMode = savedInstanceState.getBoolean(EDIT_MODE, false);
            article = savedInstanceState.getParcelable(ARTICLE_OBJECT);
        }
    }

    private void setMenuAppearance(@NonNull Menu menu) {
        MenuItem shareLinkItem = menu.findItem(R.id.detailed_news_menu__share);
        MenuItem saveItem = menu.findItem(R.id.detailed_news_menu__save_item);
        MenuItem editItem = menu.findItem(R.id.detailed_news_menu__edit_item);

        setMenuItemVisibility(shareLinkItem,
                article != null && !isEmpty(article.getUrl()));
        setMenuItemVisibility(saveItem,
                article != null && editMode);
        setMenuItemVisibility(editItem,
                article != null && !editMode);
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
                Log.e(LOG_TAG, String.format("Unexpected option: %s", item.getTitle()));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initToolbar() {
        Toolbar myToolbar = findViewById(R.id.activity_news_detailes_layout__toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initViewsVariables() {
        loadStateControl = new LoadStateControl(
                findViewById(R.id.activity_news_detailes_layout__article),
                findViewById(R.id.activity_news_detailes_layout__progress_bar),
                findViewById(R.id.activity_news_detailes_layout__error_text));
    }


    private void readArticle(ArticleIdentificator articleId) {
        if (loadStateControl != null) {
            loadStateControl.showStartLoading();
        }
        RxJavaUtils.dispose(disposableReadNewsChanged);
        disposableReadNewsChanged = NewsGetway.getArticleById(this, articleId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onArticleLoad,
                        (Throwable throwable) -> this.showErrorLoading(
                                getString(R.string.activity_news_detailes_layout__error_loading_message),
                                throwable));
    }

    private void editArticle() {
        if (article != null && articleId != null) {
            NewsDetailesActivity.startEditActivity(this, articleId, article);
            finish();
        }
    }

    private void deleteArticle() {
        if (articleId != null) {
            RxJavaUtils.dispose(disposableDeletedNews);
            disposableDeletedNews = NewsGetway.deleteArticleById(this, articleId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onArticleDeleted,
                            (Throwable throwable) -> this.showErrorLoading(
                                    getString(R.string.activity_news_detailes_layout__error_delete_article),
                                    throwable));
        }
    }

    private void saveArticle() {
        if (editMode && articleId != null && article != null) {
            updateArticleFromActivity();
            RxJavaUtils.dispose(disposableUpdateNews);
            disposableUpdateNews = NewsGetway.updateArticle(this, articleId, article)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::onArticleSaved,
                            (Throwable throwable) -> this.showErrorLoading(
                                    getString(R.string.news_detailes_activity__error_saving_article),
                                    throwable)
                    );
        }
    }

    private void onArticleLoad(@Nullable Article article) {
        this.article = article;
        if (loadStateControl != null) {
            if (article != null) {
                fillContent();
                loadStateControl.showCompletLoading();
            } else {
                loadStateControl.showErrorLoading(
                        getString(R.string.activity_news_detailes_layout__error_loading_message));
            }
        }
    }

    private void onArticleSaved() {
        if (article != null) {
            articleId = new ArticleIdentificator(article);
        }
        final View view = findViewById(R.id.activity_news_detailes_layout__article);
        IntentUtils.showSnackbar(view, "Changed were saved.");
    }

    private void onArticleDeleted() {
        finish();
    }

    private void fillContent() {
        if (article != null) {
            String urlToImage = article.getUrlToImage();
            ImageView imageView = findViewById(R.id.activity_news_detailes_layout__image);
            if (imageView != null) {
                if (isEmpty(urlToImage)) {
                    imageView.setVisibility(View.GONE);
                } else {
                    imageView.setVisibility(View.VISIBLE);
                    Picasso.get().load(urlToImage).into(imageView);
                }
            }

            TextView authorTextView = findViewById(R.id.activity_news_detailes_layout__author);
            String author = article.getAuthor();
            if (authorTextView != null) {
                authorTextView.setText(author);
            }

            String title = article.getTitle();
            if (!isEmpty(title)) {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(title);
                }
            }
            TextView titleTextView = findViewById(R.id.activity_news_detailes_layout__title);
            if (titleTextView != null) {
                titleTextView.setText(title);
            }

            Date publishedAt = article.getPublishedAt();
            TextView publishedAtTextView = findViewById(R.id.activity_news_detailes_layout__published);
            if (publishedAtTextView != null) {
                publishedAtTextView.setText(dateToString(publishedAt));
            }

            if (editMode) {
                String description = article.getContent();
                TextView descriptionTextView = findViewById(R.id.activity_news_detailes_layout__description);
                if (!isEmpty(description)) {
                    descriptionTextView.setVisibility(View.VISIBLE);
                    descriptionTextView.setText(description);
                } else {
                    descriptionTextView.setVisibility(View.GONE);
                }
            }

            String content = article.getContent();
            TextView contentTextView = findViewById(R.id.activity_news_detailes_layout__content);
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
        if (article != null) {
            String url = article.getUrl();
            if (url != null) {
                IntentUtils.showIntent(this,
                        findViewById(R.id.activity_news_detailes_layout__content),
                        IntentUtils.getBrowserIntent(url),
                        getString(R.string.no_browser_error));
            }
        }
    }

    private void updateArticleFromActivity() {
        if (!editMode || article == null) {
            return;
        }
        Article.Builder builder = new Article.Builder(article);
        applyByTextFromField(R.id.activity_news_detailes_layout__author, builder::setAuthor);
        applyByTextFromField(R.id.activity_news_detailes_layout__title, builder::setTitle);
        applyByTextFromField(R.id.activity_news_detailes_layout__content, builder::setContent);
        applyByTextFromField(R.id.activity_news_detailes_layout__description, builder::setDescription);
        applyByTextFromField(R.id.activity_news_detailes_layout__published,
                (String text) -> {
                    Date date = STDDateConverter.unconvert(text, null);
                    if (date != null) {
                        builder.setPublishedAt(date);
                    }
                });
        article = builder.build();
    }

    private void applyByTextFromField(int fieldId, Consumer<String> consumer) {
        TextView textView = findViewById(fieldId);
        if (textView != null) {
            consumer.apply(textView.getText().toString());

        }
    }

    private void setMenuItemVisibility(@Nullable MenuItem item, boolean visible) {
        if (item != null) {
            item.setVisible(visible);
        }
    }

    @NonNull
    private String dateToString(Date publishedAt) {
        IDateConverter dateConverter;
        if (editMode) {
            dateConverter = new EditDateConverter(getApplicationContext());
        } else {
            dateConverter = new STDDateConverter(getApplicationContext());
        }
        return dateConverter.convert(publishedAt);
    }

    // in the lack of std functional interfaces
    public interface Consumer<T> {
        void apply(T data);
    }
}
