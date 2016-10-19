package com.yongchun.library.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yongchun.library.R;
import com.yongchun.library.adapter.DeletePreviewPagerAdapter;
import com.yongchun.library.adapter.SimplePreviewPagerAdapter;
import com.yongchun.library.model.LocalMedia;
import com.yongchun.library.widget.PreviewViewPager;

import java.util.ArrayList;
import java.util.List;

import static com.yongchun.library.view.ImageSelectorActivity.REQUEST_OUTPUT;

/**
 * Created by dee on 15/11/24.
 */
public class ImagePreviewActivity extends AppCompatActivity {
    public static final int REQUEST_PREVIEW = 68;
    public static final String EXTRA_PREVIEW_LIST = "previewList";
    public static final String EXTRA_PREVIEW_SELECT_LIST = "previewSelectList";
    public static final String EXTRA_MAX_SELECT_NUM = "maxSelectNum";
    public static final String EXTRA_POSITION = "position";
    public static final String  EXTRA_PREVIEW_MODE = "previewMode";

    public static final String OUTPUT_LIST = "outputList";
    public static final String OUTPUT_ISDONE = "isDone";


    private RelativeLayout mBottomLayout;
    private CheckBox checkboxSelect;
    private PreviewViewPager viewPager;
    private SimplePreviewPagerAdapter mSimpleAdapter;
    private DeletePreviewPagerAdapter mDeleteAdapter;
    private RelativeLayout mTitleLayout;
    private ImageButton mBackImageButton;
    private TextView mTitleTextView;
    private LinearLayout mDoneLayout;
    private TextView mDoneNumTextView;

    private ImageButton mDeleteButton;



    private int position;
    private int maxSelectNum;
    private int previewMode; //预览的两种模式,可删除或者不可删除,m默认为0,不可删除
    /** 所有的图片列表 */
    private ArrayList<LocalMedia> images = new ArrayList<>();
    /** 选中的图片列表 */
    private ArrayList<LocalMedia> selectImages = new ArrayList<>();


    private boolean isShowBar = true;


