package com.admanager.gifs.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.admanager.gifs.R;
import com.admanager.recyclerview.BaseAdapter;
import com.admanager.recyclerview.BindableViewHolder;
import com.bumptech.glide.Glide;
import com.giphy.sdk.core.models.Media;

public class GifsAdapter extends BaseAdapter<Media, GifsAdapter.ViewHolder> {

    public GifsAdapter(Activity activity) {
        super(activity, R.layout.item_gif);
    }

    @Override
    protected ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    public class ViewHolder extends BindableViewHolder<Media> {
        TextView title;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img);
        }

        @Override
        public void bindTo(Activity activity, final Media model, int position) {
            title.setText(model.getTitle());
            Glide.with(itemView.getContext())
                    .load(model.getImages().getFixedHeightDownsampled().getGifUrl())
                    .into(img);
        }
    }
}
