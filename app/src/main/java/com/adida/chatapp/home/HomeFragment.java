package com.adida.chatapp.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.adida.chatapp.R;
import com.adida.chatapp.chatscreen.DefaultMessagesActivity;
import com.adida.chatapp.entities.User;
import com.adida.chatapp.keys.FirebaseKeys;
import com.adida.chatapp.main.MainActivity;
import com.adida.chatapp.search.CustomRowCell;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    MainActivity main;
    Context context = null;

    private ListView listView;
    private CustomRowCell customRowCell;
    ProgressDialog progressDialog;
    private ArrayList<User> data = new ArrayList<User>();

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity(); // use this reference to invoke main callbacks
            main = (MainActivity) getActivity();
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    private void getUserList() {
        progressDialog = ProgressDialog.show(context, "","Loading...");

        FirebaseDatabase.getInstance().getReference(FirebaseKeys.profile).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, String>> param = (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();
                ArrayList<com.adida.chatapp.entities.User> list = new ArrayList<com.adida.chatapp.entities.User>();
                Set<String> keys = param.keySet();
                for (Iterator<String> it = keys.iterator(); it.hasNext();) {
                    HashMap<String, String> data = param.get(it.next());
                    com.adida.chatapp.entities.User user = new com.adida.chatapp.entities.User();
                    user.email = data.get("email");
                    user.countChatMessage =data.get("countChatMessage");
                    user.countCreateConnection = data.get("countCreateConnection");
                    user.name = data.get("name");
                    user.phone = data.get("phone");
                    user.uuid = data.get("uuid");
                    list.add(user);

                }
                data = list;
                customRowCell = new CustomRowCell(context, data);

                listView.setAdapter(customRowCell);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_home, null);
        getUserList();
        this.listView = (ListView) layout.findViewById(R.id.listContactView);

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position>=0 && data.size() >0){
                    User clickedUser=data.get(position);
                    DefaultMessagesActivity.open(context,clickedUser);
                }
            }
        });

        return layout;
    }
}
