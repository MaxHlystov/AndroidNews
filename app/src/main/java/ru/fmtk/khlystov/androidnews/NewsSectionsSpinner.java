package ru.fmtk.khlystov.androidnews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class NewsSectionsSpinner {

    @NonNull
    private final Spinner spinner;

    @NonNull
    private final String[] sections;

    @NonNull
    private final ArrayAdapter<String> adapter;

    private int currentPosition = -1;
    private boolean canProcessSelection = false;

    public NewsSectionsSpinner(@NonNull Context context, @NonNull Spinner spinner,
                               @NonNull String[] sections, @NonNull String currentValue,
                               @Nullable OnSelectedItemChanged onSelectedItemChangedListener) {
        this.spinner = spinner;
        this.sections = sections;
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
                        String section = getCurrentSection();
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
    public String getCurrentSection() {
        if (currentPosition < 0 || currentPosition >= sections.length) return null;
        return sections[currentPosition];
    }

    public void setCurrentSection(@NonNull String newsSection) {
        for (int position = 0; position < adapter.getCount(); position++) {
            if (newsSection.equals(adapter.getItem(position))) {
                spinner.setSelection(position);
                currentPosition = position;
                break;
            }
        }
    }

    public interface OnSelectedItemChanged {
        void process(@NonNull String section);
    }
}
