package app.logic.activity.org;

import java.util.List;


import org.ql.activity.customtitle.ActActivity;
import org.ql.activity.customtitle.DefaultHandler;
import org.ql.views.listview.QLXListView;
import org.ql.views.listview.QLXListView.IXListViewListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import app.config.http.HttpConfig;

import app.logic.activity.ActTitleHandler;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.OrganizationInfo;
import app.utils.common.Listener;
import app.yy.geju.R;

public class MyOrgList extends ActActivity  {
       @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	
    	ActTitleHandler mHandler = new ActTitleHandler();
		setAbsHandler(mHandler);
   	    setContentView(R.layout.aty_org_list);
		mHandler.setTitle("我的组织");
    }

}
