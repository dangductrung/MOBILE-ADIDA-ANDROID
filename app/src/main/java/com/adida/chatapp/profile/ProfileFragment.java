package com.adida.chatapp.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.adida.chatapp.R;
import com.adida.chatapp.entities.User;
import com.adida.chatapp.firebase_manager.FirebaseManager;
import com.adida.chatapp.keys.FirebaseKeys;
import com.adida.chatapp.login.LoginPage;
import com.adida.chatapp.main.MainActivity;
import com.adida.chatapp.report.ReportActivity;
import com.adida.chatapp.sharepref.SharePref;
import com.adida.chatapp.statistic.StatisticPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


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

    Button btnEditButtonText, btnStatistic, btnSignout, btnReport;
    ImageButton btnEditButtonImage;
    EditText txtProfileName, txtProfilePhone;
    ProgressDialog progressDialog;
    TextView email;
    String profileName, profilePhone;
    User currnetUser;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

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
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }


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
        btnReport = (Button) layout.findViewById(R.id.btnReport);

        email = (TextView) layout.findViewById(R.id.userEmail);
        btnEditButtonImage.setVisibility(View.VISIBLE);
        btnEditButtonText.setVisibility(View.INVISIBLE);

        addAction();
        getUserData();

        return layout;
    }

    private void addAction() {
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
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseManager.getInstance().setState(false, context);
                Intent intent = new Intent(getActivity(), LoginPage.class);
                SharePref.getInstance(context).setUuid(null);
                startActivity(intent);
            }
        });
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReportActivity.class);
                startActivity(intent);
            }
        });
        btnStatistic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StatisticPage.class);
                startActivity(intent);
            }
        });
    }

    private void didTapEditButton() {
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

    private void didTapDoneButton() {
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
            currnetUser.name = tempName;
        }

        if (tempPhone == "") {
            txtProfilePhone.setText(profilePhone);
        } else {
            txtProfilePhone.setText(tempPhone);
            currnetUser.phone = tempPhone;
        }
        FirebaseManager.getInstance().updateUser(currnetUser, context);

    }

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

                txtProfileName.setText(user.name);
                txtProfilePhone.setText(user.phone);
                email.setText(user.email);
                progressDialog.dismiss();
                currnetUser = user;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
