package com.hitesh_sahu.retailapp.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hitesh_sahu.retailapp.R;
import com.hitesh_sahu.retailapp.domain.mining.AprioriFrequentItemsetGenerator;
import com.hitesh_sahu.retailapp.domain.mining.FrequentItemsetData;
import com.hitesh_sahu.retailapp.model.CenterRepository;
import com.hitesh_sahu.retailapp.model.entities.Product;
import com.hitesh_sahu.retailapp.util.AnalyticsHelpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.hitesh_sahu.retailapp.view.activities.ECartHomeActivity.MINIMUM_SUPPORT;

public class ShippingAndPayment extends AppCompatActivity {

    private AnalyticsHelpers mAnalyticsHelpers;
    AprioriFrequentItemsetGenerator<String> generator =
            new AprioriFrequentItemsetGenerator<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_and_payment);
        mAnalyticsHelpers = AnalyticsHelpers.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(AnalyticsHelpers.Param.CHECKOUT_STEP, AnalyticsHelpers.Event.SHIPPING_AND_PAYMENT);
        mAnalyticsHelpers.logEvent(AnalyticsHelpers.Event.CHECKOUT_PROGRESS, bundle);
        Button confirmBtn = (Button) findViewById(R.id.btn_confirm);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCheckOut();
                goToThankYouScreen();
            }
        });
    }

    private void confirmCheckOut() {
        ArrayList<String> productId = new ArrayList<String>();

        for (Product productFromShoppingList : CenterRepository.getCenterRepository().getListOfProductsInShoppingList()) {
            //add product ids to array
            productId.add(productFromShoppingList.getProductId());
            logEvent(productFromShoppingList.getProductId(), productFromShoppingList.getItemName());
        }

        //pass product id array to Apriori ALGO
        CenterRepository.getCenterRepository()
                .addToItemSetList(new HashSet<>(productId));

        //Do Minning
        FrequentItemsetData<String> data = generator.generate(
                CenterRepository.getCenterRepository().getItemSetList()
                , MINIMUM_SUPPORT);

        for (Set<String> itemset : data.getFrequentItemsetList()) {
            Log.e("APriori", "Item Set : " +
                    itemset + "Support : " +
                    data.getSupport(itemset));
        }

        //clear all list item
        CenterRepository.getCenterRepository().getListOfProductsInShoppingList().clear();
        List<Product> productList =CenterRepository.getCenterRepository().getListOfProductsInShoppingList();
    }

    private void logEvent(String id, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(AnalyticsHelpers.Param.ITEM_ID, id);
        bundle.putString(AnalyticsHelpers.Param.ITEM_NAME, name);
        mAnalyticsHelpers.logEvent(AnalyticsHelpers.Event.ECOMMERCE_PURCHASE, bundle);
    }

    private void goToThankYouScreen(){
        Intent intent = new Intent(this, ThankYouScreenActivity.class);
        startActivity(intent);
        finish();
    }

}
