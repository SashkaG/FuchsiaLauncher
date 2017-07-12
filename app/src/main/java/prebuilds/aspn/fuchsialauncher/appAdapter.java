package prebuilds.aspn.fuchsialauncher;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Никита on 10.07.2017.
 */

public class appAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private ArrayList<App> applist;
    private ArrayList<App> originalApps;
    private ItemFilter mFilter;
    public appAdapter(Context c, ArrayList<App> capp) {
        mContext = c;
        applist = capp;
        originalApps=capp;
        mFilter = new ItemFilter();
    }

    public int getCount() {
        return applist.size();
    }

    public Object getItem(int position) {
        return applist.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.app_icon, null);
            holder.appIcon = (ImageView)convertView.findViewById(R.id.image);
            holder.appName = (TextView)convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.appName.setText(applist.get(position).label);
        holder.appIcon.setImageDrawable(applist.get(position).icon);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class ViewHolder{
        TextView appName;
        ImageView appIcon;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<App> list = originalApps;

            int count = list.size();
            final ArrayList<App> nlist = new ArrayList<App>(count);

            App filterableApp;

            for (int i = 0; i < count; i++) {
                filterableApp = list.get(i);
                if (filterableApp.label.toString().toLowerCase().contains(filterString)) {
                    nlist.add(filterableApp);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            applist = (ArrayList<App>) results.values;
            notifyDataSetChanged();
        }

    }
}

