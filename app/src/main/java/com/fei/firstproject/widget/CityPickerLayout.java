package com.fei.firstproject.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.fei.firstproject.R;
import com.fei.firstproject.entity.CityEntity;
import com.fei.firstproject.utils.CityCodeUtils;
import com.fei.firstproject.utils.FileUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 城市Picker
 */
public class CityPickerLayout extends LinearLayout {
    /**
     * 滑动控件
     */
    private ScrollerNumberPickerView provincePicker;
    private ScrollerNumberPickerView cityPicker;
    private ScrollerNumberPickerView counyPicker;
    /**
     * 选择监听
     */
    private OnSelectingListener onSelectingListener;
    /**
     * 刷新界面
     */
    private static final int REFRESH_VIEW = 0x001;
    /**
     * 数据获取完成
     */
    private static final int FINISH_DATA_GET = 0X002;
    /**
     * 临时日期
     */
    private int tempProvinceIndex = -1;
    private int temCityIndex = -1;
    private int tempCounyIndex = -1;
    private Context context;
    private List<CityEntity> province_list = new ArrayList<CityEntity>();
    private HashMap<String, List<CityEntity>> city_map = new HashMap<String, List<CityEntity>>();
    private HashMap<String, List<CityEntity>> couny_map = new HashMap<String, List<CityEntity>>();

    private CityCodeUtils cityCodeUtil = CityCodeUtils.getSingleton();
    private String city_code_string;
    private String province_string;
    private String city_string;
    private String country_string;

    public CityPickerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public CityPickerLayout(Context context) {
        super(context);
        this.context = context;
        init();

    }

    private void init() {
        initView();
        initListener();
        initData();
    }

    private void initData() {
        // 读取城市信息string
        handler.post(new Runnable() {
            @Override
            public void run() {
                JSONParser parser = new JSONParser();
                String area_str = FileUtils.readAssets(context, "area.json");
                province_list = parser.getJSONParserResult(area_str, "area0");
                city_map = parser.getJSONParserResultArray(area_str, "area1");
                couny_map = parser.getJSONParserResultArray(area_str, "area2");
                handler.sendEmptyMessage(FINISH_DATA_GET);
            }
        });
    }

