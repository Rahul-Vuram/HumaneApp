package vuram_test_2.vuram.com.vuram_test_2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import vuram_test_2.vuram.com.vuram_test_2.util.Connectivity;

public class UserProfileActivity extends AppCompatActivity {

    CircleImageView editImageButton;
    RecyclerView orgListView;
    ScrollView scrollView;
    Toolbar toolbar;

    GPSTracker gps;
    Button addOrgButton, submitOrgFormButton, goToMap;
    EditText orgRegNoEditText, orgNameEditText, addressEditText, emailEditText, phoneEditText, orgDescEditText;
    Spinner orgTypeSpinner;
    Gson gson;
    String mapAddress;
    OrganisationDetails orgDetails;


    Random randomNumberGenerator = null;
    ArrayList<OrgDetails> orgDetailsList = null;
    int orgCount = 0;
    public final String TAG = "UserProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar_user_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorTextIcons), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        scrollView = ((ScrollView) findViewById(R.id.scrollview_user_profile));
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
        orgListView = (RecyclerView) findViewById(R.id.org_details_recyclerview_user_profile);
        editImageButton = (CircleImageView) findViewById(R.id.edit_imagebutton_user_profile);
        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfileActivity.this, EditProfileActivity.class));
            }
        });

        orgDetailsList = new ArrayList<OrgDetails>();
        randomNumberGenerator = new Random();
        orgCount = randomNumberGenerator.nextInt(2) + 2;
        Log.d(TAG, "onCreate: Org Count " + orgCount);
        for (int i = 0; i < orgCount; i++) {
            OrgDetails orgDetails = new OrgDetails();
            orgDetails.orgRegNo = "CBE12345678";
            orgDetails.orgName = "Aalayam Education And Charitable Trust";
            orgDetails.orgLogo = "www.aalayam.com/img";
            orgDetailsList.add(orgDetails);
        }
        Log.d(TAG, "onCreate: Org List Size " + orgDetailsList.size());
        OrgListAdapter orgListAdapter = new OrgListAdapter(this, orgDetailsList);
        orgListView.setAdapter(orgListAdapter);
        orgListView.setLayoutManager(new LinearLayoutManager(this));

        // Org Registration Form
        orgRegNoEditText = (EditText) findViewById(R.id.org_register_num_editText_org_form);
        orgNameEditText = (EditText) findViewById(R.id.org_name_editText_org_form);
        addressEditText = (EditText) findViewById(R.id.org_address_editText_org_form);
        emailEditText = (EditText) findViewById(R.id.org_email_editText_org_form);
        phoneEditText = (EditText) findViewById(R.id.org_phone_editText_org_form);
        orgTypeSpinner = (Spinner) findViewById(R.id.org_type_spinner_org_form);
        orgDescEditText = (EditText) findViewById(R.id.org_desc_editText_org_form);

        addOrgButton = (Button) findViewById(R.id.add_org_button_edit_profile);
        addOrgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addOrgButton.getText().toString().equals("Add Organisation")) {
                    addOrgButton.setText("Cancel");
                    View orgFormView = findViewById(R.id.org_form);
                    orgFormView.setVisibility(View.VISIBLE);
                } else {
                    addOrgButton.setText("Add Organisation");
                    View orgFormView = findViewById(R.id.org_form);
                    orgFormView.setVisibility(View.GONE);
                }
            }
        });
        submitOrgFormButton = (Button) findViewById(R.id.submit_button_org_form);
        submitOrgFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orgRegNo, orgName, address, email, phone, orgType, orgDesc;
                orgRegNo = orgRegNoEditText.getText().toString();
                orgName = orgNameEditText.getText().toString();
                address = addressEditText.getText().toString();
                email = emailEditText.getText().toString();
                phone = phoneEditText.getText().toString();
                orgType = orgTypeSpinner.getSelectedItem().toString();
                orgDesc = orgDescEditText.getText().toString();

                orgDetails = new OrganisationDetails();
                orgDetails.setOrg_reg_no(orgRegNo);
                orgDetails.setOrg_name(orgName);
                orgDetails.setLatitude((int)MapsActivity.latitude);
                orgDetails.setLongitude((int)MapsActivity.longitude);
                if (mapAddress != null) {
                    if (!mapAddress.isEmpty()) {
                        address = mapAddress;
                    }
                }
                orgDetails.setAddress(address);
                orgDetails.setEmail(email);
                orgDetails.setMobile(phone);
                orgDetails.setOrg_type(orgType);
                orgDetails.setDescription(orgDesc);

                new RegisterOrgTask().execute();
            }
        });

        goToMap= (Button) findViewById(R.id.map);
        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(UserProfileActivity.this);

                // check if GPS enabled
                if(gps.canGetLocation()){
                    if(isNetworkAvailable()) {

                        Intent intent = new Intent(UserProfileActivity.this, MapsActivity.class);
                        startActivityForResult(intent, 2);
                    }
                    else
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserProfileActivity.this);
                        alertDialog.setTitle("Internet settings");
                        alertDialog.setMessage("Mobile data is not enabled. Do you want to go to settings menu?");

                        // On pressing Settings button
                        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(intent);
                            }
                        });
                        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        // Showing Alert Message
                        alertDialog.show();
                    }
                }else{
                    gps.showSettingsAlert();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2)
        {
            mapAddress = data.getStringExtra("ADDRESS");
            addressEditText.setText(mapAddress);
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class RegisterOrgTask extends AsyncTask {

        HttpClient client;
        HttpResponse response;

        @Override
        protected Object doInBackground(Object[] params) {
            client = new DefaultHttpClient();
            gson = new Gson();
            String jsonString = gson.toJson(orgDetails).toString();
            Log.d(TAG, "doInBackground: JSON Object: " + jsonString);
            String token = Connectivity.getAuthToken(UserProfileActivity.this, Connectivity.Donor_Token);
            response = Connectivity.makePostRequest(RestAPIURL.registerOrg, jsonString, client, token);
            if (response != null) {
                Log.d("Response Code",response.getStatusLine().getStatusCode()+"");

                try {
                    Connectivity.getJosnFromResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("Response","Null");
            }
            return null;
        }
    }
}
