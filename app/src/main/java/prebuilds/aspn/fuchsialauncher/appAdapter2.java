package prebuilds.aspn.fuchsialauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Никита on 10.07.2017.
 */

public class appAdapter2 extends RecyclerView.Adapter<appAdapter2.ViewHolder> {
    private Context mContext;
    private ArrayList<App> applist;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // наш пункт состоит только из одного TextView
        public TextView mTextView;
        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            mImageView=(ImageView) v.findViewById(R.id.image);
            mTextView = (TextView) v.findViewById(R.id.name);
        }
    }

    public appAdapter2(Context c, ArrayList<App> capp) {
        mContext = c;
        applist = capp;
    }

    @Override
    public appAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(mContext).inflate(R.layout.app_icon, parent, false);

        // тут можно программно менять атрибуты лэйаута (size, margins, paddings и др.)
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTextView.setText(applist.get(position).label);
        holder.mImageView.setImageDrawable(applist.get(position).icon);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = mContext.getPackageManager().getLaunchIntentForPackage(applist.get(position).name.toString());
                mContext.startActivity(i);
            }
        });
    }
    @Override
    public int getItemCount() {
        return applist.size();
    }
}
