package ru.fmtk.khlystov.androidnews;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
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
    private List<Article> articles;

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
        if (viewType == R.layout.news_item_even_layout) {
            return new ViewHolderEven(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.news_item_even_layout, parent, false));
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
        return isOdd(position) ? R.layout.news_item_odd_layout : R.layout.news_item_even_layout;
    }

    public void replaceData(@NonNull List<Article> newArticles) {
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffUtil.Callback() {

            @Override
            public int getOldListSize() {
                return articles.size();
            }

            @Override
            public int getNewListSize() {
                return newArticles.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                Article oldArticle = articles.get(oldItemPosition);
                Article newArticle = articles.get(newItemPosition);
                if (oldArticle != null && newArticle != null) {
                    return oldArticle.hashCode() == newArticle.hashCode();
                }
                return false;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Article oldArticle = articles.get(oldItemPosition);
                Article newArticle = articles.get(newItemPosition);
                if (oldArticle != null) {
                    return oldArticle.equals(newArticle);
                }
                return false;
            }
        });
        this.articles = newArticles;
        diff.dispatchUpdatesTo(this);
    }

    private boolean isOdd(int value) {
        return (value & 1) == 1;
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

        @NonNull
        private final TextView source;

        @NonNull
        private final TextView author;

        @NonNull
        private final TextView title;

        @NonNull
        private final TextView preview;

        @NonNull
        private final TextView published;

        @NonNull
        private final ImageView image;

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
            if (!isEmpty(sourceName)) {
                source.setText(sourceName);
            } else {
                author.setText(article.getAuthor());
            }
            title.setText(article.getTitle());
            preview.setText(article.getDescription());
            if (dateConverter != null) {
                Date publishedAt = article.getPublishedAt();
                published.setText(dateConverter.convert(publishedAt));
            }
            if (isEmpty(article.getUrlToImage())) {
                image.setVisibility(View.GONE);
            } else {
                image.setVisibility(View.VISIBLE);
                Picasso.get().load(article.getUrlToImage()).into(image);
            }
            if (onItemClickListener != null) {
                itemView.setOnClickListener(
                        view -> onItemClickListener.onItemClick(itemView, article));
            }
        }
    }

    private static class ViewHolderOdd extends ViewHolder {

        @NonNull
        private final TextView source;

        @NonNull
        private final TextView title;

        @NonNull
        private final TextView preview;

        @NonNull
        private final TextView published;

        @NonNull
        private final ImageView image;

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

        @Override
        protected void bind(@NonNull Article article,
                            @Nullable OnItemClickListener onItemClickListener,
                            @Nullable IDateConverter dateConverter) {
            String sourceName = article.getSourceName();
            source.setText(isEmpty(sourceName) ?
                    source.getContext().getString(R.string.news_recycler_adapter__source_undefined) :
                    sourceName);

            title.setText(article.getTitle());
            preview.setText(article.getDescription());
            if (dateConverter != null) {
                Date publishedAt = article.getPublishedAt();
                published.setText(dateConverter.convert(publishedAt));
            }
            if (isEmpty(article.getUrlToImage())) {
                image.setVisibility(View.GONE);
            } else {
                image.setVisibility(View.VISIBLE);
                Picasso.get().load(article.getUrlToImage()).into(image);
            }
            if (onItemClickListener != null) {
                itemView.setOnClickListener(
                        view -> onItemClickListener.onItemClick(itemView, article));
            }
        }
    }
}
