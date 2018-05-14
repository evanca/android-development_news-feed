package com.example.android.newsstagei;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static com.example.android.newsstagei.R.layout.recyclerview_item;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private List<News> mNewsListExtracted;

    // Setting on click listener (RecyclerView) step 1
    private View.OnClickListener mClickListener;

    public NewsAdapter(List<News> newsListExtracted) {
        this.mNewsListExtracted = newsListExtracted;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(recyclerview_item, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        // Setting on click listener (RecyclerView)
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onClick(view);

            }
        });

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView textViewWebtitle = holder.textViewWebtitle;
        TextView textViewSectionname = holder.textViewSectionname;
        TextView textViewWebpublicationdate = holder.textViewWebpublicationdate;
        ImageView thumbnailImage = holder.thumbnailImage;

        String contributor = new String("");
        if (mNewsListExtracted.get(listPosition).getContributor() != null) {
            contributor = " / " + mNewsListExtracted.get(listPosition).getContributor();
        }

        textViewWebtitle.setText(mNewsListExtracted.get(listPosition).getWebTitle());
        textViewSectionname.setText(mNewsListExtracted.get(listPosition).getSectionName());
        textViewWebpublicationdate.setText(mNewsListExtracted.get(listPosition).getWebPublicationDate().substring(0, 10) + contributor);

        if (!(mNewsListExtracted.get(listPosition).getBitmap() == null)) {
            thumbnailImage.setImageBitmap(mNewsListExtracted.get(listPosition).getBitmap());
        } else thumbnailImage.setImageResource(R.drawable.newspaper);

        // Making random (based on a title length) cells colorful;
        Boolean isNext = mNewsListExtracted.get(listPosition).getNextIsBigger();

        if (isNext) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.random_news));
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Convert the String URL into a URI object
                Uri newsUri = Uri.parse(mNewsListExtracted.get(listPosition).getUrl());

                // Create a new intent to view the news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                Context context = v.getContext();
                context.startActivity(websiteIntent);

            }
        });

    }

    @Override
    public int getItemCount() {
        if (mNewsListExtracted != null) {
            return mNewsListExtracted.size();
        } else {
            return 0;
        }
    }

    public void setNewsList(List<News> list) {
        mNewsListExtracted = list;
        this.notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewWebtitle;
        TextView textViewSectionname;
        TextView textViewWebpublicationdate;
        ImageView thumbnailImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.textViewWebtitle = (TextView) itemView.findViewById(R.id.webtitle_view);
            this.textViewSectionname = (TextView) itemView.findViewById(R.id.sectionname_view);
            this.textViewWebpublicationdate = (TextView) itemView.findViewById(R.id.webpublicationdate_view);
            this.thumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnail_image);
        }
    }

}


