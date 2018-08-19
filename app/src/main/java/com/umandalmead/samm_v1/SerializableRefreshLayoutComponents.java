package com.umandalmead.samm_v1;

import android.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;

import java.io.Serializable;

/**
 * Created by MeadRoseAnn on 8/19/2018.
 */

public class SerializableRefreshLayoutComponents  implements Serializable
{
    public SwipeRefreshLayout _swipeRefreshLayoutSerializable;
    public FragmentManager _fragmentManager;
    public NonScrollListView _adminUsersListView;

    public SerializableRefreshLayoutComponents(SwipeRefreshLayout swipeRefreshLayout, FragmentManager fragmentManager, NonScrollListView adminUsersListView)
    {
        this._swipeRefreshLayoutSerializable = swipeRefreshLayout;
        this._fragmentManager = fragmentManager;
        this._adminUsersListView =  adminUsersListView;
    }
}
