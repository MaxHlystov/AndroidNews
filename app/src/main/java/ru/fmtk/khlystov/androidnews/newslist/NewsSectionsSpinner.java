package ru.fmtk.khlystov.androidnews.newslist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import ru.fmtk.khlystov.androidnews.R;
import ru.fmtk.khlystov.newsgetter.NewsSection;

public class NewsSectionsSpinner {

    @NonNull
    private final Spinner spinner;

    @NonNull
    private final ArrayAdapter<NewsSection> adapter;

    @NonNull
    private final NewsSection[] sections = NewsSection.values();

    private int currentPosition = -1;
    private boolean canProcessSelection = false;

    public NewsSectionsSpinner(@NonNull Context context,
                               @NonNull Spinner spinner,
                               @NonNull NewsSection currentValue,
                               @Nullable OnSelectedItemChanged onSelectedItemChangedListener) {
        this.spinner = spinner;
        this.adapter = new ArrayAdapter<>(context,
                R.layout.news_sections_item, sections);
        adapter.setDropDownViewResource(R.layout.news_sections_selected_item);
        spinner.setAdapter(adapter);
        setCurrentSection(currentValue);
        if (onSelectedItemChangedListener != null) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (currentPosition != position && canProcessSelection) {
                        currentPosition = position;
                        NewsSection section = getCurrentSection();
                        if (section != null) {
                            onSelectedItemChangedListener.process(section);
                        }
                    }
                    if (!canProcessSelection) canProcessSelection = true;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }

            });
        }
    }

    @NonNull
    public Spinner getSpinner() {
        return spinner;
    }

    @Nullable
    public NewsSection getCurrentSection() {
        if (currentPosition < 0 || currentPosition >= sections.length) { return null; }
        return sections[currentPosition];
    }

    public void setCurrentSection(@NonNull NewsSection newsSection) {
        for (int position = 0; position < adapter.getCount(); position++) {
            if (newsSection.equals(adapter.getItem(position))) {
                spinner.setSelection(position);
                currentPosition = position;
                break;
            }
        }
    }

    public interface OnSelectedItemChanged {
        void process(@NonNull NewsSection section);
    }
}
