package com.adida.chatapp.search;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.adida.chatapp.R;
import com.adida.chatapp.entities.User;
import com.adida.chatapp.keys.FirebaseKeys;
import com.adida.chatapp.keys.StringKeys;
import com.adida.chatapp.main.MainActivity;
import com.adida.chatapp.sharepref.SharePref;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchResultRowCell extends BaseAdapter implements Filterable {
    private ArrayList<User> userListFull;
    private ArrayList<User> userList;
    Context mContext;
    private boolean[] isBlockList;

    public SearchResultRowCell(Context context, ArrayList<User> user, boolean[] isBlockList) {
        mContext = context;
        userList = user;
        userListFull = new ArrayList<User>(user);
        this.isBlockList = isBlockList;
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

    List<String> blockedIds;
    List<String> addIds;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);


        Log.e("QT", "user:" + user.uuid);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View row = inflater.inflate(R.layout.search_result_row_cell, parent, false);

        TextView name = (TextView) row.findViewById(R.id.userId);
        ImageView icon = (ImageView) row.findViewById(R.id.icon);
        Button btnBlock = row.findViewById(R.id.btnBlock);
        Button btnAdd = row.findViewById(R.id.btnAdd);

        name.setText(user.email);
        icon.setImageResource(R.drawable.main_yellow_hair);
        icon.setScaleType(ImageView.ScaleType.FIT_XY);

        // check is block
        markUserBlocked(row, isBlockList[position]);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapAddButton(user, position, row);
            }
        });


        btnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapBlockButton(btnBlock, user, position, row);
            }
        });
        return (row);
    }

    public void markUserBlocked(View v, boolean isBlock){

        TextView name = v.findViewById(R.id.userId);
        Button btnBlock = v.findViewById(R.id.btnBlock);

        if(isBlock){
            name.setTextColor(Color.RED);
            btnBlock.setText("Unblock");
        }
        else{
            name.setTextColor(Color.BLACK);
            btnBlock.setText("Block");
        }
    }

    private void didTapAddButton(User user, int position, View row){
        if (isBlockList[position] != true) {
            String myId = SharePref.getInstance(mContext).getUuid();
            Log.e("test", "user:" + myId + "click:" + user.uuid);
            ArrayList<String> data = new ArrayList<>();
            FirebaseDatabase.getInstance()
                    .getReference(FirebaseKeys.add)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                                addIds = new ArrayList<>();
                            }
                            // add/remove id to be blocked/unblocked
                            addIds.add(user.uuid);
                            // save to database
                            FirebaseDatabase.getInstance()
                                    .getReference(FirebaseKeys.add)
                                    .child(myId)
                                    .setValue(addIds);
                            userList.remove(position);
                            isBlockList = removeTheElement(isBlockList, position);
                            notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            CharSequence text = "Unblock to add";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(mContext, text, duration);
            toast.show();
        }

    }

    private static boolean[] removeTheElement(boolean[] arr, int index)  {

        // If the array is empty
        // or the index is not in array range
        // return the original array
        if (arr == null  || index < 0  || index >= arr.length) {
            return arr;
        }

        // Create another array of size one less
        boolean[] anotherArray = new boolean[arr.length - 1];

        // Copy the elements except the index
        // from original array to the other array
        for (int i = 0, k = 0; i < arr.length; i++) {

            // if the index is
            // the removal element index
            if (i == index) {
                continue;
            }

            // if the index is not
            // the removal element index
            anotherArray[k++] = arr[i];
        }

        // return the resultant array
        return anotherArray;
    }

    private void didTapBlockButton(Button btnBlock, User user, int position, View row){
        String myId = SharePref.getInstance(mContext).getUuid();
        Log.e("QT", "user:" + myId + "click:" + user.uuid);

        boolean isBlock = btnBlock.getText().toString().equals("Block");
        Log.e("QT", "isBlock: " + isBlock);

        // update database
        FirebaseDatabase.getInstance()
                .getReference(FirebaseKeys.block)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.e("QT", "on data change");

                        blockedIds = null;
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            if (item.getKey().toString().equals(myId)) {
                                Log.e("QT", "found me");

                                blockedIds = (ArrayList<String>) item.getValue();
                                break;
                            }
                        }

                        if(blockedIds == null){
                            Log.e("QT", "empty list");
                            blockedIds = new ArrayList<>();
                        }
                        // add/remove id to be blocked/unblocked
                        if(isBlock){
                            if (!blockedIds.contains(user.uuid)) {
                                blockedIds.add(user.uuid);
                            }
                        }
                        else{
                            blockedIds.remove(user.uuid);
                        }

                        // save to database
                        FirebaseDatabase.getInstance()
                                .getReference(FirebaseKeys.block)
                                .child(myId)
                                .setValue(blockedIds);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("QT", "onCancelled");
                    }
                });
        // check if exist user to be blocked
        // show user
        isBlockList[position] = isBlock;
        markUserBlocked(row, isBlock);
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
