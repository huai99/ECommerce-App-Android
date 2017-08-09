package com.thoughtworks.retailapp.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.thoughtworks.retailapp.R;
import com.thoughtworks.retailapp.util.AnalyticsHelpers;

public class ThankYouScreenActivity extends AppCompatActivity {

    private AnalyticsHelpers mAnalyticsHelpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you_screen);
        mAnalyticsHelpers = AnalyticsHelpers.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(AnalyticsHelpers.Param.CHECKOUT_STEP, AnalyticsHelpers.Event.THANK_YOU_SCREEN);
        mAnalyticsHelpers.logEvent(AnalyticsHelpers.Event.CHECKOUT_PROGRESS, bundle);
        Button goBackToMainBtn = (Button) findViewById(R.id.btn_return_main);
        goBackToMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMain();
            }
        });
    }

    private void returnToMain() {
        Intent intent = new Intent(this, ECartHomeActivity.class);
        startActivity(intent);
        finish();
    }

}
