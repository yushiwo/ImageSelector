package com.yongchun.multiimageselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yongchun.library.view.ImagePreviewActivity;
import com.yongchun.library.view.ImageSelectorActivity;
import com.yongchun.multiimageselector.adapter.GridAdapter;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by dee on 15/11/27.
 */
public class SelectResultActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGES = "extraImages";
    private RecyclerView resultRecyclerView;
    private GridAdapter mGridAdapter;
    private Button mAddMoreButton;

    private ArrayList<String> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initView();

        setListeners();
    }

    private void setListeners() {
        mAddMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageSelectorActivity.start(SelectResultActivity.this, 9, ImageSelectorActivity.MODE_MULTIPLE, true, true, false, images);
            }
        });
    }

    public void initView() {
        images = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_IMAGES);
        mAddMoreButton = (Button)findViewById(R.id.add_more_btn);

        resultRecyclerView = (RecyclerView) findViewById(R.id.result_recycler);
        resultRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

//        if (images.size() == 1) {
//            resultRecyclerView.setVisibility(View.GONE);
//            Glide.with(SelectResultActivity.this)
//                    .load(new File(images.get(0)))
//                    .into(singleImageView);
//        } else {
//            singleImageView.setVisibility(View.GONE);
//            mGridAdapter = new GridAdapter(this, images);
//            resultRecyclerView.setAdapter(mGridAdapter);
//
//            mGridAdapter.setOnItemClickListener(new GridAdapter.OnItemClickListener() {
//                @Override
//                public void onItemClick(int position) {
//                    Log.d("zr", "click " + position);
//                }
//            });
//        }

//        singleImageView.setVisibility(View.GONE);
//        mGridAdapter = new GridAdapter(this, images);
//        resultRecyclerView.setAdapter(mGridAdapter);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE){
            images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            mGridAdapter = new GridAdapter(this, images);
            resultRecyclerView.setAdapter(mGridAdapter);

            mGridAdapter.setOnItemClickListener(new GridAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Log.d("zr", "click " + position);

                    ImagePreviewActivity.startDeletePreview(SelectResultActivity.this, images, 9, position);
                }
            });
        }else if(resultCode == RESULT_OK && requestCode == ImagePreviewActivity.REQUEST_PREVIEW){
            images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            mGridAdapter = new GridAdapter(this, images);
            resultRecyclerView.setAdapter(mGridAdapter);

            mGridAdapter.setOnItemClickListener(new GridAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Log.d("zr", "click " + position);

                    ImagePreviewActivity.startDeletePreview(SelectResultActivity.this, images, 9, position);
                }
            });
        }
    }

}