    /**
     *
     * @param context
     * @param images
     * @param selectImages
     * @param maxSelectNum
     * @param position
     * @param mode 预览的类型,是否可删除预览的图片
     */
    public static void startPreview(Activity context, List<LocalMedia> images, List<LocalMedia> selectImages, int maxSelectNum, int position, int mode) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(EXTRA_PREVIEW_LIST, (ArrayList) images);
        intent.putExtra(EXTRA_PREVIEW_SELECT_LIST, (ArrayList) selectImages);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum);
        intent.putExtra(EXTRA_PREVIEW_MODE, mode);
        context.startActivityForResult(intent, REQUEST_PREVIEW);
    }

    public static void startDeletePreview(Activity context, ArrayList<String> imageList, int maxSelectNum, int position) {
        ArrayList<LocalMedia> holeImages = new ArrayList<>();
        ArrayList<LocalMedia> selectedImages = new ArrayList<>();
        for(int i = 0; i < imageList.size(); i ++){
            LocalMedia media = new LocalMedia(imageList.get(i), 0, 0);
            holeImages.add(media);
            selectedImages.add(media);
        }
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(EXTRA_PREVIEW_LIST, (ArrayList) holeImages);
        intent.putExtra(EXTRA_PREVIEW_SELECT_LIST, (ArrayList) selectedImages);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum);
        intent.putExtra(EXTRA_PREVIEW_MODE, 1);
        context.startActivityForResult(intent, REQUEST_PREVIEW);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initView();
        registerListener();
    }

    public void initView() {
        images = (ArrayList<LocalMedia>) getIntent().getSerializableExtra(EXTRA_PREVIEW_LIST);
        selectImages = (ArrayList<LocalMedia>) getIntent().getSerializableExtra(EXTRA_PREVIEW_SELECT_LIST);
        maxSelectNum = getIntent().getIntExtra(EXTRA_MAX_SELECT_NUM, 9);
        position = getIntent().getIntExtra(EXTRA_POSITION, 1);
        previewMode = getIntent().getIntExtra(EXTRA_PREVIEW_MODE, 0);

        mBottomLayout = (RelativeLayout) findViewById(R.id.select_bar_layout);

        mTitleLayout = (RelativeLayout)findViewById(R.id.layout_title);
        mBackImageButton = (ImageButton)findViewById(R.id.select_back_btn);
        mTitleTextView = (TextView)findViewById(R.id.title_text);
        mDoneLayout = (LinearLayout)findViewById(R.id.done_layout);
        mDoneNumTextView = (TextView)findViewById(R.id.done_num_text);

        mTitleTextView.setText(position + 1 + "/" + images.size());

        onSelectNumChange();

        checkboxSelect = (CheckBox) findViewById(R.id.checkbox_select);
        onImageSwitch(position);


        mSimpleAdapter = new SimplePreviewPagerAdapter(getSupportFragmentManager(), images);
        mDeleteAdapter = new DeletePreviewPagerAdapter(getSupportFragmentManager(), images);

        viewPager = (PreviewViewPager) findViewById(R.id.preview_pager);

        mDeleteButton = (ImageButton) findViewById(R.id.btn_delete);

        if(previewMode == 1){
            mDeleteButton.setVisibility(View.VISIBLE);
            checkboxSelect.setVisibility(View.GONE);
            mBottomLayout.setVisibility(View.GONE);
            viewPager.setAdapter(mDeleteAdapter);
            viewPager.setCurrentItem(position);
        }else{
            mDeleteButton.setVisibility(View.GONE);
            checkboxSelect.setVisibility(View.VISIBLE);
            mBottomLayout.setVisibility(View.VISIBLE);
            viewPager.setAdapter(mSimpleAdapter);
            viewPager.setCurrentItem(position);
        }
    }

    public void registerListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mTitleTextView.setText(position + 1 + "/" + images.size());
                onImageSwitch(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mBackImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(previewMode == 0){
                    onDoneClick(false);
                }else{
                    onResult(selectImages);
                }
            }
        });

        checkboxSelect.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void onClick(View v) {
                boolean isChecked = checkboxSelect.isChecked();
                if (selectImages.size() >= maxSelectNum && isChecked) {
                    Toast.makeText(ImagePreviewActivity.this, getString(R.string.message_max_num, maxSelectNum), Toast.LENGTH_LONG).show();
                    checkboxSelect.setChecked(false);
                    return;
                }
                LocalMedia image = images.get(viewPager.getCurrentItem());
                if (isChecked) {
                    selectImages.add(image);
                } else {
                    for (LocalMedia media : selectImages) {
                        if (media.getPath().equals(image.getPath())) {
                            selectImages.remove(media);
                            break;
                        }
                    }
                }
                onSelectNumChange();
            }
        });

        mDoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneClick(true);
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPager.getCurrentItem();
                selectImages.remove(position);
                images.remove(position);
                mDeleteAdapter.updateData(selectImages);

                onSelectNumChange();
                mTitleTextView.setText(viewPager.getCurrentItem() + 1 + "/" + images.size());
                // 全部删光,关闭预览界面
                if(selectImages.size() <= 0){
                    onResult(selectImages);
                }
            }
        });
    }

    @SuppressLint("StringFormatMatches")
    public void onSelectNumChange() {
        boolean enable = selectImages.size() != 0;
        mDoneLayout.setEnabled(enable);
        if (enable) {
            mDoneNumTextView.setVisibility(View.VISIBLE);
            mDoneNumTextView.setText(String.valueOf(selectImages.size()));
        } else {
            mDoneNumTextView.setVisibility(View.GONE);
        }
    }

    /**
     * 切换图片的时候判断这张图片是否被选中
     * @param position
     */
    public void onImageSwitch(int position) {
        checkboxSelect.setChecked(isSelected(images.get(position)));
    }

    public boolean isSelected(LocalMedia image) {
        for (LocalMedia media : selectImages) {
            if (media.getPath().equals(image.getPath())) {
                return true;
            }
        }
        return false;
    }

    public void switchBarVisibility() {
        if(previewMode == 1){
            mTitleLayout.setVisibility(isShowBar ? View.GONE : View.VISIBLE);
        }else{
            mTitleLayout.setVisibility(isShowBar ? View.GONE : View.VISIBLE);
            mBottomLayout.setVisibility(isShowBar ? View.GONE : View.VISIBLE);
        }
        isShowBar = !isShowBar;
    }
    public void onDoneClick(boolean isDone){
        Intent intent = new Intent();
        intent.putExtra(OUTPUT_LIST,(ArrayList)selectImages);
        intent.putExtra(OUTPUT_ISDONE,isDone);
        setResult(RESULT_OK,intent);
        finish();
    }

    public void onResult(ArrayList<LocalMedia> medias) {
        ArrayList<String> images = new ArrayList<>();
        for (LocalMedia media : medias) {
            images.add(media.getPath());
        }
        setResult(RESULT_OK, new Intent().putStringArrayListExtra(REQUEST_OUTPUT, images));
        finish();
    }
}
