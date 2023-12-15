package com.fei.firstproject.activity;

import android.os.Bundle;

import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.databinding.ActivityMyAskBinding;
import com.fei.firstproject.entity.ObjectEntity;

import java.util.ArrayList;
import java.util.List;


public class MyAskActivity extends BaseProjectActivity<EmptyViewModel, ActivityMyAskBinding> {


    public String[] groupString = {"射手", "辅助", "坦克", "法师"};
    public String[][] childString = {
            {"孙尚香", "后羿", "马可波罗", "狄仁杰"},
            {"孙膑", "蔡文姬", "鬼谷子", "杨玉环"},
            {"张飞", "廉颇", "牛魔", "项羽"},
            {"诸葛亮", "王昭君", "安琪拉", "干将"}

    };

    @Override
    public void permissionsDeniedCallBack(int requestCode) {

    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {

    }

    @Override
    public void permissionDialogDismiss(int requestCode) {

    }

    @Override
    public void initTitle() {
        setBackTitle(getString(R.string.my_ask));
    }


    private void initData() {

        List<ObjectEntity> objectEntityList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ObjectEntity objectEntity = new ObjectEntity();
            objectEntity.setName(groupString[i]);
            List<ObjectEntity> objectEntityList1 = new ArrayList<>();
            for (int j = 0; j < childString[i].length; j++) {
                ObjectEntity objectEntity1 = new ObjectEntity();
                objectEntity1.setName(childString[i][j]);
                objectEntity.setObjects(null);
                objectEntityList1.add(objectEntity1);
            }
            objectEntity.setObjects(objectEntityList);
        }
    }

    @Override
    public void initRequest() {

    }

    @Override
    public void createObserver() {

    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        initData();
    }
}
