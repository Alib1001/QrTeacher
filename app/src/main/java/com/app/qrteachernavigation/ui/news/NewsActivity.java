package com.app.qrteachernavigation.ui.news;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.app.qrteachernavigation.R;
import com.app.qrteachernavigation.models.News;

import androidx.appcompat.widget.Toolbar;

public class NewsActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView contentTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        titleTextView = findViewById(R.id.detail_content_text_view);
        contentTextView = findViewById(R.id.detail_content_text_view);

        News news = (News) getIntent().getSerializableExtra("news");

        if (news != null) {
            setTitle(news.getTitle());
            titleTextView.setText(news.getTitle());
            contentTextView.setText(news.getContent());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
