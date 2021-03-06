package prebuilds.aspn.fuchsialauncher;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Никита on 12.07.2017.
 */
interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());

    }

}

public class appAdapter3 extends RecyclerView.Adapter<appAdapter3.ViewHolder> implements ItemTouchHelperAdapter{
    private Context mContext;
    private ArrayList<App> applist;
    private static String[] colors = {"#F44336","#E91E63","#9C27B0","#673AB7","#3F51B5","#2196F3","#03A9F4","#00BCD4","#009688","#4CAF50","#8BC34A","#CDDC39","#FFEB3B","#FFC107","#FF9800","#FF5722","#795548"};

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(applist, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(applist, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        ActivityManager am = (ActivityManager) mContext.getSystemService( ACTIVITY_SERVICE );
        am.killBackgroundProcesses(applist.get(position).name.toString());
        applist.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // наш пункт состоит только из одного TextView
        public TextView mTextView;
        public CircularImageView mImageView;
        public ConstraintLayout mCard;
        public String pkgName;
        public LinearLayout header;
        public String color;
        public ViewHolder(View v) {
            super(v);
            color = colors[new Random().nextInt(appAdapter3.colors.length)];
            mImageView = (CircularImageView) v.findViewById(R.id.icon);
            mImageView.setBorderColor(Color.parseColor(color));
            mTextView = (TextView) v.findViewById(R.id.name);
            header = (LinearLayout)v.findViewById(R.id.header);
            header.setBackgroundColor(Color.parseColor(color));
            mCard = (ConstraintLayout) v.findViewById(R.id.card);
        }
    }

    public appAdapter3(Context c, ArrayList<App> capp) {
        mContext = c;
        applist = capp;
    }
    public void add(App app)
    {
        applist.add(app);
        notifyItemInserted(applist.size()-1);
    }
    public void remove(App app)
    {
        int pos = applist.indexOf(app);
        applist.remove(pos);
        notifyItemRemoved(pos);
    }
    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public appAdapter3.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(mContext).inflate(R.layout.launched_app_card, parent, false);

        // тут можно программно менять атрибуты лэйаута (size, margins, paddings и др.)
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTextView.setText(applist.get(position).label);
        holder.mImageView.setImageDrawable(applist.get(position).icon);
        holder.pkgName=applist.get(position).name.toString();
        holder.mCard.setOnClickListener(new View.OnClickListener() {
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
