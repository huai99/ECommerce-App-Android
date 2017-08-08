/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hitesh_sahu.retailapp.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.bumptech.glide.Glide;
import com.hitesh_sahu.retailapp.R;
import com.hitesh_sahu.retailapp.model.CenterRepository;
import com.hitesh_sahu.retailapp.model.entities.Money;
import com.hitesh_sahu.retailapp.model.entities.Product;
import com.hitesh_sahu.retailapp.util.AnalyticsHelpers;
import com.hitesh_sahu.retailapp.util.ColorGenerator;
import com.hitesh_sahu.retailapp.util.Utils;
import com.hitesh_sahu.retailapp.view.activities.ECartHomeActivity;
import com.hitesh_sahu.retailapp.view.customview.ItemTouchHelperAdapter;
import com.hitesh_sahu.retailapp.view.customview.ItemTouchHelperViewHolder;
import com.hitesh_sahu.retailapp.view.customview.OnStartDragListener;
import com.hitesh_sahu.retailapp.view.customview.TextDrawable;
import com.hitesh_sahu.retailapp.view.customview.TextDrawable.IBuilder;
import com.hitesh_sahu.retailapp.view.fragment.MyCartFragment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple RecyclerView.Adapter that implements {@link ItemTouchHelperAdapter} to
 * respond to move and dismiss events from a
 * {@link android.support.v7.widget.helper.ItemTouchHelper}.
 *
 * @author Hitesh Sahu (hiteshsahu.com)
 */
