package vuram_test_2.vuram.com.vuram_test_2;

import android.content.ClipData;
import android.content.Context;
import android.view.View;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class NeedDetails {
    int need_id;
    boolean is_fulfilled;
    String latitude, longitude;
    List<NeedItemDetails> items;
    List<DonationDetails> donations;
    OrganisationDetails org;

    NeedDetails() {
        items = new ArrayList<>();
        donations = new ArrayList<>();
    }

    public OrganisationDetails getOrganisationDetails(OrganisationDetails organisationDetails) {
        return organisationDetails;
    }

    public void setOrganisationDetails(OrganisationDetails organisationDetails)
    {
        this.org=organisationDetails;
    }

    public  String  print()
    {
        return(items.get(0).print());
    }

    public int getNeed_id() {
        return need_id;
    }

    public void setNeed_id(int need_id) {
        this.need_id = need_id;
    }

    public boolean is_fulfilled() {
        return is_fulfilled;
    }

    public void setIs_fulfilled(boolean is_fulfilled) {
        this.is_fulfilled = is_fulfilled;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public List<NeedItemDetails> getItems() {
        return items;
    }

    public void setItems(List<NeedItemDetails> items) {
        this.items = items;
    }

    public List<DonationDetails> getDonations() {
        return donations;
    }

    public void setDonations(List<DonationDetails> donations) {
        this.donations = donations;
    }

    public OrganisationDetails getOrg() {
        return org;
    }

    public void setOrg(OrganisationDetails org) {
        this.org = org;
    }
}