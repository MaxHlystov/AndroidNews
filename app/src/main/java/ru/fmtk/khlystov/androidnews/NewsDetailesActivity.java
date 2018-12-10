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
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Date;

import ru.fmtk.khlystov.utils.fashionutils.IDateConverter;
import ru.fmtk.khlystov.utils.fashionutils.STDDateConverter;
import ru.fmtk.khlystov.newsgetter.Article;
import ru.fmtk.khlystov.utils.IntentUtils;

import static android.text.TextUtils.isEmpty;

public class NewsDetailesActivity extends AppCompatActivity {

    @NonNull
    private static final String ARTICLE_OBJECT = "NewsDetailesActivity_ARTICLE_OBJECT";

    @Nullable
    private Article article = null;

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void goToArticleLink() {
        if (article != null) {
            String url = article.getUrl();
            if (url != null) {
                IntentUtils.showIntent(this,
                        findViewById(R.id.activity_news_detailes__web_view),
                        IntentUtils.getBrowserIntent(url),
                        getString(R.string.no_browser_error));
            }
        }
    }

    private void fillContent() {
        WebView webView = findViewById(R.id.activity_news_detailes__web_view);
        webView.setWebViewClient(new WebViewClient());
        if (article != null) {
            String url = article.getUrl();
            if (!isEmpty(url)) webView.loadUrl(url);

        }
    }

    @NonNull
    private String dateToString(Date publishedAt) {
        IDateConverter dateConverter = new STDDateConverter(getApplicationContext());
        return dateConverter.convert(publishedAt);
    }
}
