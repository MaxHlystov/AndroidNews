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
        text = "";
        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.ImagedText,
                    defStyleAttr,
                    defStyleAttr);
        } catch (Exception ex) {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
        if (typedArray != null) {
            try {
                image = typedArray.getDrawable(R.styleable.ImagedText_src);
                imageToTextMargin = typedArray.getDimensionPixelOffset(R.styleable.ImagedText_imageToTextMargin, 0);
                text = typedArray.getString(R.styleable.ImagedText_text);
            } finally {
                typedArray.recycle();
            }
            init();
        }
    }

    private void init() {
        View rootView = inflate(getContext(), R.layout.main_layout, this);
        TextView textView = rootView.findViewById(R.id.layout_imaged_text__text);
        textView.setText(text);
        if (android.text.TextUtils.isEmpty(text)) {
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
