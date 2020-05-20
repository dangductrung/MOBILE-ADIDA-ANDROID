package com.adida.chatapp.search;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.adida.chatapp.R;

import java.util.ArrayList;
import java.util.List;

public class CustomRowCell extends BaseAdapter implements Filterable {
    private ArrayList<User> userListFull;
    private ArrayList<User> userList;
    Context mContext;

    public CustomRowCell(Context context, ArrayList<User> user) {
        mContext = context;
        userList = user;
        userListFull = user;
    }

    @Override
    public User getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View row = inflater.inflate(R.layout.activity_custom_row_cell, parent, false);
        TextView name = (TextView) row.findViewById(R.id.userId);
        ImageView icon = (ImageView) row.findViewById(R.id.icon);
        name.setText(user.name);
        icon.setImageResource(user.imageId);
        icon.setScaleType(ImageView.ScaleType.FIT_XY);

        return (row);
    }

    @Override
    public Filter getFilter() {
        return SearchFilter;
    }

    private Filter SearchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(userListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (User item : userListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            userList.clear();
            userList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
