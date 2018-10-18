package ru.fmtk.khlystov.androidnews;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;

import ru.fmtk.khlystov.androidnews.fashionutils.IDateConverter;
import ru.fmtk.khlystov.androidnews.fashionutils.STDDateConverter;
import ru.fmtk.khlystov.newsgetter.Article;
import ru.fmtk.khlystov.thirdpartyintentutils.BrowserIntentProvider;
import ru.fmtk.khlystov.thirdpartyintentutils.IntentUtils;

import static android.text.TextUtils.isEmpty;

public class NewsDetailesActivity extends AppCompatActivity {

    @Nullable
    private Article article = null;

    @NonNull
    private static final String ARTICLE_OBJECT = "NewsDetailesActivity_ARTICLE_OBJECT";

    @NonNull
    private static final String simpleString = "0123456789";

    public static void startActivity(@NonNull Context parent, @NonNull Article article) {
        Intent intent = new Intent(parent, NewsDetailesActivity.class);
        intent.putExtra(ARTICLE_OBJECT, article);
        parent.startActivity(intent);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detailes);

        Toolbar myToolbar = findViewById(R.id.activity_news_detailes_layout__toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        article = getIntent().getParcelableExtra(ARTICLE_OBJECT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fillContent();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detailed_news_menu, menu);
        if (article != null) {
            boolean show = !isEmpty(article.getUrl());
            MenuItem shareLinkItem = menu.findItem(R.id.detailed_news_menu__share);
            if (shareLinkItem != null) {
                shareLinkItem.setVisible(show);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.detailed_news_menu__share) {
            goToArticleLink();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToArticleLink() {
        if (article != null) {
            String url = article.getUrl();
            if (url != null) {
                IntentUtils.showIntent(this,
                        findViewById(R.id.activity_news_detailes_layout__content),
                        BrowserIntentProvider.get(url),
                        getString(R.string.no_browser_error));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void fillContent() {
        if (article != null) {
            String urlToImage = article.getUrlToImage();
            String title = article.getTitle();
            String source = article.getSourceName();
            String author = article.getAuthor();
            Date publishedAt = article.getPublishedAt();
            String content = inflateContent(article.getContent(), 4500);

            ImageView imageView = findViewById(R.id.activity_news_detailes_layout__image);
            TextView authorTextView = findViewById(R.id.activity_news_detailes_layout__author);
            TextView titleTextView = findViewById(R.id.activity_news_detailes_layout__title);
            TextView publishedAtTextView = findViewById(R.id.activity_news_detailes_layout__published);
            TextView contentTextView = findViewById(R.id.activity_news_detailes_layout__content);

            if (imageView != null) {
                if (isEmpty(urlToImage)) {
                    imageView.setVisibility(View.GONE);
                } else {
                    imageView.setVisibility(View.VISIBLE);
                    Picasso.get().load(urlToImage).into(imageView);
                }
            }

            if (!isEmpty(source)) {
                android.support.v7.app.ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(source);
                }
            }

            if (authorTextView != null) {
                authorTextView.setText(author);
            }

            if (titleTextView != null) {
                titleTextView.setText(title);
            }
            if (publishedAtTextView != null) {
                publishedAtTextView.setText(dateToString(publishedAt));
            }

            if (!isEmpty(content)) {
                contentTextView.setVisibility(View.VISIBLE);
                contentTextView.setText(content);
            } else {
                contentTextView.setVisibility(View.GONE);
            }
        }
    }

    @NonNull
    private String dateToString(Date publishedAt) {
        IDateConverter dateConverter = new STDDateConverter(getApplicationContext());
        return dateConverter.convert(publishedAt);
    }


    private String inflateContent(@Nullable String content, int needLength) {
        String template = isEmpty(content) ? simpleString : content;
        if (template.length() < needLength) {
            StringBuilder stringBuilder = new StringBuilder(template);
            while (stringBuilder.length() < needLength) {
                stringBuilder.append(template);
            }
            return stringBuilder.toString();
        }
        return content;
    }
}