public class ShoppingListAdapter extends
        RecyclerView.Adapter<ShoppingListAdapter.ItemViewHolder> implements
        ItemTouchHelperAdapter {

    private final OnStartDragListener mDragStartListener;

    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;

    private IBuilder mDrawableBuilder;

    private TextDrawable drawable;

    private String ImageUrl;

    private List<Product> productList = new ArrayList<Product>();
    private static OnItemClickListener clickListener;

    private Context context;

    private AnalyticsHelpers mAnalyticsHelpers;

    public ShoppingListAdapter(Context context,
                               OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;

        this.context = context;

        productList = CenterRepository.getCenterRepository().getListOfProductsInShoppingList();

        mAnalyticsHelpers = AnalyticsHelpers.getInstance(context);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_product_list, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        holder.itemName.setText(productList.get(position).getItemName());

        holder.itemDesc.setText(productList.get(position).getItemShortDesc());

        String sellCostString = Money.rupees(
                BigDecimal.valueOf(Long.valueOf(productList.get(position)
                        .getSellMRP()))).toString()
                + "  ";

        String buyMRP = Money.rupees(
                BigDecimal.valueOf(Long.valueOf(productList.get(position)
                        .getMRP()))).toString();

        String costString = sellCostString + buyMRP;

        holder.itemCost.setText(costString, BufferType.SPANNABLE);

        Spannable spannable = (Spannable) holder.itemCost.getText();

        spannable.setSpan(new StrikethroughSpan(), sellCostString.length(),
                costString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mDrawableBuilder = TextDrawable.builder().beginConfig().withBorder(4)
                .endConfig().roundRect(10);

        drawable = mDrawableBuilder.build(String.valueOf(productList
                .get(position).getItemName().charAt(0)), mColorGenerator
                .getColor(productList.get(position).getItemName()));

        ImageUrl = productList.get(position).getImageURL();

        holder.quanitity.setText(CenterRepository.getCenterRepository()
                .getListOfProductsInShoppingList().get(position).getQuantity());

        Glide.with(context).load(ImageUrl).placeholder(drawable)
                .error(drawable).animate(R.anim.base_slide_right_in)
                .centerCrop().into(holder.imagView);

        // Start a drag whenever the handle view it touched
        holder.imagView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

        holder.addItem.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addItem(holder, position);
                Product product = productList.get(position);
                logEvent(AnalyticsHelpers.Event.ADD_TO_CART,
                        product.getProductId(),
                        product.getItemName(),
                        product.getQuantity(),
                        product.getSellMRP());
                logBeginCheckoutEvent(product.getSellMRP(), AnalyticsHelpers.Currency.RUPEE);
                logCheckOutProcess(AnalyticsHelpers.Event.ADD_TO_CART);
            }
        });

        holder.removeItem.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                removeItem(holder, position);
                Product product = productList.get(position);
                logEvent(AnalyticsHelpers.Event.REMOVE_FROM_CART,
                        product.getProductId(),
                        product.getItemName(),
                        product.getQuantity(),
                        product.getSellMRP());
                logCheckOutProcess(AnalyticsHelpers.Event.REMOVE_FROM_CART);
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {

        ((ECartHomeActivity) context).updateItemCount(false);

        ((ECartHomeActivity) context).updateCheckOutAmount(
                BigDecimal.valueOf(Long.valueOf(CenterRepository
                        .getCenterRepository().getListOfProductsInShoppingList().get(position)
                        .getSellMRP())), false);

        CenterRepository.getCenterRepository().getListOfProductsInShoppingList().remove(position);

        if (Integer.valueOf(((ECartHomeActivity) context).getItemCount()) == 0) {

            MyCartFragment.updateMyCartFragment(false);

        }

        Utils.vibrate(context);

        // productList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        Collections.swap(productList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return productList.size();

    }

    /**
     * Simple example of a view holder that implements
     * {@link ItemTouchHelperViewHolder} and has a "handle" view that initiates
     * a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder, View.OnClickListener {

        // public final ImageView handleView;

        TextView itemName, itemDesc, itemCost, availability, quanitity,
                addItem, removeItem;
        ImageView imagView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            // handleView = (ImageView) itemView.findViewById(R.id.handle);

            itemName = (TextView) itemView.findViewById(R.id.item_name);

            itemDesc = (TextView) itemView.findViewById(R.id.item_short_desc);

            itemCost = (TextView) itemView.findViewById(R.id.item_price);

            availability = (TextView) itemView
                    .findViewById(R.id.iteam_avilable);

            quanitity = (TextView) itemView.findViewById(R.id.iteam_amount);

            itemName.setSelected(true);

            imagView = ((ImageView) itemView.findViewById(R.id.product_thumb));

            addItem = ((TextView) itemView.findViewById(R.id.add_item));

            removeItem = ((TextView) itemView.findViewById(R.id.remove_item));

            itemView.setOnClickListener(this);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        @Override
        public void onClick(View v) {

            clickListener.onItemClick(v, getPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(
            final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    private void logEvent(String eventType, String id, String name, String quantity, String price) {
        Bundle bundle = new Bundle();
        bundle.putString(AnalyticsHelpers.Param.ITEM_ID, id);
        bundle.putString(AnalyticsHelpers.Param.ITEM_NAME, name);
        bundle.putString(AnalyticsHelpers.Param.QUANTITY, quantity);
        bundle.putString(AnalyticsHelpers.Param.PRICE, price);
        mAnalyticsHelpers.logEvent(eventType, bundle);
    }

    private void logBeginCheckoutEvent(String value, String currency) {
        Bundle bundle = new Bundle();
        bundle.putString(AnalyticsHelpers.Param.VALUE, value);
        bundle.putString(AnalyticsHelpers.Param.CURRENCY, currency);
        mAnalyticsHelpers.logEvent(AnalyticsHelpers.Event.BEGIN_CHECKOUT, bundle);
    }

    private void logCheckOutProcess(String process) {
        Bundle bundle = new Bundle();
        bundle.putString(AnalyticsHelpers.Param.CHECKOUT_STEP, process);
        mAnalyticsHelpers.logEvent(AnalyticsHelpers.Event.CHECKOUT_PROGRESS, bundle);
    }

    private void addItem(ItemViewHolder holder, int position) {
        CenterRepository
                .getCenterRepository()
                .getListOfProductsInShoppingList()
                .get(position)
                .setQuantity(
                        String.valueOf(

                                Integer.valueOf(CenterRepository
                                        .getCenterRepository().getListOfProductsInShoppingList()
                                        .get(position).getQuantity()) + 1));

        holder.quanitity.setText(CenterRepository.getCenterRepository()
                .getListOfProductsInShoppingList().get(position).getQuantity());

        Utils.vibrate(context);

        ((ECartHomeActivity) context).updateCheckOutAmount(
                BigDecimal.valueOf(Long.valueOf(CenterRepository
                        .getCenterRepository().getListOfProductsInShoppingList()
                        .get(position).getSellMRP())), true);

    }

    private void removeItem(ItemViewHolder holder, int position) {
        if (Integer.valueOf(CenterRepository.getCenterRepository()
                .getListOfProductsInShoppingList().get(position).getQuantity()) > 2) {

            CenterRepository
                    .getCenterRepository()
                    .getListOfProductsInShoppingList()
                    .get(position)
                    .setQuantity(
                            String.valueOf(

                                    Integer.valueOf(CenterRepository
                                            .getCenterRepository()
                                            .getListOfProductsInShoppingList().get(position)
                                            .getQuantity()) - 1));

            holder.quanitity.setText(CenterRepository
                    .getCenterRepository().getListOfProductsInShoppingList()
                    .get(position).getQuantity());

            ((ECartHomeActivity) context).updateCheckOutAmount(
                    BigDecimal.valueOf(Long.valueOf(CenterRepository
                            .getCenterRepository().getListOfProductsInShoppingList()
                            .get(position).getSellMRP())), false);

            Utils.vibrate(context);
        } else if (Integer.valueOf(CenterRepository.getCenterRepository()
                .getListOfProductsInShoppingList().get(position).getQuantity()) == 1) {
            ((ECartHomeActivity) context).updateItemCount(false);

            ((ECartHomeActivity) context).updateCheckOutAmount(
                    BigDecimal.valueOf(Long.valueOf(CenterRepository
                            .getCenterRepository().getListOfProductsInShoppingList()
                            .get(position).getSellMRP())), false);

            CenterRepository.getCenterRepository().getListOfProductsInShoppingList()
                    .remove(position);

            if (Integer.valueOf(((ECartHomeActivity) context)
                    .getItemCount()) == 0) {

                MyCartFragment.updateMyCartFragment(false);

            }

            Utils.vibrate(context);

        }
    }
}
