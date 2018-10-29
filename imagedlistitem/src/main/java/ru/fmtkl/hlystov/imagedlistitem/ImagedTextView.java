package ru.fmtkl.hlystov.imagedlistitem;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.text.TextUtils.isEmpty;

public class ImagedTextView extends LinearLayout {

    @Nullable
    private String text = "";
    @Nullable
    private Drawable image = null;
    private int imageToTextMargin = 0;

    public ImagedTextView(@NonNull Context context) {
        this(context, null, 0, 0);
    }

    public ImagedTextView(@NonNull Context context, @NonNull AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public ImagedTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleResource) {
        super(context, attrs, defStyleAttr, defStyleResource);
        TypedArray typedArray = null;
        try {
            text = "";
            typedArray = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.ImagedTextView,
                    defStyleAttr,
                    defStyleAttr);
            if (typedArray != null) {
                image = typedArray.getDrawable(R.styleable.ImagedTextView_src);
                imageToTextMargin = typedArray.getDimensionPixelOffset(R.styleable.ImagedTextView_imageToTextMargin, 0);
                text = typedArray.getString(R.styleable.ImagedTextView_text);
                init();
            }
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }

    }

    private void init() {
        View rootView = inflate(getContext(), R.layout.main_layout, this);
        TextView textView = rootView.findViewById(R.id.layout_imaged_text__text);
        textView.setText(text);
        if (isEmpty(text)) {
            textView.setVisibility(INVISIBLE);
        } else {
            textView.setVisibility(VISIBLE);
        }

        ImageView imageView = rootView.findViewById(R.id.layout_imaged_text__image);
        if (imageView.getLayoutParams() instanceof MarginLayoutParams) {
            MarginLayoutParams marginLayoutParams =
                    (MarginLayoutParams) imageView.getLayoutParams();
            marginLayoutParams.setMarginEnd(imageToTextMargin);
            imageView.requestLayout();
        }
        imageView.setImageDrawable(image);
        if (image == null) {
            imageView.setVisibility(GONE);
        } else {
            imageView.setVisibility(VISIBLE);
        }
    }
}
