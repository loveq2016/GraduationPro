package app.logic.activity.notice;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.ql.activity.customtitle.ActActivity;

import java.util.ArrayList;

import app.yy.geju.R;

/**
 * Created by Administrator on 2017/4/20 0020.
 */

public class SeeImageActivity extends ActActivity {

    public static final String DATAS="DATAS";
    private ViewPager viewPager ;
    private ArrayList<String> datas ;
    private SeeImagePagerAdapter seeImagePagerAdapter ;
    private ArrayList<ImageView> imageViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView( R.layout.see_image_activity);
        datas = getIntent().getStringArrayListExtra(DATAS);
        if(null == datas){
            datas = new ArrayList<>();
        }
        for(int i = 0 ; i< datas.size() ; i++){
            ImageView imageView = new ImageView(this);
            Picasso.with(this).load(datas.get(i)).fit().centerInside().into(imageView);
            imageViews.add(imageView);
        }
        viewPager = (ViewPager) findViewById( R.id.see_image_viewpager);
        seeImagePagerAdapter = new SeeImagePagerAdapter();
        viewPager.setAdapter(seeImagePagerAdapter);
    }


    /**
     * 适配器
     */
    class SeeImagePagerAdapter extends PagerAdapter{

        public SeeImagePagerAdapter(){

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews.get(position) );
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageViews.get(position));
            return imageViews.get(position);
        }

        @Override
        public int getCount() {
            return imageViews!=null?imageViews.size():0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object ;
        }
    }
}
