package vuram_test_2.vuram.com.vuram_test_2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.fiskur.simpleviewpager.ImageResourceLoader;
import eu.fiskur.simpleviewpager.SimpleViewPager;
import vuram_test_2.vuram.com.vuram_test_2.util.Connectivity;

public class OrgDetailsActivity extends AppCompatActivity  {
    Button bt1;
    ImageButton imageButton;
    static int count=0;;
    ArrayList<MainItemDetails> mainItemDetailsList;
    int needItemId,needQuantity,subItemId,mainItemCode;
    static Context context;
    List itemslist,subItemlist;
    List<NeedItemDetails> items,subItem;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;
    static View.OnClickListener myOnClickListener;
    private static ArrayList<Integer> removedItems;
    ArrayList needitems;
    HttpClient client;
    String OrgName,Address,EmailId,Mobile;
    NeedDetails needDetails;
    NeedItemDetails needItemDetails;
    NeedDetails need;
    String mainitemname;
    MainItemDetails mainItemDetails;
    TextView Organisationname,Organisationemail,Organisationmobile,Organisationaddress;
    int needid;
    OrganisationDetails orgdetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_details);
        imageButton=(ImageButton)findViewById(R.id.back_home);
        Organisationname=(TextView)findViewById(R.id.adapter1);
        Organisationmobile=(TextView)findViewById(R.id.adapter5);
        Organisationaddress=(TextView)findViewById(R.id.address1);
        Organisationemail=(TextView)findViewById(R.id.adapter3);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        context = getBaseContext();
        new GetParticularNeedDetails().execute();
        needDetails = new NeedDetails();
        needItemDetails =  new NeedItemDetails();
        bt1 = (Button) findViewById(R.id.donate_donor_org);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (view == bt1)

                {
                    Intent intent = new Intent(OrgDetailsActivity.this, DetailsOfQuantitySelected.class);
                    startActivity(intent);
                }
            }
        });
        final SimpleViewPager simpleViewPager = (SimpleViewPager) findViewById(R.id.simple_view_pager_donor_org);
        int[] resourceIds = new int[]{
                R.drawable.ngo,
                R.drawable.ngo,
                R.drawable.ngo,
                R.drawable.ngo };

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_donor_org);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModel>();
        for (int i = 0; i < MyData.nameArray.length; i++) {
            data.add(new DataModel(
                    MyData.drawableArray[i],
                    MyData.nameArray[i],
                    MyData.requestarray[i],
                    MyData.donatedarray[i],
                    MyData.id_[i]

            ));
        }

        removedItems = new ArrayList<Integer>();
        myOnClickListener = new MyOnClickListener(this);
        adapter = new ItemDetailsAdapter(data);
        recyclerView.setAdapter(adapter);


        simpleViewPager.setImageIds(resourceIds, new ImageResourceLoader() {
            @Override
            public void loadImageResource(ImageView imageView, int i) {
                imageView.setImageResource(i);
            }
        });

        int indicatorColor = Color.parseColor("#ffffff");
        int selectedIndicatorColor = Color.parseColor("#8BC34A");
        simpleViewPager.showIndicator(indicatorColor, selectedIndicatorColor);
    }



    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;
        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.layout_select_quantity);
            dialog.setTitle("Enter The Quantity");


            final TextView value=(TextView)dialog.findViewById(R.id.number_custom);

            Button increment=(Button) dialog.findViewById(R.id.increment_custom);
            Button decrement=(Button) dialog.findViewById(R.id.decrement_custom);
            increment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count++;
                    value.setText(String.valueOf(count));

                }
            });
            decrement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count--;
                    value.setText(String.valueOf(count));

                }
            });

            Button dialogButton = (Button) dialog.findViewById(R.id.cancel_custom);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            final Button yesButton=(Button)dialog.findViewById(R.id.submit);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     //quantity.setText("Your donation is: "+count);
                     dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
    public class GetParticularNeedDetails extends AsyncTask {
        HttpResponse response;
        ArrayList<NeedDetails> needdetails;

        @Override
        protected Object doInBackground(Object[] objects) {
            client = new DefaultHttpClient();
            response = Connectivity.makeGetRequest(RestAPIURL.needList, client, Connectivity.getAuthToken(OrgDetailsActivity.this, Connectivity.Donor_Token));
            if (response != null)
                if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {
                    try {
                        JSONObject jsonObject = new JSONObject(Connectivity.getJosnFromResponse(response));
                        JSONArray results = jsonObject.getJSONArray("results");
                        Gson gson = new Gson();
                        needdetails = gson.fromJson(results.toString(), new TypeToken<List<NeedDetails>>() {
                        }.getType());
                        Log.d("Result", needdetails.size() + "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("CAllS ", "Response null");
                }


            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            needid = 0;
            need = needdetails.get(needid);
            Log.d("out", need + "");
            itemslist = need.getItems();
            Log.d("ItemsList", "doInBackground: " + itemslist);
            orgdetails = need.getOrg();
            DisplayOrgDetails(orgdetails);
            Log.d("ItemListsize", String.valueOf(itemslist.size()));

            DatabaseHelper db=new DatabaseHelper(context);
            mainItemDetailsList=db.getAllMainItemDetails();

            for(int i=0;i<itemslist.size();i++)
            {
                NeedItemDetails needItemDetails = (NeedItemDetails) itemslist.get(i);
                needItemId=needItemDetails.getItem_type_id();
                needQuantity=needItemDetails.getQuantity();
                Log.d("name2",needItemId+"");
                subItemId = needItemDetails.getSub_item_type_id();


                for (int j = 0; j < mainItemDetailsList.size(); j++) {
                    MainItemDetails mainItemDetails = mainItemDetailsList.get(j);
                    Log.d("name",mainItemDetails.getMainItemCode()+"");
                    if (needItemId == mainItemDetails.getMainItemCode()) {
                        String mainItemName = mainItemDetails.getMainItemName();
                        Log.d("name1",mainItemName+"");
                        //needitems.add(mainItemName);
                        //needitems.add(needQuantity);
                    }
                }

               /* mainItemDetails=mainItemDetailsList.get(needItemId);
                mainItemCode=mainItemDetails.getMainItemCode();
                if(mainItemCode==needItemId)
               {
                    mainitemname=mainItemDetails.getMainItemName();

                    needitems.add(mainitemname);
                    needitems.add(needQuantity);
                }*/
            }


        }

        public void DisplayOrgDetails(OrganisationDetails orgdetails) {
           OrgName=orgdetails.org_name;
            Address=orgdetails.address;
            Mobile=orgdetails.mobile;
            EmailId=orgdetails.email;
            Organisationname.setText(OrgName);
            Organisationmobile.setText(Mobile);
            Organisationaddress.setText(Address);
            Organisationemail.setText(EmailId);
        }
    }

}




