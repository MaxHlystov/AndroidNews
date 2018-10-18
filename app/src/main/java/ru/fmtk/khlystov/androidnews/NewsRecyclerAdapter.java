package ru.fmtk.khlystov.androidnews;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import ru.fmtk.khlystov.androidnews.fashionutils.IDateConverter;
import ru.fmtk.khlystov.newsgetter.Article;

import static android.text.TextUtils.isEmpty;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder> {

    @NonNull
    private final IDateConverter dateConverter;

    @NonNull
    private final List<Article> articles;

    @Nullable
    private final OnItemClickListener onClickListener;

    public NewsRecyclerAdapter(@NonNull List<Article> articles,
                               @NonNull IDateConverter dateConverter,
                               @Nullable OnItemClickListener onClickListener) {
        this.articles = articles;
        this.onClickListener = onClickListener;
        this.dateConverter = dateConverter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return ViewHolderEven.create(parent);
        }
        return ViewHolderOdd.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(articles.get(position), onClickListener, dateConverter);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position & 1;
    }

    public interface OnItemClickListener {
        void onItemClick(@NonNull View view, @NonNull Article article);
    }

    protected abstract static class ViewHolder extends RecyclerView.ViewHolder {

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        protected abstract void bind(@NonNull Article article,
                                     @Nullable OnItemClickListener onItemClickListener,
                                     @Nullable IDateConverter dateConverter);
    }

    private static class ViewHolderEven extends ViewHolder {

        @Nullable
        protected final TextView source;

        @Nullable
        protected final TextView author;

        @Nullable
        protected final TextView title;

        @Nullable
        protected final TextView preview;

        @Nullable
        protected final TextView published;

        @Nullable
        protected final ImageView image;

        @NonNull
        private static ViewHolder create(@NonNull ViewGroup parent) {
            return new ViewHolderEven(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.news_item_even_layout, parent, false));
        }

        private ViewHolderEven(@NonNull View itemView) {
            super(itemView);
            source = itemView.findViewById(R.id.news_item_layout__source);
            author = itemView.findViewById(R.id.news_item_layout__author);
            title = itemView.findViewById(R.id.news_item_layout__title);
            preview = itemView.findViewById(R.id.news_item_layout__preview);
            published = itemView.findViewById(R.id.news_item_layout__published);
            image = itemView.findViewById(R.id.news_item_layout__image);
        }

        @Override
        protected void bind(@NonNull Article article,
                            @Nullable OnItemClickListener onItemClickListener,
                            @Nullable IDateConverter dateConverter) {
            String sourceName = article.getSourceName();
            if (!isEmpty(sourceName) && source != null) {
                source.setText(sourceName);
            } else if (author != null) {
                author.setText(article.getAuthor());
            }
            if (title != null) {
                title.setText(article.getTitle());
            }
            if (preview != null) {
                preview.setText(article.getDescription());
            }
            if (published != null && dateConverter != null) {
                Date publishedAt = article.getPublishedAt();
                published.setText(dateConverter.convert(publishedAt));
            }
            if (image != null) {
                if (isEmpty(article.getUrlToImage())) {
                    image.setVisibility(View.GONE);
                } else {
                    image.setVisibility(View.VISIBLE);
                    Picasso.get().load(article.getUrlToImage()).into(image);
                }
            }
            if (onItemClickListener != null) {
                itemView.setOnClickListener(
                        view -> onItemClickListener.onItemClick(itemView, article));
            }
        }
    }

    private static class ViewHolderOdd extends ViewHolder {


        @Nullable
        protected final TextView source;

        @Nullable
        protected final TextView title;

        @Nullable
        protected final TextView preview;

        @Nullable
        protected final TextView published;

        @Nullable
        protected final ImageView image;

        @NonNull
        private static ViewHolder create(@NonNull ViewGroup parent) {
            return new ViewHolderOdd(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.news_item_odd_layout, parent, false));
        }

        private ViewHolderOdd(@NonNull View itemView) {
            super(itemView);
            source = itemView.findViewById(R.id.news_item_layout__source);
            title = itemView.findViewById(R.id.news_item_layout__title);
            preview = itemView.findViewById(R.id.news_item_layout__preview);
            published = itemView.findViewById(R.id.news_item_layout__published);
            image = itemView.findViewById(R.id.news_item_layout__image);
        }

        protected void bind(@NonNull Article article,
                            @Nullable OnItemClickListener onItemClickListener,
                            @Nullable IDateConverter dateConverter) {
            String sourceName = article.getSourceName();
            if (source != null) {
                source.setText(isEmpty(sourceName) ?
                        source.getContext().getString(R.string.news_recycler_adapter__source_undefined) :
                        sourceName);
            }

            if (title != null) {
                title.setText(article.getTitle());
            }
            if (preview != null) {
                preview.setText(article.getDescription());
            }
            if (published != null && dateConverter != null) {
                Date publishedAt = article.getPublishedAt();
                published.setText(dateConverter.convert(publishedAt));
            }
            if (image != null) {
                if (isEmpty(article.getUrlToImage())) {
                    image.setVisibility(View.GONE);
                } else {
                    image.setVisibility(View.VISIBLE);
                    Picasso.get().load(article.getUrlToImage()).into(image);
                }
            }
            if (onItemClickListener != null) {
                itemView.setOnClickListener(
                        view -> onItemClickListener.onItemClick(itemView, article));
            }
        }
    }
}
