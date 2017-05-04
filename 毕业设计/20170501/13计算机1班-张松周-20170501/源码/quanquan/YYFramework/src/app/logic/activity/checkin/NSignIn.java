package app.logic.activity.checkin;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import app.logic.adapter.YYBaseListAdapter;
import app.logic.pojo.UnCheckInInfo;
import app.yy.geju.R;

public class NSignIn extends Fragment {

	private View view;
	public GridView mGridView;
	//适配器
	private YYBaseListAdapter<UnCheckInInfo> mAdapter;

	public NSignIn() {
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_n_signin, container, false);
		mGridView = (GridView) view.findViewById(R.id.n_singin_gv);
		return view;
	}

	private void steView(View view) {
	}
}





