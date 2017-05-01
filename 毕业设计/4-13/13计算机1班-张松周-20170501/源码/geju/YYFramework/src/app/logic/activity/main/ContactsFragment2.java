package app.logic.activity.main;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.sort.sortlistview.CharacterParser;
import com.sort.sortlistview.PinyinComparator;
import com.sort.sortlistview.SideBar;
import com.sort.sortlistview.SortAdapter;
import com.sort.sortlistview.SortModel;
import com.sort.sortlistview.SideBar.OnTouchingLetterChangedListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import app.logic.activity.chat.ChatRoomListActivity;
import app.logic.activity.friends.AddFriendsActivity;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.logic.controller.UserManagerController;
import app.logic.pojo.FriendInfo;
import app.logic.singleton.YYInterface;
import app.logic.singleton.YYInterface.UpdataFriendListListener;
import app.logic.singleton.YYSingleton;
import app.utils.common.Listener;
import app.yy.geju.R;

/*
 * GZYY    2016-8-27  下午1:12:06
 */

public class ContactsFragment2 extends Fragment implements OnItemClickListener, YYInterface.OnHasFriendsStatusPoint {

    private View view;
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList = new ArrayList<SortModel>();
    private PinyinComparator pinyinComparator;
    private boolean haveData = false;
    protected WeakReference<View> mRootView;
    private ImageView hasFriendPoint;
    private View empty_view;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_contacts_list2, null);
            initViews();
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        loadContacts();
    }

    private void initViews() {
        addListener();
        empty_view = view.findViewById(R.id.empty_view);
        ((TextView) view.findViewById(R.id.empty_tv01)).setText("您还没有添加任何朋友");
        ((TextView) view.findViewById(R.id.empty_tv02)).setText("赶紧添加你的朋友吧");
        hasFriendPoint = (ImageView) view.findViewById(R.id.point_iv);
        if(HomeActivity.haveFriendsRequest){  //有朋友请求的时候，红点显示（主要用于一开始进入到这个界面是，其他情况由接口回调来决定）
            hasFriendPoint.setVisibility(View.VISIBLE);
        }else{
            hasFriendPoint.setVisibility(View.GONE);
        }
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        sideBar = (SideBar) view.findViewById(R.id.sidrbar);
        dialog = (TextView) view.findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                //if (position != -1) {
                    //sortListView.setSelection(position);
                //}
                if(position>=0 && position < ( SourceDateList.size()-1) ){
                    sortListView.setSelection(position);
                }
            }
        });
        sortListView = (ListView) view.findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(this);
        adapter = new SortAdapter(getContext(), SourceDateList);
        sortListView.setAdapter(adapter);

        // 删除好友后回调更新
        YYSingleton.getInstance().setUpdataFriendListListener(new UpdataFriendListListener() {
            @Override
            public void onCallBack() {
                // loadContacts();
            }
        });
        YYSingleton.getInstance().setOnHasFriendsStatusPoint(this);
    }

    private void addListener() {
        view.findViewById(R.id.new_friends_rl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getContext(), AddFriendsActivity.class));
            }
        });
        view.findViewById(R.id.chatRoom_rl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getContext(), ChatRoomListActivity.class));
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (SourceDateList == null) {
            return;
        }
        SortModel infoModel = SourceDateList.get(position);
        if (infoModel != null) {
            if (infoModel.getFriendInfo().isRequest_accept() || infoModel.getFriendInfo().isResponse()) {
                Intent intent = new Intent();
                intent.setClass(getContext(), PreviewFriendsInfoActivity.class);
                intent.putExtra(PreviewFriendsInfoActivity.kFROM_CHART_ACTIVITY, false);
                intent.putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID, infoModel.getFriendInfo().getWp_friends_info_id());
                startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.setClass(getContext(), AddFriendsActivity.class);
                startActivity(intent);
            }
        }
    }

    /*
    填充数据
     */
    private List<SortModel> filledDataList(List<FriendInfo> models) {
        List<SortModel> list = new ArrayList<SortModel>();
        for (int i = 0; i < models.size(); i++) {
            SortModel sortModel = new SortModel(models.get(i));
            String tempString = models.get(i).getFriend_name() == null || TextUtils.isEmpty(models.get(i).getFriend_name()) ? models.get(i).getNickName() : models.get(i).getFriend_name();
            sortModel.setName(tempString);
            String pinyinString = characterParser.getSelling(tempString);
            String sortString = pinyinString.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) { //!"unknown".equals(pinyinString) &&
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            if(tempString!=null && "昵".equals(tempString.substring(0,1))){  //对“昵”字做特殊处理（目前没有找到更好的库）
                sortModel.setSortLetters("N");
            }
            list.add(sortModel);
        }
        return list;
    }

    /**
     * ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    //获取联系人列表
    private void loadContacts() {
        UserManagerController.getFriendsList(getActivity(), new Listener<List<FriendInfo>, List<FriendInfo>>() {
            @Override
            public void onCallBack(List<FriendInfo> request, List<FriendInfo> reply) {
                ArrayList<FriendInfo> tmpInfos = new ArrayList<FriendInfo>();

                if (reply != null && reply.size() > 0) {
                    String myPhone = UserManagerController.getCurrUserInfo().getPhone();
                    for (FriendInfo friendInfo : reply) {
                        if (friendInfo.getPhone() != null && !friendInfo.getPhone().equals(myPhone)) {

                            if (friendInfo.isResponse() && friendInfo.isRequest_accept()) {
                                friendInfo.setOtherRequest(false);
                                tmpInfos.add(friendInfo);
                            }

                        }
                    }
                }
                String[] data = new String[tmpInfos.size()];
                for (int i = 0; i < tmpInfos.size(); i++) {
                    data[i] = tmpInfos.get(i).getNickName();
                }

                SourceDateList.clear();
                SourceDateList.addAll(filledDataList(tmpInfos));

                //自定义排序
                Collections.sort(SourceDateList, pinyinComparator);
                adapter.notifyDataSetChanged();
                if (SourceDateList.size() > 0) {
                    empty_view.setVisibility(View.GONE);
                } else {
                    empty_view.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    @Override
    public void onCallBack(boolean status) {

        hasFriendPoint.setVisibility(status ? View.VISIBLE : View.INVISIBLE);

    }
}
