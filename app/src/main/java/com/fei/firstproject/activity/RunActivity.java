package com.fei.firstproject.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.view.GravityCompat;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.databinding.ActivityRunBinding;
import com.fei.firstproject.widget.AppHeadView;
import com.scwang.smartrefresh.layout.util.DelayedRunable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;

public class RunActivity extends BaseProjectActivity<EmptyViewModel, ActivityRunBinding> {

    private String[] times = {"00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00"};
    private String[] weeks = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private String[] days = {"1", "3", "5", "7", "11", "13", "15", "17", "19", "21", "23", "25", "27", "29", "31"};

    private List values = new ArrayList<PointValue>();//折线上的点
    private List<AxisValue> xValues = new ArrayList<>();//x轴值
    private List<AxisValue> yValues = new ArrayList<>();//y轴值
    private LineChartData data = new LineChartData();
    private Random ra = new Random();

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
        appHeadView.setFlHeadLeftPadding(getResources().getDimensionPixelSize(R.dimen.size_10));
        appHeadView.setLeftStyle(AppHeadView.IMAGE);
        appHeadView.setFlHeadLeftVisible(View.VISIBLE);
        appHeadView.setLeftDrawable(R.drawable.selector_head_left_arrow);
        appHeadView.setMiddleStyle(AppHeadView.TEXT);
        appHeadView.setMiddleText(getString(R.string.run));
        appHeadView.setFlHeadRightVisible(View.VISIBLE);
        appHeadView.setRightStyle(AppHeadView.IMAGE);
        appHeadView.setRightDrawable(R.drawable.selector_ic_setting);
    }


    private void initView() {
        initCircleView();
        initChart();
        initDayChart();
    }

    private void initChart() {
        Line line = new Line(values).setColor(getResources().getColor(R.color.colorPrimary));//声明线并设置颜色
        line.setCubic(true);//设置是平滑的还是直的
        List lines = new ArrayList<Line>();
        lines.add(line);
        mChildBinding.chart.setInteractive(true);//设置图表是可以交互的（拖拽，缩放等效果的前提）
        mChildBinding.chart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);//设置缩放方向
        Axis axisX = new Axis();//x轴
        Axis axisY = new Axis();//y轴
        axisX.setTextSize(12);
        axisY.setTextSize(12);
        axisX.setValues(xValues);
        axisY.setValues(yValues);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        data.setLines(lines);
        mChildBinding.chart.setZoomEnabled(true);//设置是否支持缩放
        mChildBinding.chart.setInteractive(true);//设置图表是否可以与用户互动
        mChildBinding.chart.setLineChartData(data);//给图表设置数据
    }

    private void initDayChart() {

        values.clear();
        xValues.clear();
        yValues.clear();

        for (int i = 0; i < times.length; i++) {
            xValues.add(new AxisValue(12 / times.length * i).setLabel(times[i]));
            values.add(new PointValue(i, ra.nextInt(10) * (i + 1)));
        }

        for (int i = 0; i < times.length; i++) {
            yValues.add(new AxisValue(i * (100 / times.length)).setLabel(i * (100 / times.length) + ""));
        }

        mChildBinding.chart.setLineChartData(data);//给图表设置数据
    }

    private void initWeekChart() {

        values.clear();
        xValues.clear();
        yValues.clear();

        for (int i = 0; i < weeks.length; i++) {
            xValues.add(new AxisValue(12 / weeks.length * i).setLabel(weeks[i]));
            values.add(new PointValue(i, ra.nextInt(10) * (i + 1)));
        }

        for (int i = 0; i < weeks.length; i++) {
            yValues.add(new AxisValue(i * (100 / weeks.length)).setLabel(i * (100 / weeks.length) + ""));
        }

        mChildBinding.chart.setLineChartData(data);//给图表设置数据
    }

    private void initMonthChart() {

        values.clear();
        xValues.clear();
        yValues.clear();

        for (int i = 0; i < days.length; i++) {
            xValues.add(new AxisValue(12 / days.length * i).setValue(Float.parseFloat(days[i])));
            values.add(new PointValue(i, ra.nextInt(10) * (i + 1)));
        }

        for (int i = 0; i < days.length; i++) {
            yValues.add(new AxisValue(i * (100 / days.length)).setLabel(i * (100 / days.length) + ""));
        }

        mChildBinding.chart.setLineChartData(data);//给图表设置数据
    }

    private void initCircleView() {
        mChildBinding.circleView.startAnimator(3000, mChildBinding.tvTotal, mChildBinding.tvPercent, 3000);
    }

    private void initListener() {
        appHeadView.setOnLeftRightClickListener(new AppHeadView.onAppHeadViewListener() {
            @Override
            public void onLeft(View view) {
                finish();
            }

            @Override
            public void onRight(View view) {
                if (mChildBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mChildBinding.drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    mChildBinding.drawerLayout.openDrawer(GravityCompat.END);
                }
            }

            @Override
            public void onEdit(TextView v, int actionId, KeyEvent event) {

            }
        });
        if (mChildBinding.swipeLayout != null) {
            mChildBinding.swipeLayout.setRefreshEnabled(true);
            mChildBinding.swipeLayout.setLoadMoreEnabled(false);
            mChildBinding.swipeLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    initRequest();
                }
            });
        }
        mChildBinding.rgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_day:
                        initDayChart();
                        break;
                    case R.id.rb_week:
                        initWeekChart();
                        break;
                    case R.id.rb_month:
                        initMonthChart();
                        break;
                }
            }
        });
    }

    @Override
    public void initRequest() {
        runOnUiThread(new DelayedRunable(new Runnable() {
            @Override
            public void run() {
                mChildBinding.swipeLayout.setRefreshing(false);
            }
        }, 5000));
    }

    @Override
    public void createObserver() {

    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        initView();
        initListener();
    }
}
