package com.thoughtworks.retailapp.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.thoughtworks.retailapp.R;
import com.thoughtworks.retailapp.model.CenterRepository;
import com.thoughtworks.retailapp.model.entities.Product;
import com.thoughtworks.retailapp.util.Utils;
import com.thoughtworks.retailapp.util.Utils.AnimationType;
import com.thoughtworks.retailapp.view.activities.ECartHomeActivity;
import com.thoughtworks.retailapp.view.adapter.ShoppingListAdapter;
import com.thoughtworks.retailapp.view.adapter.ShoppingListAdapter.OnItemClickListener;
import com.thoughtworks.retailapp.view.customview.OnStartDragListener;
import com.thoughtworks.retailapp.view.customview.SimpleItemTouchHelperCallback;

public class MyCartFragment extends Fragment implements OnStartDragListener {

    public MyCartFragment() {
    }

    private ItemTouchHelper mItemTouchHelper;
    private static FrameLayout noItemDefault;
    private static RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_product_list_fragment, container,
                false);

        view.findViewById(R.id.slide_down).setVisibility(View.VISIBLE);
        view.findViewById(R.id.slide_down).setOnTouchListener(
                new OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        Utils.switchFragmentWithAnimation(R.id.frag_container,
                                new HomeFragment(),
                                ((ECartHomeActivity) (getContext())),
                                Utils.HOME_FRAGMENT, AnimationType.SLIDE_DOWN);

                        return false;
                    }
                });

        // Fill Recycler View

        noItemDefault = (FrameLayout) view.findViewById(R.id.default_nodata);

        recyclerView = (RecyclerView) view
                .findViewById(R.id.product_list_recycler_view);

        if (CenterRepository.getCenterRepository().getListOfProductsInShoppingList().size() != 0) {

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                    getActivity().getBaseContext());

            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);

            ShoppingListAdapter shoppinListAdapter = new ShoppingListAdapter(
                    getActivity(), this);

            recyclerView.setAdapter(shoppinListAdapter);


            shoppinListAdapter
                    .SetOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(View view, int position) {
                            ProductDetailsFragment detailsFragment = new ProductDetailsFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("subcategoryKey", "");
                            bundle.putInt("subcategoryKey", position);
                            bundle.putBoolean("isFromCart", true);
                            detailsFragment.setArguments(bundle);

                            Utils.switchFragmentWithAnimation(
                                    R.id.frag_container,
                                    detailsFragment,
                                    ((ECartHomeActivity) (getContext())), null,
                                    AnimationType.SLIDE_LEFT);

                        }
                    });

            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(
                    shoppinListAdapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recyclerView);

        } else {

            updateMyCartFragment(false);

        }

        view.findViewById(R.id.start_shopping).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Utils.switchContent(R.id.frag_container,
                                Utils.HOME_FRAGMENT,
                                ((ECartHomeActivity) (getContext())),
                                AnimationType.SLIDE_UP);

                    }
                });

        // Handle Back press
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK) {

                    Utils.switchContent(R.id.frag_container,
                            Utils.HOME_FRAGMENT,
                            ((ECartHomeActivity) (getContext())),
                            AnimationType.SLIDE_UP);

                }
                return true;
            }
        });

        return view;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    /**
     * @param rootView
     * @param myCartListView
     */
    public static void updateMyCartFragment(boolean showList) {

        if (showList) {

            if (null != recyclerView && null != noItemDefault) {
                recyclerView.setVisibility(View.VISIBLE);

                noItemDefault.setVisibility(View.GONE);
            }
        } else {
            if (null != recyclerView && null != noItemDefault) {
                recyclerView.setVisibility(View.GONE);

                noItemDefault.setVisibility(View.VISIBLE);
            }
        }
    }

}