    private void initListener() {
        provincePicker.setOnSelectListener(new ScrollerNumberPickerView.OnSelectListener() {

            @Override
            public void endSelect(int id, String text) {

                if (text.equals("") || text == null)
                    return;
                if (tempProvinceIndex != id) {
                    String selectDay = cityPicker.getSelectedText();
                    if (selectDay == null || selectDay.equals(""))
                        return;
                    String selectMonth = counyPicker.getSelectedText();
                    if (selectMonth == null || selectMonth.equals(""))
                        return;
                    // 城市数组
                    cityPicker.setData(cityCodeUtil.getCity(city_map,
                            cityCodeUtil.getProvince_list_code().get(id)));
                    cityPicker.setDefault(1);
                    counyPicker.setData(cityCodeUtil.getCouny(couny_map,
                            cityCodeUtil.getCity_list_code().get(1)));
                    counyPicker.setDefault(1);
                    int lastDay = Integer.valueOf(provincePicker.getListSize());
                    if (id > lastDay) {
                        provincePicker.setDefault(lastDay - 1);
                    }
                }
                tempProvinceIndex = id;
                Message message = new Message();
                message.what = REFRESH_VIEW;
                handler.sendMessage(message);
            }

            @Override
            public void selecting(int id, String text) {

            }
        });
        cityPicker.setOnSelectListener(new ScrollerNumberPickerView.OnSelectListener() {

            @Override
            public void endSelect(int id, String text) {

                if (text.equals("") || text == null)
                    return;
                if (temCityIndex != id) {
                    String selectDay = provincePicker.getSelectedText();
                    if (selectDay == null || selectDay.equals(""))
                        return;
                    String selectMonth = counyPicker.getSelectedText();
                    if (selectMonth == null || selectMonth.equals(""))
                        return;
                    counyPicker.setData(cityCodeUtil.getCouny(couny_map,
                            cityCodeUtil.getCity_list_code().get(id)));
                    counyPicker.setDefault(1);
                    int lastDay = Integer.valueOf(cityPicker.getListSize());
                    if (id > lastDay) {
                        cityPicker.setDefault(lastDay - 1);
                    }
                }
                temCityIndex = id;
                Message message = new Message();
                message.what = REFRESH_VIEW;
                handler.sendMessage(message);
            }

            @Override
            public void selecting(int id, String text) {


            }
        });
        counyPicker.setOnSelectListener(new ScrollerNumberPickerView.OnSelectListener() {

            @Override
            public void endSelect(int id, String text) {


                if (text.equals("") || text == null)
                    return;
                if (tempCounyIndex != id) {
                    String selectDay = provincePicker.getSelectedText();
                    if (selectDay == null || selectDay.equals(""))
                        return;
                    String selectMonth = cityPicker.getSelectedText();
                    if (selectMonth == null || selectMonth.equals(""))
                        return;
                    // 城市数组
                    city_code_string = cityCodeUtil.getCouny_list_code()
                            .get(id);
                    int lastDay = Integer.valueOf(counyPicker.getListSize());
                    if (id > lastDay) {
                        counyPicker.setDefault(lastDay - 1);
                    }
                }
                tempCounyIndex = id;
                Message message = new Message();
                message.what = REFRESH_VIEW;
                handler.sendMessage(message);
            }

            @Override
            public void selecting(int id, String text) {


            }
        });
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_city_picker, this);
        provincePicker = findViewById(R.id.province);
        cityPicker = findViewById(R.id.city);
        counyPicker = findViewById(R.id.couny);
    }

    public static class JSONParser {
        public ArrayList<String> province_list_code = new ArrayList<String>();
        public ArrayList<String> city_list_code = new ArrayList<String>();

        public List<CityEntity> getJSONParserResult(String JSONString, String key) {
            List<CityEntity> list = new ArrayList<CityEntity>();
            JsonObject result = new JsonParser().parse(JSONString)
                    .getAsJsonObject().getAsJsonObject(key);

            Iterator<?> iterator = result.entrySet().iterator();
            while (iterator.hasNext()) {
                @SuppressWarnings("unchecked")
                Entry<String, JsonElement> entry = (Entry<String, JsonElement>) iterator
                        .next();
                CityEntity CityEntity = new CityEntity();

                CityEntity.setCity_name(entry.getValue().getAsString());
                CityEntity.setId(entry.getKey());
                province_list_code.add(entry.getKey());
                list.add(CityEntity);
            }
            return list;
        }

        public HashMap<String, List<CityEntity>> getJSONParserResultArray(
                String JSONString, String key) {
            HashMap<String, List<CityEntity>> hashMap = new HashMap<String, List<CityEntity>>();
            JsonObject result = new JsonParser().parse(JSONString)
                    .getAsJsonObject().getAsJsonObject(key);

            Iterator<?> iterator = result.entrySet().iterator();
            while (iterator.hasNext()) {
                @SuppressWarnings("unchecked")
                Entry<String, JsonElement> entry = (Entry<String, JsonElement>) iterator
                        .next();
                List<CityEntity> list = new ArrayList<CityEntity>();
                JsonArray array = entry.getValue().getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    CityEntity CityEntity = new CityEntity();
                    CityEntity.setCity_name(array.get(i).getAsJsonArray().get(0)
                            .getAsString());
                    CityEntity.setId(array.get(i).getAsJsonArray().get(1)
                            .getAsString());
                    city_list_code.add(array.get(i).getAsJsonArray().get(1)
                            .getAsString());
                    list.add(CityEntity);
                }
                hashMap.put(entry.getKey(), list);
            }
            return hashMap;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_VIEW:
                    if (onSelectingListener != null)
                        onSelectingListener.selected(true);
                    break;
                case FINISH_DATA_GET:
                    provincePicker.setData(cityCodeUtil.getProvince(province_list));
                    provincePicker.setDefault(1);
                    cityPicker.setData(cityCodeUtil.getCity(city_map, cityCodeUtil
                            .getProvince_list_code().get(1)));
                    cityPicker.setDefault(1);
                    counyPicker.setData(cityCodeUtil.getCouny(couny_map, cityCodeUtil
                            .getCity_list_code().get(1)));
                    counyPicker.setDefault(1);
                    break;
                default:
                    break;
            }
        }

    };

    public void setOnSelectingListener(OnSelectingListener onSelectingListener) {
        this.onSelectingListener = onSelectingListener;
    }

    public String getCity_code_string() {
        return city_code_string;
    }

    /**
     * 获取省
     *
     * @return
     */
    public String getProvince_string() {
        province_string = provincePicker.getSelectedText();

        return province_string;
    }

    /*获取市
     *
     * */
    public String getCity_string() {
        city_string = cityPicker.getSelectedText();
        return city_string;
    }

    /**
     * 获取区
     *
     * @author Administrator
     */
    public String getCountry_string() {
        country_string = counyPicker.getSelectedText();
        return country_string;
    }


    public interface OnSelectingListener {

        void selected(boolean selected);
    }
}
