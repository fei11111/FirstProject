package com.fei.firstproject.fragment.manager;

import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

/*
 * 管理所有的Fragment，只能通过这个类去构造Fragment
 * 
 */

public class FragmentInstanceManager {

    private FragmentInstanceManager() {
    }

    private static FragmentInstanceManager sInstance = new FragmentInstanceManager();

    public static FragmentInstanceManager getInstance() {
        return sInstance;
    }

    //存储fargment实例
    private Map<String, Fragment> fragments = new HashMap<String, Fragment>();


    public void setfragments(Map<String, Fragment> fragments) {
        this.fragments = fragments;
    }

    //对外提供获取Fragmnet市里的方法
    public Fragment getFragmet(Class<? extends Fragment> clazz) {

        String key = clazz.getSimpleName();
        Fragment fragment = fragments.get(key);

        if (fragment == null) {
            synchronized (FragmentInstanceManager.class) {
                try {
                    if (fragment == null) {
                        fragment = clazz.newInstance();
                        fragments.put(key, fragment);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        return fragment;
    }


}
