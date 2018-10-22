package com.fei.firstproject.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.fei.firstproject.R;
import com.fei.firstproject.widget.AppHeadView;
import com.fei.firstproject.widget.CircleStrokeView;
import com.scwang.smartrefresh.layout.util.DelayedRunable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class RunActivity extends BaseActivity {

    @BindView(R.id.swipeLayout)
    SwipeToLoadLayout swipeLayout;
    @BindView(R.id.circle_view)
    CircleStrokeView circleView;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_percent)
    TextView tvPercent;
    @BindView(R.id.ll_circle)
    LinearLayout llCircle;
    @BindView(R.id.swipe_target)
    LinearLayout swipeTarget;
    @BindView(R.id.rb_day)
    RadioButton rbDay;
    @BindView(R.id.rb_week)
    RadioButton rbWeek;
    @BindView(R.id.rb_month)
    RadioButton rbMonth;
    @BindView(R.id.chart)
    LineChartView chart;
    @BindView(R.id.right_drawer_layout)
    RelativeLayout rightDrawerLayout;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    private String[] times = {"00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00"};
    private String[] weeks = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private String[] days = {"1", "3", "5", "7", "11", "13", "15", "17", "19", "21", "23", "25", "27", "29", "31"};

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
    public int getContentViewResId() {
        return R.layout.activity_run;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initView();
        initListener();
    }

    private void initView() {
        initCircleView();
        initChart();
    }

    private void initChart() {
        List values = new ArrayList<PointValue>();//折线上的点
        values.add(new PointValue(0, 2));
        values.add(new PointValue(1, 4));
        values.add(new PointValue(2, 3));
        values.add(new PointValue(3, 3));
        values.add(new PointValue(4, 3));
        values.add(new PointValue(5, 3));
        values.add(new PointValue(6, 3));
        values.add(new PointValue(7, 3));
        Line line = new Line(values).setColor(Color.BLUE);//声明线并设置颜色
        line.setCubic(true);//设置是平滑的还是直的
        List lines = new ArrayList<Line>();
        lines.add(line);

        chart.setInteractive(true);//设置图表是可以交互的（拖拽，缩放等效果的前提）
        chart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);//设置缩放方向
        LineChartData data = new LineChartData();
        Axis axisX = new Axis();//x轴
        Axis axisY = new Axis();//y轴
        List<AxisValue> xValues = new ArrayList<>();
        for (int i = 0; i < times.length; i++) {
            xValues.add(new AxisValue(i).setLabel(times[i]));
        }
        List<AxisValue> yValues = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            yValues.add(new AxisValue(i).setValue(i));
        }
        axisY.setValues(yValues);
        axisX.setTextColor(Color.RED);
        axisX.setValues(xValues);
        axisX.setTextSize(12);
        axisY.setTextSize(12);
        axisY.setValues(yValues);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        data.setLines(lines);
        chart.setLineChartData(data);//给图表设置数据
        chart.setZoomEnabled(true);//设置是否支持缩放
        chart.setInteractive(true);//设置图表是否可以与用户互动
    }

    private void initCircleView() {
        circleView.startAnimator(tvTotal, tvPercent, 3000);
    }

    private void initListener() {
        appHeadView.setOnLeftRightClickListener(new AppHeadView.onAppHeadViewListener() {
            @Override
            public void onLeft(View view) {
                finish();
            }

            @Override
            public void onRight(View view) {
                if (drawerLayout.isDrawerOpen(Gravity.END)) {
                    drawerLayout.closeDrawer(Gravity.END);
                } else {
                    drawerLayout.openDrawer(Gravity.END);
                }
            }

            @Override
            public void onEdit(TextView v, int actionId, KeyEvent event) {

            }
        });
        if (swipeLayout != null) {
            swipeLayout.setRefreshEnabled(true);
            swipeLayout.setLoadMoreEnabled(false);
            swipeLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    initRequest();
                }
            });
        }
    }

    @Override
    public void initRequest() {
        runOnUiThread(new DelayedRunable(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 5000));
    }

}
