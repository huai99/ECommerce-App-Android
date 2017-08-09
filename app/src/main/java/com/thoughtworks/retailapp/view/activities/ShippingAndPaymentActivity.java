package com.thoughtworks.retailapp.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.thoughtworks.retailapp.R;
import com.thoughtworks.retailapp.domain.mining.AprioriFrequentItemsetGenerator;
import com.thoughtworks.retailapp.domain.mining.FrequentItemsetData;
import com.thoughtworks.retailapp.model.CenterRepository;
import com.thoughtworks.retailapp.model.entities.Product;
import com.thoughtworks.retailapp.util.AnalyticsHelpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.thoughtworks.retailapp.view.activities.ECartHomeActivity.MINIMUM_SUPPORT;

public class ShippingAndPaymentActivity extends AppCompatActivity {

    private AnalyticsHelpers mAnalyticsHelpers;
    AprioriFrequentItemsetGenerator<String> generator =
            new AprioriFrequentItemsetGenerator<>();
    String shippingOptions;
    Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_and_payment);
        mAnalyticsHelpers = AnalyticsHelpers.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(AnalyticsHelpers.Param.CHECKOUT_STEP, AnalyticsHelpers.Event.SHIPPING_AND_PAYMENT);
        mAnalyticsHelpers.logEvent(AnalyticsHelpers.Event.CHECKOUT_PROGRESS, bundle);
        Button confirmBtn = (Button) findViewById(R.id.btn_confirm);
        mSpinner = (Spinner) findViewById(R.id.spinner_shipment);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shippingOptions = mSpinner.getSelectedItem().toString();
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
            logEvent(productFromShoppingList.getSellMRP(), AnalyticsHelpers.Currency.RUPEE, shippingOptions);
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
        List<Product> productList = CenterRepository.getCenterRepository().getListOfProductsInShoppingList();
    }

    private void logEvent(String value, String currency, String shipping) {
        Bundle bundle = new Bundle();
        bundle.putString(AnalyticsHelpers.Param.VALUE, value);
        bundle.putString(AnalyticsHelpers.Param.CURRENCY, currency);
        bundle.putString(AnalyticsHelpers.Param.SHIPPING, shipping);
        mAnalyticsHelpers.logEvent(AnalyticsHelpers.Event.ECOMMERCE_PURCHASE, bundle);
    }

    private void goToThankYouScreen() {
        Intent intent = new Intent(this, ThankYouScreenActivity.class);
        startActivity(intent);
        finish();
    }

}
