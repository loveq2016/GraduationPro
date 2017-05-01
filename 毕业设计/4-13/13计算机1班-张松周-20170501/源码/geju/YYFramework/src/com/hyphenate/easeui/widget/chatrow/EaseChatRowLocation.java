package com.hyphenate.easeui.widget.chatrow;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;

import app.logic.activity.navi.other.Location;
import app.logic.pojo.TYLocationInfo;
import app.utils.managers.TYLocationManager;
import app.yy.geju.R;
import com.hyphenate.easeui.ui.EaseBaiduMapActivity;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.LatLng;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EaseChatRowLocation extends EaseChatRow {

    private TextView locationView;
    private EMLocationMessageBody locBody;
    private BaiduMap baiduMap;
    private TextureMapView mapView;
    private View ll_markView;

    public EaseChatRowLocation(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);

    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_location2 : R.layout.ease_row_sent_location2, this);
    }

    @Override
    protected void onFindViewById() {
        locationView = (TextView) findViewById(R.id.tv_location);
        mapView = (TextureMapView) findViewById(R.id.mv_location);

        ll_markView = findViewById(R.id.ll_location);

        if (ll_markView != null) {
            ll_markView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBubbleClick();
                }
            });
        }


    }

    @Override
    protected void onSetUpView() {
        locBody = (EMLocationMessageBody) message.getBody();
        locationView.setText(locBody.getAddress());
//
        setupBaiduView();
        // handle sending message
        if (message.direct() == EMMessage.Direct.SEND) {
            setMessageSendCallback();
            switch (message.status()) {
                case CREATE:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.GONE);
                    break;
                case FAIL:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                    progressBar.setVisibility(View.VISIBLE);
                    statusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        } else {
            if (!message.isAcked() && message.getChatType() == ChatType.Chat) {
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 设置百度地图位置
     */
    private void setupBaiduView() {
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mapView.showZoomControls(false);
        mapView.showScaleControl(false);
        baiduMap.setMyLocationEnabled(false);
        //baiduMap.getUiSettings().setAllGesturesEnabled(false);
        baiduMap.getUiSettings().setRotateGesturesEnabled(false);
        baiduMap.getUiSettings().setScrollGesturesEnabled(false);
        baiduMap.getUiSettings().setZoomGesturesEnabled(false);

        com.baidu.mapapi.model.LatLng lla = new com.baidu.mapapi.model.LatLng(locBody.getLatitude(), locBody.getLongitude());
        CoordinateConverter converter = new CoordinateConverter();
        converter.coord(lla);
        converter.from(CoordinateConverter.CoordType.COMMON);
        com.baidu.mapapi.model.LatLng convertLatLng = converter.convert();

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        baiduMap.setMapStatus( msu );
//        com.baidu.mapapi.model.LatLng p = new com.baidu.mapapi.model.LatLng(locBody.getLatitude() , locBody.getLongitude() );
        baiduMap.setMyLocationEnabled(true);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(convertLatLng);
        baiduMap.animateMapStatus(mapStatusUpdate,17);

//        com.baidu.mapapi.model.LatLng lla = new com.baidu.mapapi.model.LatLng(locBody.getLatitude(), locBody.getLongitude());
//        CoordinateConverter converter = new CoordinateConverter();
//        converter.coord(lla);
//        converter.from(CoordinateConverter.CoordType.COMMON);
//        com.baidu.mapapi.model.LatLng convertLatLng = converter.convert();
//        OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)).zIndex(4).draggable(true);
//        baiduMap.addOverlay(ooA);
//        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
//        baiduMap.animateMapStatus(u);

        if (mapView != null) {
            mapView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    onBubbleClick();
                    return false;
                }
            });
        }
    }

    @Override
    protected void onUpdateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onBubbleClick() {
        Intent intent = new Intent(context, EaseBaiduMapActivity.class);
        intent.putExtra("navi" , true) ;
        intent.putExtra("latitude", locBody.getLatitude());
        intent.putExtra("longitude", locBody.getLongitude());
        intent.putExtra("address", locBody.getAddress());
        activity.startActivity(intent);
    }

    /*
     * listener for map clicked
	 */
    protected class MapClickListener implements View.OnClickListener {
        LatLng location;
        String address;
        public MapClickListener(LatLng loc, String address) {
            location = loc;
            this.address = address;

        }
        @Override
        public void onClick(View v) {

        }
    }
}
