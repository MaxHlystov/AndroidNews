package ru.fmtk.khlystov.androidnews;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addTextView(this, findViewById(R.id.main),
                getString(R.string.copyright));

        ImageView img_telegram = findViewById(R.id.telega);
        ImageView img_github = findViewById(R.id.github);
        ImageView img_linkedin = findViewById(R.id.linkedin);
        TextView tv_msg = findViewById(R.id.message);
        Button btn_send_mail = findViewById(R.id.btn_send_message);
        TextView tv_stepik = findViewById(R.id.key_info2);

        IConfigValues config = ConfigValues.getConfig();

        img_telegram.setOnClickListener((View img) -> {
            BrowserOpener.openURL(this, config.getURL_Telegram());
        });

        img_github.setOnClickListener((View img) -> {
            BrowserOpener.openURL(this, config.getURL_Github());
        });

        img_linkedin.setOnClickListener((View img) -> {
            BrowserOpener.openURL(this, config.getURL_Linkedin());
        });

        tv_stepik.setOnClickListener((View tv) -> {
            BrowserOpener.openURL(this, config.getURL_Stepik());
        });

        btn_send_mail.setOnClickListener((View tv) -> {
            @Nullable Intent i = getMailIntent(config.getMyEmail(), getString(R.string.greeting), tv_msg.getText().toString());
            if (i != null) {
                startActivity(i);
            } else {
                Snackbar.make(tv, getString(R.string.NoEmail), Snackbar.LENGTH_LONG).show();
            }

        });
    }

    private @Nullable TextView addTextView(@NonNull Activity activity,
                                 @NonNull RelativeLayout parent,
                                 @NonNull CharSequence text) {
        int margin = getResources().getDimensionPixelOffset(R.dimen.half_margin);
        TextView tv = new TextView(activity);
        tv.setText(text);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, // width
                ViewGroup.LayoutParams.WRAP_CONTENT); // height
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.setMargins(margin, margin, margin, margin);
        tv.setLayoutParams(layoutParams);
        parent.addView(tv);
        return tv;
    }

    private @Nullable Intent getMailIntent(@NonNull String to,
                                 @NonNull String subject,
                                 @NonNull String msg) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        String mailto = "mailto:" + to + "?subject=" + subject + "&body=" + msg;
        i.setData(Uri.parse(mailto));
        if (i.resolveActivity(getPackageManager()) != null)
            return i;
        return null;
    }

}
