package com.adida.chatapp.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.adida.chatapp.R;
import com.adida.chatapp.entities.User;
import com.adida.chatapp.keys.FirebaseKeys;
import com.adida.chatapp.main.MainActivity;
import com.adida.chatapp.sharepref.SharePref;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SearchFragment extends Fragment{
    MainActivity main;
    Context context = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private SearchResultRowCell customRowCell;
    SearchView srcSearchView;
    ListView listSearchView;
    ProgressDialog progressDialog;
    private ArrayList<User> data = new ArrayList<User>();

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        try {
            context = getActivity(); // use this reference to invoke main callbacks
            main = (MainActivity) getActivity();
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_search, null);

        getUserList();

        listSearchView = (ListView) layout.findViewById(R.id.listSearchView);
        srcSearchView = (SearchView) layout.findViewById(R.id.srcSearchView);


        srcSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customRowCell.getFilter().filter(newText);
                return false;
            }
        });

        return layout;
    }


    List<String> blockedIds = null;
    boolean[] isBlockList = null;
    List<String> addIds = null;
    boolean[] isAddList = null;
    private void getUserList() {
        progressDialog = ProgressDialog.show(context, "","Loading...");
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.state).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                HashMap<String, Boolean> state = (HashMap<String, Boolean>) dataSnapshot.getValue();
                for (Iterator<String> it = state.keySet().iterator(); it.hasNext(); ) {
                    String uuid = it.next();
                    if (state.get(uuid).booleanValue() == true) {
                        FirebaseDatabase.getInstance().getReference(FirebaseKeys.profile).child(uuid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                if (!user.uuid.equals(SharePref.getInstance(context).getUuid())) {
                                    data.add(user);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                // data is now a list of active users
                // get list of blocked users
                FirebaseDatabase.getInstance()
                        .getReference(FirebaseKeys.block)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.e("QT SearchFragment", "on data change");

                                // get blocked list
                                String myId = SharePref.getInstance(context).getUuid();
                                blockedIds = null;
                                for(DataSnapshot item : dataSnapshot.getChildren()){
                                    if(item.getKey().toString().equals(myId)){
                                        Log.e("QT SearchFragment", "found me");

                                        blockedIds = (ArrayList<String>)item.getValue();
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("QT SearchFragment", "onCancelled");
                            }
                        });


                // Add list
                FirebaseDatabase.getInstance()
                        .getReference(FirebaseKeys.add)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.e("QT SearchFragment", "on data change");

                                // get add list
                                String myId = SharePref.getInstance(context).getUuid();
                                addIds = null;
                                for(DataSnapshot item : dataSnapshot.getChildren()){
                                    if(item.getKey().toString().equals(myId)){
                                        Log.e("QT SearchFragment", "found me");

                                        addIds = (ArrayList<String>)item.getValue();
                                        break;
                                    }
                                }

                                ArrayList<User> dataToSearch = new ArrayList<User>();
                                for (int i = 0;i< data.size(); i++){
                                    if (addIds != null) {
                                        if (!addIds.contains(data.get(i).uuid)) {
                                            dataToSearch.add(data.get(i));
                                        }
                                    } else {
                                        dataToSearch.add(data.get(i));
                                    }
                                }

                                isBlockList = new boolean[dataToSearch.size()];
                                isAddList = new boolean[dataToSearch.size()];
                                if(blockedIds != null) {
                                    // loop all active users
                                    for(int i = 0; i < dataToSearch.size(); i++){
                                        // if active user is in blocked list
                                        if(blockedIds.contains(dataToSearch.get(i).uuid)){
                                            isBlockList[i] = true;
                                        }
                                    }
                                }
                                customRowCell = new SearchResultRowCell(context, dataToSearch, isBlockList, isAddList);
                                listSearchView.setAdapter(customRowCell);
                                customRowCell.notifyDataSetChanged();
                                progressDialog.dismiss();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("QT SearchFragment", "onCancelled");
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });}
}
