package com.qiyue.qdmobile.ui.filters;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipManager;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.models.Filter;
import com.qiyue.qdmobile.widgets.CSSListFragment;
import com.qiyue.qdmobile.widgets.DragnDropListView;
import com.qiyue.qdmobile.widgets.DragnDropListView.DropListener;

import java.util.ArrayList;

public class AccountFiltersListFragment extends CSSListFragment {
    private static final String THIS_FILE = "AccountFiltersListFragment";

    private boolean dualPane;
    private Long curCheckFilterId = SipProfile.INVALID_ID;
    private View mHeaderView;
    private AccountFiltersListAdapter mAdapter;
    private long accountId;

    private final static String CURRENT_CHOICE = "curChoice";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void setAccountId(long accId) {
        accountId = accId;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView lv = getListView();

        //getListView().setSelector(R.drawable.transparent);
        lv.setCacheColorHint(Color.TRANSPARENT);


        // View management
        View detailsFrame = getActivity().findViewById(R.id.details);
        dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            curCheckFilterId = savedInstanceState.getLong(CURRENT_CHOICE, SipProfile.INVALID_ID);
            //curCheckWizard = savedInstanceState.getString(CURRENT_WIZARD);
        }
        setListShown(false);
        if (mAdapter == null) {
            if (mHeaderView != null) {
                lv.addHeaderView(mHeaderView, null, true);
            }
            mAdapter = new AccountFiltersListAdapter(getActivity(), null);
            //getListView().setEmptyView(getActivity().findViewById(R.id.progress_container));
            //getActivity().findViewById(android.R.id.empty).setVisibility(View.GONE);
            setListAdapter(mAdapter);
            registerForContextMenu(lv);

            lv.setVerticalFadingEdgeEnabled(true);
        }

        if (dualPane) {
            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lv.setVerticalScrollBarEnabled(false);
            lv.setFadingEdgeLength(50);

            updateCheckedItem();
            // Make sure our UI is in the correct state.
        } else {
            lv.setVerticalScrollBarEnabled(true);
            lv.setFadingEdgeLength(100);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Use custom drag and drop view -- reuse the one of accounts_edit_list
        View v = inflater.inflate(R.layout.accounts_edit_list, container, false);

        final DragnDropListView lv = (DragnDropListView) v.findViewById(android.R.id.list);

        lv.setGrabberId(R.id.grabber);
        // Setup the drop listener
        lv.setOnDropListener(new DropListener() {
            @Override
            public void drop(int from, int to) {
                Log.d(THIS_FILE, "Drop from " + from + " to " + to);
                int hvC = lv.getHeaderViewsCount();
                from = Math.max(0, from - hvC);
                to = Math.max(0, to - hvC);

                int i;
                // First of all, compute what we get before move
                ArrayList<Long> orderedList = new ArrayList<Long>();
                CursorAdapter ad = (CursorAdapter) getListAdapter();
                for (i = 0; i < ad.getCount(); i++) {
                    orderedList.add(ad.getItemId(i));
                }
                // Then, invert in the current list the two items ids
                Long moved = orderedList.remove(from);
                orderedList.add(to, moved);

                // Finally save that in db
                ContentResolver cr = getActivity().getContentResolver();
                for (i = 0; i < orderedList.size(); i++) {
                    Uri uri = ContentUris.withAppendedId(SipManager.FILTER_ID_URI_BASE, orderedList.get(i));
                    ContentValues cv = new ContentValues();
                    cv.put(Filter.FIELD_PRIORITY, i);
                    cr.update(uri, cv, null, null);
                }
            }
        });

        OnClickListener addClickButtonListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddFilter();
            }
        };
        // Header view
        mHeaderView = inflater.inflate(R.layout.generic_add_header_list, container, false);
        mHeaderView.setOnClickListener(addClickButtonListener);
        ((TextView) mHeaderView.findViewById(R.id.text)).setText(R.string.add_filter);

        // Empty view
        Button bt = (Button) v.findViewById(android.R.id.empty);
        bt.setText(R.string.add_filter);
        bt.setOnClickListener(addClickButtonListener);

        return v;
    }

    private void updateCheckedItem() {
        if (curCheckFilterId != SipProfile.INVALID_ID) {
            for (int i = 0; i < getListAdapter().getCount(); i++) {
                long profId = getListAdapter().getItemId(i);
                if (profId == curCheckFilterId) {
                    getListView().setItemChecked(i, true);
                }
            }
        } else {
            for (int i = 0; i < getListAdapter().getCount(); i++) {
                getListView().setItemChecked(i, false);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(CURRENT_CHOICE, curCheckFilterId);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Log.d(THIS_FILE, "Checked " + position + " et " + id);

        ListView lv = getListView();
        lv.setItemChecked(position, true);

        curCheckFilterId = id;
        showDetails(id);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    private void showDetails(long filterId) {

        Intent it = new Intent(getActivity(), EditFilter.class);
        it.putExtra(Intent.EXTRA_UID, filterId);
        it.putExtra(Filter.FIELD_ACCOUNT, accountId);
        startActivity(it);
        	
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), SipManager.FILTER_URI, new String[]{
                BaseColumns._ID,
                Filter.FIELD_ACCOUNT,
                Filter.FIELD_ACTION,
                Filter.FIELD_MATCHES,
                Filter.FIELD_PRIORITY,
                Filter.FIELD_REPLACE
        }, Filter.FIELD_ACCOUNT + "=?", new String[]{Long.toString(accountId)}, Filter.DEFAULT_ORDER);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        super.onLoadFinished(loader, data);
        updateCheckedItem();
    }

    public static final int MENU_ITEM_MODIFY = Menu.FIRST;
    public static final int MENU_ITEM_DELETE = Menu.FIRST + 1;

    /**
     * Retrieve filter id from a given context menu info pressed
     *
     * @param cmi The context menu info to retrieve infos from
     * @return corresponding filter id if everything goes well, -1 if not able to retrieve filter
     */
    private long filterIdFromContextMenuInfo(ContextMenuInfo cmi) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) cmi;
        } catch (ClassCastException e) {
            Log.e(THIS_FILE, "bad menuInfo", e);
            return -1;
        }
        return info.id;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        final long filterId = filterIdFromContextMenuInfo(menuInfo);
        if (filterId == -1) {
            return;
        }

        menu.add(0, MENU_ITEM_MODIFY, 0, R.string.edit);
        menu.add(0, MENU_ITEM_DELETE, 0, R.string.delete_filter);

    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        final long filterId = filterIdFromContextMenuInfo(item.getMenuInfo());
        if (filterId == -1) {
            // For some reason the requested item isn't available, do nothing
            return super.onContextItemSelected(item);
        }

        switch (item.getItemId()) {
            case MENU_ITEM_DELETE: {
                getActivity().getContentResolver().delete(ContentUris.withAppendedId(SipManager.FILTER_ID_URI_BASE, filterId), null, null);
                return true;
            }
            case MENU_ITEM_MODIFY: {
                showDetails(filterId);
                return true;
            }
        }
        return super.onContextItemSelected(item);

    }

    private void onClickAddFilter() {
        showDetails(-1);
    }

    @Override
    public void changeCursor(Cursor c) {
        if (mAdapter != null) {
            mAdapter.changeCursor(c);
        }
    }


}
