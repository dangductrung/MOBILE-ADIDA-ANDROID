package com.adida.chatapp.search;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;

import com.adida.chatapp.R;
import com.adida.chatapp.main.MainActivity;

import java.util.ArrayList;


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

    private ArrayList<User> data = new ArrayList<User>();
    private ArrayList<User> dataFull = new ArrayList<User>();

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

        createData();

        listSearchView = (ListView) layout.findViewById(R.id.listSearchView);
        srcSearchView = (SearchView) layout.findViewById(R.id.srcSearchView);
        customRowCell = new CustomRowCell(context, data);

        listSearchView.setAdapter(customRowCell);

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

    private void createData(){
        String[] name = {"A", "B", "C", "D"};
        String[] id = {"#01","#02","#03","#04"};
        int[] image = {R.drawable.main_yellow_hair, R.drawable.main_yellow_hair, R.drawable.main_yellow_hair, R.drawable.main_yellow_hair};

        for(int i =0 ; i<name.length ; ++i) {
            data.add(new User(id[i], name[i], image[i]));
        }
    }
}
