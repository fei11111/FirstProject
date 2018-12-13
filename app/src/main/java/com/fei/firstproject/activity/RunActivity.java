package com.fei.firstproject.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @BindView(R.id.rg_tab)
    RadioGroup rgTab;

    private String[] times = {"00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00"};
    private String[] weeks = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private String[] days = {"1", "3", "5", "7", "11", "13", "15", "17", "19", "21", "23", "25", "27", "29", "31"};

    private List values = new ArrayList<PointValue>();//折线上的点
    private List<AxisValue> xValues = new ArrayList<>();//x轴值
    private List<AxisValue> yValues = new ArrayList<>();//y轴值
    private LineChartData data = new LineChartData();
    private Random ra = new Random();
    ;

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

    @Override
    public void init(Bundle savedInstanceState) {
        initView();
        initListener();
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
        chart.setInteractive(true);//设置图表是可以交互的（拖拽，缩放等效果的前提）
        chart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);//设置缩放方向
        Axis axisX = new Axis();//x轴
        Axis axisY = new Axis();//y轴
        axisX.setTextSize(12);
        axisY.setTextSize(12);
        axisX.setValues(xValues);
        axisY.setValues(yValues);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        data.setLines(lines);
        chart.setZoomEnabled(true);//设置是否支持缩放
        chart.setInteractive(true);//设置图表是否可以与用户互动
        chart.setLineChartData(data);//给图表设置数据
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

        chart.setLineChartData(data);//给图表设置数据
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

        chart.setLineChartData(data);//给图表设置数据
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

        chart.setLineChartData(data);//给图表设置数据
    }

    private void initCircleView() {
        circleView.startAnimator(3000, tvTotal, tvPercent, 3000);
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
        rgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
                swipeLayout.setRefreshing(false);
            }
        }, 5000));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
