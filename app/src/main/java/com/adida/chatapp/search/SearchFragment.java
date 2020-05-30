package com.adida.chatapp.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    MainActivity main;
    Context context = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private CustomRowCell customRowCell;
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
        getUserData();

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

                                customRowCell = new CustomRowCell(context, data, true);

                                listSearchView.setAdapter(customRowCell);
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });}

    private void getUserData() {
        progressDialog = ProgressDialog.show(context, "", "Loading...");
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.profile).child(SharePref.getInstance(context).getUuid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, String> param = (HashMap<String, String>) dataSnapshot.getValue();

                com.adida.chatapp.entities.User user = new com.adida.chatapp.entities.User();
                user.email = param.get("email");
                user.countChatMessage = param.get("countChatMessage");
                user.countCreateConnection = param.get("countCreateConnection");
                user.name = param.get("name");
                user.phone = param.get("phone");
                user.uuid = param.get("uuid");

                progressDialog.dismiss();
                System.out.println("UIID: ");
                System.out.print(user.uuid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
