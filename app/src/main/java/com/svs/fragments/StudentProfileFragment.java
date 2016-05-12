package com.svs.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.svs.R;
import com.svs.SessionManager;

import java.util.HashMap;


/**
 * Created by Ravi on 29/07/15.
 */
public class StudentProfileFragment extends Fragment {

    TextView txtname, txtenroll, txtdob,txtmobile, txtemail, txtaddress;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails;
    public StudentProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_student, container, false);

        txtname = (TextView) v.findViewById(R.id.txtname);
        txtenroll = (TextView) v.findViewById(R.id.txtenroll);
        txtdob = (TextView) v.findViewById(R.id.txtdob);
        txtemail = (TextView) v.findViewById(R.id.txtemail);
        txtmobile= (TextView) v.findViewById(R.id.txtmobile);
        txtaddress = (TextView) v.findViewById(R.id.txtaddress);
        sessionmanager = new SessionManager(getActivity());
        userDetails = new HashMap<String, String>();
        userDetails = sessionmanager.getUserDetails();
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtname.setText(userDetails.get(SessionManager.KEY_NAME) +" "+userDetails.get(SessionManager.KEY_LNAME));
        txtmobile.setText(userDetails.get(SessionManager.KEY_MOBILE));
        txtenroll.setText(userDetails.get(SessionManager.KEY_HALL));
        txtemail.setText(userDetails.get(SessionManager.KEY_EMAIL));
        txtaddress.setText(userDetails.get(SessionManager.KEY_ADD));
        //txtname.setText(userDetails.get(SessionManager.KEY_NAME));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
