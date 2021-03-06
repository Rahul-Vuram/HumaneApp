package vuram_test_2.vuram.com.vuram_test_2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vuram_test_2.vuram.com.vuram_test_2.util.Connectivity;

public class NeedDetailsActivity extends AppCompatActivity {

    ArrayList<NeedItemDetails> needListData;
    ArrayList<DonationDetails> needCardData;
    ArrayList<DonationDetails> donatedDetailsList;
    ArrayList<NeedDetails> needItemResult;
    List  donatedItemList,itemslist;
    int donatedItemId,needItemId,needQuantity,donatedQuantity,needId;
    String donorName,mainItemName;
    RecyclerView needrecyclerView, receivalCardView;
    NeedListViewAdapter needListViewAdapter;
    NeedReceivalCard needReceivalCard;
    Button showlistButton;
    LinearLayout needListLayout, headingLayout;
    CircularProgressBar progressBar;
    TextView percentage;
    View divider1;
    Toolbar toolbar;
    NeedItemDetails needItemsToDisplay;
    NeedDetails needDetails,need;
    NeedItemDetails needItemDetails;
    //NeedDetails need;
     DonationDetails donationDetailsToDisplay;
     DonatedItemDetails donatedItemDetailsTodisplay;
     DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar_need_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        needDetails = new NeedDetails();
        needItemDetails =  new NeedItemDetails();
        needItemResult = new ArrayList<>();
        donatedDetailsList = new ArrayList<>();
        need = new NeedDetails();
        itemslist = new ArrayList();

     //   needDetails.setNeed_id(8);

         needListData = new ArrayList();
         needCardData = new ArrayList();
        //needViewData();
        //needCardViewData();

        progressBar = (CircularProgressBar) findViewById(R.id.circularProgressBar_ReceivalPage);
        percentage = (TextView) findViewById(R.id.circularProgressPercentageTextVIew_ReceivalPage);

        int animationDuration = 1000; // 2500ms = 2,5s
        progressBar.setProgressWithAnimation(80, animationDuration);
        percentage.setText("80%");

        showlistButton = (Button) findViewById(R.id.showListButton_ReceivalPage);
        needrecyclerView = (RecyclerView) findViewById(R.id.needListRecyclerView_ReceivalPage);
        receivalCardView = (RecyclerView) findViewById(R.id.donorListRecyclerView_ReceivalPage);
        needListLayout = (LinearLayout) findViewById(R.id.needListDivider_ReceivalPage);
        headingLayout = (LinearLayout) findViewById(R.id.listHeadingLinearLayout_ReceivalPage);

        new NeedAndDonatedDetails().execute();

