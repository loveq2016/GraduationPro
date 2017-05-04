package org.ql.views.listview;

import org.ql.bundle.R;
import org.ql.utils.debug.QLLog;
import org.ql.views.listview.QLXListView.IXListViewListener;
import org.ql.views.listview.dslv.DragSortListView;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

public class QLXDragSortListView extends DragSortListView implements OnScrollListener{

	private final String tag = "";
	private BaseAdapter adapter;
	private boolean hideUpdateTime = true;
	private float mLastY = -1; // save event y
	private Scroller mScroller; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	// the interface to trigger refresh and load more.
	private IXListViewListener mListViewListener;

	// -- header view
	private QLXListViewHeader mHeaderView;
	// header view content, use it to calculate the Header's height. And hide it
	// when disable pull refresh.
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderViewHeight; // header view's height
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false; // is refreashing.

	// -- footer view
	private QLXListViewFooter mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady = false;
	
	// total list items, used to detect is at the bottom of listview.
	private int mTotalItemCount;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400; // scroll back duration
	private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px at bottom, trigger load more.
	private final static float OFFSET_RADIO = 1.8f; // support iOS like pull feature.

//	/**
//	 * @param context
//	 */
//	public QLXDragSortListView(Context context) {
//		super(context);
//		initWithContext(context);
//	}
//
//	public QLXDragSortListView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		initWithContext(context);
//	}
//
//	public QLXDragSortListView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		initWithContext(context);
//	}
//	
	public QLXDragSortListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}
	
	/**
	 * 是否隐藏更新时间
	 * @param hideUpdateTime
	 */
	public void setHideUpdateTimeTextView(boolean hideUpdateTime){
		this.hideUpdateTime = hideUpdateTime;
	}

	private void initWithContext(Context context) {
		if(Build.VERSION.SDK_INT >= 9){
			setOverscrollFooter(null);
		}
		mScroller = new Scroller(context, new DecelerateInterpolator());
		// XListView need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		super.setOnScrollListener(this);

		// init header view
		mHeaderView = new QLXListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView.findViewById(R.id.xlistview_header_content);
		mHeaderTimeView = (TextView) mHeaderView.findViewById(R.id.xlistview_header_time);
		
		if(hideUpdateTime){
			mHeaderTimeView.setVisibility(View.GONE);
		}
		
		addHeaderView(mHeaderView);
		

		// init footer view
		mFooterView = new QLXListViewFooter(context);

		// init header height
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
				});
	}

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
		// make sure XListViewFooter is the last footer view, and only add once.
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}
		super.setAdapter(adapter);
	}

	/**
	 * enable or disable pull down refresh feature.
	 * 
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // disable, hide the content
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置是否允许上拉加载更多,当没有更过会提示“没有更过了”,不会removeFooterView<br>
	 * 必须在notifyDataSetChanged后调用
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		setPullLoadEnable(enable, null,false);
	}
	
	/**
	 * 设置是否允许上拉加载更多
	 * @param enable
	 * @param removeView 是否removeFooterView
	 */
	public void setPullLoadEnable(boolean enable,boolean removeView) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			if(mIsFooterReady){
				removeFooterView(mFooterView);
				mIsFooterReady = false;
			}
			mFooterView.hide();
			mFooterView.setOnClickListener(null);
			try {	
				if(removeView){					
					removeFooterView(mFooterView);
				}
			} catch (Exception e) {
			}
		} else {
			mPullLoading = false;
			if(!mIsFooterReady){
				addFooterView(mFooterView);
				mIsFooterReady = true;
			}
			mFooterView.show();
			mFooterView.setState(QLXListViewFooter.STATE_NORMAL);
			// both "pull up" and "click" will invoke load more.
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}
	
	/**
	 * 设置是否允许上拉加载更多
	 * @param enable
	 * @param msg 没有更多数据的提示信息，如果传null则显示“没有更多了”
	 */
	public void setPullLoadEnable(boolean enable,String msg,boolean removeView) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			if(null!=adapter){
				adapter.notifyDataSetChanged();
			}
			//anan-mark
