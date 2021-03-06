package ru.fmtk.khlystov.androidnews.newslist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import ru.fmtk.khlystov.androidnews.R;
import ru.fmtk.khlystov.utils.fashionutils.IDateConverter;
import ru.fmtk.khlystov.newsgetter.model.Article;
import ru.fmtk.khlystov.utils.NetworkUtils;

import static android.text.TextUtils.isEmpty;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder> {

    private static final int MAX_IMG_WIDTH = 800;
    private static final int MAX_IMG_HEIGHT = 600;

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
        int layoutId = viewType;
        if (layoutId != R.layout.news_item_even_layout
                && layoutId != R.layout.news_item_odd_layout) {
            layoutId = R.layout.news_item_odd_layout;
        }
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        layoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsRecyclerAdapter.ViewHolder holder, int position) {
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
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(
                new InternalDiffUtilCallback(articles, newArticles));
        this.articles = newArticles;
        diff.dispatchUpdatesTo(this);
    }

    private boolean isOdd(int value) {
        return (value & 1) == 1;
    }

    public interface OnItemClickListener {
        void onItemClick(@NonNull View view, @NonNull Article article);
    }

    private static class InternalDiffUtilCallback extends DiffUtil.Callback {

        @NonNull
        private final List<Article> oldArticles;

        @NonNull
        private final List<Article> newArticles;

        public InternalDiffUtilCallback(@NonNull List<Article> oldArticles, @NonNull List<Article> newArticles) {
            this.oldArticles = oldArticles;
            this.newArticles = newArticles;
        }

        @Override
        public int getOldListSize() {
            return oldArticles.size();
        }

        @Override
        public int getNewListSize() {
            return newArticles.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Article oldArticle = oldArticles.get(oldItemPosition);
            Article newArticle = newArticles.get(newItemPosition);
            if (oldArticle != null && newArticle != null) {
                return oldArticle.hashCode() == newArticle.hashCode();
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Article oldArticle = oldArticles.get(oldItemPosition);
            Article newArticle = newArticles.get(newItemPosition);
            if (oldArticle != null) {
                return oldArticle.equals(newArticle);
            }
            return false;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final TextView subsection;

        @Nullable
        private final TextView author;

        @NonNull
        private final TextView title;

        @NonNull
        private final TextView preview;

        @NonNull
        private final TextView published;

        @NonNull
        private final ImageView image;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            subsection = itemView.findViewById(R.id.news_item_layout__subsection);
            author = itemView.findViewById(R.id.news_item_layout__author);
            title = itemView.findViewById(R.id.news_item_layout__title);
            preview = itemView.findViewById(R.id.news_item_layout__preview);
            published = itemView.findViewById(R.id.news_item_layout__published);
            image = itemView.findViewById(R.id.news_item_layout__image);
        }

        protected void bind(@NonNull Article article,
                            @Nullable OnItemClickListener onItemClickListener,
                            @Nullable IDateConverter dateConverter) {
            String sectionName = article.getSubsection();
            if (!isEmpty(sectionName)) {
                subsection.setText(sectionName);
                subsection.setVisibility(View.VISIBLE);
            } else {
                subsection.setVisibility(View.GONE);
            }
            if (author != null) {
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
                NetworkUtils.getImgToImageView(article.getUrlToImage(),
                        image, MAX_IMG_WIDTH, MAX_IMG_HEIGHT);
            }
            if (onItemClickListener != null) {
                itemView.setOnClickListener(
                        view -> onItemClickListener.onItemClick(itemView, article));
            }
        }
    }
}