        // Toast.makeText(this,"after caardd adapter",Toast.LENGTH_LONG).show();
    }

    /*   public void needViewData()
       {
           for(int i = 0;i<10;i++) {
               NeedListViewItems needListViewItems = new NeedListViewItems("Clothes", "Male", "20");
               needData.add(needListViewItems);
           }

       }

       public void needCardViewData(){
           for (int j=0;j<6;j++){
               NeedListViewItems items = new NeedListViewItems("Clothes", "Male","20","Akshaya");
               needCardData.add(items);

           }

       }*/
    class NeedAndDonatedDetails extends AsyncTask {
        HttpResponse response;
        HttpClient client;
        NeedListViewItems needListViewItems;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            client = new DefaultHttpClient();
            databaseHelper = new DatabaseHelper(NeedDetailsActivity.this);

            response = Connectivity.makeGetRequest(RestAPIURL.mainItemDetails, client, Connectivity.getAuthToken(NeedDetailsActivity.this, Connectivity.Donor_Token));
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {
                    try {
                        JSONObject jsonObject = new JSONObject(Connectivity.getJosnFromResponse(response));
                        JSONArray results = jsonObject.getJSONArray("results");
                        Gson gson = new Gson();
                     //   Log.d("123", "doInBackground: "+results.toString());
                        needItemResult = gson.fromJson(results.toString(), new TypeToken<List<NeedDetails>>() {
                        }.getType());

                        Log.d("output", needItemResult + "");
                        needId = 1;
                        need = needItemResult.get(needId);

                        //need = needItemResult.get(needItemResult.indexOf(8));

                       // for(int k=0;k<needItemResult.size();k++) {
                          // need = needItemResult.get(need.getNeed_id());
                        Log.d("output for need id 3", need + "");

                        // needId=need.getNeed_id();
                       // }

                        if (need != null) {

                            itemslist = need.getItems();
                            Log.d("ItemsList", "doInBackground: "+itemslist);

                            for (int i = 0; i < itemslist.size(); i++) {
                                NeedItemDetails needItemDetails = (NeedItemDetails) itemslist.get(i);
                                needItemId = needItemDetails.getNeed_item_id();
                                needQuantity = needItemDetails.getQuantity();
                                mainItemName = databaseHelper.getItemNameFromLookUp(needItemId);

                                Log.d("Need Item id", "doInBackground: " + needItemId);
                                Log.d("Need Quantity", "doInBackground: " + needQuantity);
                                Log.d("Main Item Name", "doInBackground: " + mainItemName);


                                needItemsToDisplay = new NeedItemDetails(needItemId, needQuantity);
                                needListData.add(needItemsToDisplay);

                            }


                            donatedDetailsList= (ArrayList<DonationDetails>) need.getDonations();

                            for (int i = 0; i < donatedDetailsList.size(); i++) {
                                DonationDetails donationDetails = donatedDetailsList.get(i);

                                donatedItemList = donationDetails.getDonateditems();
                                 donorName = donationDetails.getUser();
                                Log.d("donor Name", "doInBackground: " + donorName);


                                for (int j = 0; j < donatedItemList.size(); j++) {
                                    DonatedItemDetails donatedItemDetails = (DonatedItemDetails) donatedItemList.get(j);

                                    donatedItemId = donatedItemDetails.getDonated_item_id();
                                    donatedQuantity = donatedItemDetails.getQuantity();

                                    Log.d("donated Item", "doInBackground: " + donatedItemId);
                                    Log.d("donated Quantity", "doInBackground: " + donatedQuantity);
                                }

                                donatedItemDetailsTodisplay = new DonatedItemDetails(donatedItemId,donatedQuantity);
                                donationDetailsToDisplay = new DonationDetails(donatedItemDetailsTodisplay,donorName);
                              //  needListViewItems = new NeedListViewItems(donatedItemId, donorName, donatedQuantity);

                                needCardData.add(donationDetailsToDisplay);
                            }
                        }
                        else
                            Toast.makeText(NeedDetailsActivity.this, "Json Object retreival failed", Toast.LENGTH_SHORT).show();



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    // return null;
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {

            needListViewAdapter = new NeedListViewAdapter(NeedDetailsActivity.this,needListData);
            needrecyclerView.setAdapter(needListViewAdapter);
            needrecyclerView.setLayoutManager(new LinearLayoutManager(NeedDetailsActivity.this));
            needListLayout.setVisibility(View.GONE);
            headingLayout.setVisibility(View.GONE);



            // Toast.makeText(this,"after list adapter",Toast.LENGTH_LONG).show();
            needReceivalCard = new NeedReceivalCard(NeedDetailsActivity.this,needCardData);
            receivalCardView.setAdapter(needReceivalCard);
            receivalCardView.setLayoutManager(new LinearLayoutManager(NeedDetailsActivity.this));

            divider1 = findViewById(R.id.divider1);

            showlistButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (needListLayout.getVisibility() == View.GONE) {
                        needListLayout.setVisibility(View.VISIBLE);
                        headingLayout.setVisibility(View.VISIBLE);
                        divider1.setVisibility(View.VISIBLE);
                        showlistButton.setText("Hide Need List");
                    } else {
                        needListLayout.setVisibility(View.GONE);
                        headingLayout.setVisibility(View.GONE);
                        divider1.setVisibility(View.GONE);
                        showlistButton.setText("Show Need List");
                    }

                }
            });


            Toast.makeText(NeedDetailsActivity.this, "Displayed Succesfully", Toast.LENGTH_SHORT).show();
            super.onPostExecute(o);
        }

    }
}

