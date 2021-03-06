package com.thoughtworks.retailapp.util;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AnalyticsHelpers {

    public static class Currency {
        public static final String RUPEE = "rupee";
    }

    public static class Param {
        public static final String CHECKOUT_STEP = "checkout_step";
        public static final String CONTENT_TYPE = "content_type";
        public static final String CURRENCY = "currency";
        public static final String COUPON = "coupon";
        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String FLIGHT_NUMBER = "flight_number";
        public static final String GROUP_ID = "group_id";
        public static final String ITEM_CATEGORY = "item_category";
        public static final String ITEM_ID = "item_id";
        public static final String ITEM_LOCATION_ID = "item_location_id";
        public static final String ITEM_NAME = "item_name";
        public static final String LOCATION = "location";
        public static final String LEVEL = "level";
        public static final String SIGN_UP_METHOD = "sign_up_method";
        public static final String NUMBER_OF_NIGHTS = "number_of_nights";
        public static final String NUMBER_OF_PASSENGERS = "number_of_passengers";
        public static final String NUMBER_OF_ROOMS = "number_of_rooms";
        public static final String DESTINATION = "destination";
        public static final String ORIGIN = "origin";
        public static final String PRICE = "price";
        public static final String QUANTITY = "quantity";
        public static final String SCORE = "score";
        public static final String SHIPPING = "shipping";
        public static final String TRANSACTION_ID = "transaction_id";
        public static final String SEARCH_TERM = "search_term";
        public static final String TAX = "tax";
        public static final String VALUE = "value";
        public static final String VIRTUAL_CURRENCY_NAME = "virtual_currency_name";

        protected Param() {
        }
    }

    public static class Event {
        public static final String ADD_PAYMENT_INFO = "add_payment_info";
        public static final String ADD_TO_CART = "add_to_cart";
        public static final String ADD_TO_WISHLIST = "add_to_wishlist";
        public static final String APP_OPEN = "app_open";
        public static final String BEGIN_CHECKOUT = "begin_checkout";
        public static final String ECOMMERCE_PURCHASE = "ecommerce_purchase";
        public static final String GENERATE_LEAD = "generate_lead";
        public static final String JOIN_GROUP = "join_group";
        public static final String LEVEL_UP = "level_up";
        public static final String LOGIN = "login";
        public static final String POST_SCORE = "post_score";
        public static final String PRESENT_OFFER = "present_offer";
        public static final String PURCHASE_REFUND = "purchase_refund";
        public static final String SEARCH = "search";
        public static final String SELECT_CONTENT = "select_content";
        public static final String SHARE = "share";
        public static final String SIGN_UP = "sign_up";
        public static final String SPEND_VIRTUAL_CURRENCY = "spend_virtual_currency";
        public static final String TUTORIAL_BEGIN = "tutorial_begin";
        public static final String TUTORIAL_COMPLETE = "tutorial_complete";
        public static final String UNLOCK_ACHIEVEMENT = "unlock_achievement";
        public static final String VIEW_ITEM = "view_item";
        public static final String VIEW_ITEM_LIST = "view_item_list";
        public static final String VIEW_SEARCH_RESULTS = "view_search_results";
        public static final String EARN_VIRTUAL_CURRENCY = "earn_virtual_currency";
        public static final String REMOVE_FROM_CART = "remove_from_cart";
        public static final String CHECKOUT_PROGRESS = "checkout_progress";
        public static final String SHIPPING_AND_PAYMENT = "shipping_and_payment";
        public static final String THANK_YOU_SCREEN = "thank_you_screen";

        protected Event() {
        }
    }

    private FirebaseAnalytics mFirebaseAnalytics;
    private static AnalyticsHelpers instance;

    protected static Object lock = new Object();

    private AnalyticsHelpers(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static AnalyticsHelpers getInstance(Context context) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new AnalyticsHelpers(context);
                    return instance;
                } else {
                    return instance;
                }
            }
        } else {
            return instance;
        }
    }

    public void logEvent(String eventType, Bundle params) {
        mFirebaseAnalytics.logEvent(eventType, params);
    }
}