//			if(getAdapter()!=null){
//				QLLog.e("getAdapter().getCount()--> "+getAdapter().getCount());
//			}else{
//				QLLog.e("getAdapter()--> null");
//			}
			if(getAdapter()==null||getAdapter().getCount()<3){				
				if(mIsFooterReady){
					removeFooterView(mFooterView);
					mIsFooterReady = false;
				}
				mFooterView.hide();
				mFooterView.setOnClickListener(null);
				try {	
					if(removeView){					
						removeFooterView(mFooterView);
					}
				} catch (Exception e) {
				}
			}else{			
				setPullLoadEnable(true, false);
				mEnablePullLoad = false;
				mFooterView.setOnClickListener(null);
				mFooterView.mHintView.setText(msg==null?"没有更多了":msg);
			}
			
		} else {
			mPullLoading = false;
			if(!mIsFooterReady){
				addFooterView(mFooterView);
				mIsFooterReady = true;
			}
			mFooterView.show();
			mFooterView.setState(QLXListViewFooter.STATE_NORMAL);
			// both "pull up" and "click" will invoke load more.
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		QLLog.e("t", "XListView stopRefresh mPullRefreshing = "+mPullRefreshing);
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
		}
	}

	/**
	 * stop load more, reset footer view.
	 */
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(QLXListViewFooter.STATE_NORMAL);
		}
	}

	/**
	 * set last refresh time
	 * 
	 * @param time
	 */
	public void setRefreshTime(String time) {
		mHeaderTimeView.setText(time);
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	private void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) delta + mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				mHeaderView.setState(QLXListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(QLXListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0); // scroll to top each time
	}

	/**
	 * reset header view's height.
	 */
	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		if (mPullRefreshing && height == 0){ // not visible.
			QLLog.v("QLXlistView", "resetHeaderHeight() : mHeaderView not visible.");
			return;
		}
		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			QLLog.v("QLXlistView", "resetHeaderHeight() :  refreshing and header isn't shown fully. do nothing.");
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && height > mHeaderViewHeight) {
			QLLog.v("QLXlistView", "resetHeaderHeight() : is refreshing, just scroll back to show all the header.");
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		
		QLLog.v(tag, "resetHeaderHeight() : height = "+height+" ,finalHeight = "+finalHeight);
		mScroller.startScroll(0, height, 0, finalHeight - height,SCROLL_DURATION);
		// trigger computeScroll
		invalidate();
	}

	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load more.
				mFooterView.setState(QLXListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(QLXListViewFooter.STATE_NORMAL);
			}
		}
		if(mEnablePullLoad)
			mFooterView.setBottomMargin(height);
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		QLLog.v("QLXListView", "resetFooterHeight - bottomMargin="+bottomMargin+" , count : "+getFooterViewsCount());
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,SCROLL_DURATION);
			invalidate();
		}
	}

	/**
	 * 代码调用下拉刷新
	 */
	public void startRefresh(){
		QLLog.e("t", "startRefresh mHeaderViewHeight = "+mHeaderViewHeight);
		if(mHeaderViewHeight <=0){
			mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver().removeGlobalOnLayoutListener(this);
						startRefresh2();
					}
				});
		}else{
			startRefresh2();
		}
	}
	/**
	 * 代码调用下拉刷新
	 */
	public void startRefreshName(String name){
		QLLog.e("t", "startRefreshName mHeaderViewHeight = "+mHeaderViewHeight+" ,name = "+name);
		if(mHeaderViewHeight <=0){
			mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							mHeaderViewHeight = mHeaderViewContent.getHeight();
							getViewTreeObserver().removeGlobalOnLayoutListener(this);
							startRefresh2();
						}
					});
		}else{
			startRefresh2();
		}
	}
	
	private void startRefresh2(){
		if(mPullRefreshing)
			return;
		QLLog.e(tag, "startRefresh2 mHeaderViewHeight = "+mHeaderViewHeight);
		updateHeaderHeight(mHeaderViewHeight);
		invokeOnScrolling();
		
		// invoke refresh
		if (mEnablePullRefresh) {
			mPullRefreshing = true;
			mHeaderView.setState(QLXListViewHeader.STATE_REFRESHING);
			if (mListViewListener != null) {
				mListViewListener.onRefresh();
			}
		}
		resetHeaderHeight();
	}
	
	private void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(QLXListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
		resetFooterHeight();
	}
	
	

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0 && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				// the first item is showing, header has shown or pull down.
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1 && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				// last item, already pulled up or want to pull up.
				updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			break;
		default:
			mLastY = -1; // reset
			if (getFirstVisiblePosition() == 0) {
				// invoke refresh
				if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
					mPullRefreshing = true;
					mHeaderView.setState(QLXListViewHeader.STATE_REFRESHING);
					if (mListViewListener != null) {
						mListViewListener.onRefresh();
					}
				}
				resetHeaderHeight();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				// invoke load more.
				if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
					mPullLoading = true;
					mFooterView.setState(QLXListViewFooter.STATE_LOADING);
					if (mListViewListener != null) {
						mListViewListener.onLoadMore();
					}
				}
				resetFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// send to user's listener
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	/**
	 * you can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

//	/**
//	 * implements this interface to get refresh/load more event.
//	 */
//	public interface IXListViewListener {
//		public void onRefresh();
//
//		public void onLoadMore();
//	}
}
