package com.adida.chatapp.search;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adida.chatapp.R;
import com.adida.chatapp.entities.User;
import com.adida.chatapp.keys.FirebaseKeys;
import com.adida.chatapp.sharepref.SharePref;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomRowCell extends BaseAdapter implements Filterable {
    private ArrayList<User> userListFull;
    private ArrayList<User> userList;
    Context mContext;

    public CustomRowCell(Context context, ArrayList<User> user) {
        mContext = context;
        userList = user;
        userListFull = new ArrayList<User>(user);
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
        ImageButton btnDelete = (ImageButton) row.findViewById(R.id.btnDeleteButton);
        ImageButton btnCall = (ImageButton) row.findViewById(R.id.btnCall);
        name.setText(user.email);
        icon.setImageResource(R.drawable.main_yellow_hair);
        icon.setScaleType(ImageView.ScaleType.FIT_XY);

        btnDelete.setFocusable(false);
        btnDelete.setFocusableInTouchMode(false);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapDeleteIcon(user, position, row);
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Handle video call
            }
        });

        return (row);
    }

    List<String> addIds = null;
    private void didTapDeleteIcon(User user, int position, View row){
        String myId = SharePref.getInstance(mContext).getUuid();
        Log.e("test", "user:" + myId + "click:" + user.uuid);
        ArrayList<String> data = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference(FirebaseKeys.add)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.e("QT", "on data change");

                        addIds = null;
                        for(DataSnapshot item : dataSnapshot.getChildren()){
                            if(item.getKey().toString().equals(myId)){
                                Log.e("QT", "found me");

                                addIds = (ArrayList<String>)item.getValue();
                                break;
                            }
                        }

                        // show user
                        // check if exist user to be blocked
                        if(addIds == null){
                            Log.e("QT", "empty list");
                            addIds = new ArrayList<>();
                        }
                        // add/remove id to be blocked/unblocked
                        addIds.remove(user.uuid);
                        // save to database
                        FirebaseDatabase.getInstance()
                                .getReference(FirebaseKeys.add)
                                .child(myId)
                                .setValue(addIds);

                        userList.remove(position);
                        notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("QT", "onCancelled");
                    }
                });
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
                    if (item.email.toLowerCase().contains(filterPattern)) {
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
