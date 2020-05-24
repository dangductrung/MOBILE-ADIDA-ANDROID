package com.adida.chatapp.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.adida.chatapp.R;
import com.adida.chatapp.main.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    MainActivity main;
    Context context = null;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Button btnEditButtonText, btnStatistic, btnSignout;
    ImageButton btnEditButtonImage;
    EditText txtProfileName, txtProfilePhone;

    String profileName, profilePhone;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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

//        Button btnStatis = (Button) getView().findViewById(R.id.btnStatistic);
//
//        final Intent  intent = new Intent(getView().getContext(), StatisticPage.class);
//
//        btnStatis.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(intent);
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_profile, container, false);
        txtProfileName = (EditText) layout.findViewById(R.id.txtProfileName);
        txtProfilePhone = (EditText) layout.findViewById(R.id.txtProfilePhone);

        btnEditButtonImage = (ImageButton) layout.findViewById(R.id.btnEditButtonImage);
        btnEditButtonText = (Button) layout.findViewById(R.id.btnEditButtonText);
        btnStatistic = (Button) layout.findViewById(R.id.btnStatistic);
        btnSignout = (Button) layout.findViewById(R.id.btnSignout);

        btnEditButtonImage.setVisibility(View.VISIBLE);
        btnEditButtonText.setVisibility(View.INVISIBLE);

        addAction();

        return layout;
    }

    private void addAction(){
        btnEditButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapEditButton();
            }
        });
        btnEditButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapDoneButton();
            }
        });
    }

    private void didTapEditButton(){
        btnEditButtonImage.setVisibility(View.INVISIBLE);
        btnEditButtonText.setVisibility(View.VISIBLE);
        btnStatistic.setVisibility(View.INVISIBLE);
        btnSignout.setVisibility(View.INVISIBLE);

        txtProfileName.setEnabled(true);
        txtProfilePhone.setEnabled(true);

        profileName = txtProfileName.getText().toString();
        profilePhone = txtProfilePhone.getText().toString();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
        navBar.setVisibility(View.INVISIBLE);
    }

    private void didTapDoneButton(){
        btnEditButtonText.setVisibility(View.INVISIBLE);
        btnEditButtonImage.setVisibility(View.VISIBLE);
        btnStatistic.setVisibility(View.VISIBLE);
        btnSignout.setVisibility(View.VISIBLE);

        txtProfileName.setEnabled(false);
        txtProfilePhone.setEnabled(false);
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavigationView);
        navBar.setVisibility(View.VISIBLE);

        String tempName = txtProfileName.getText().toString();
        String tempPhone = txtProfilePhone.getText().toString();

        if (tempName == "") {
            txtProfileName.setText(profileName);
        } else {
            txtProfileName.setText(tempName);
        }

        if (tempPhone == "") {
            txtProfilePhone.setText(profilePhone);
        } else {
            txtProfilePhone.setText(tempPhone);
        }

    }
}
