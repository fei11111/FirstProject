package com.fei.firstproject.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {

	private static final String TAG = "FlowLayout";

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected LayoutParams generateLayoutParams(
			LayoutParams p) {
		return new MarginLayoutParams(p);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new MarginLayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
	}

	/**
	 * 璐熻矗璁剧疆瀛愭帶浠剁殑娴嬮噺妯″紡鍜屽ぇ灏� 鏍规嵁鎵�鏈夊瓙鎺т欢璁剧疆鑷繁鐨勫鍜岄珮
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 鑾峰緱瀹冪殑鐖跺鍣ㄤ负瀹冭缃殑娴嬮噺妯″紡鍜屽ぇ灏�
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

		Log.e(TAG, sizeWidth + "," + sizeHeight);

		// 濡傛灉鏄痺arp_content鎯呭喌涓嬶紝璁板綍瀹藉拰楂�
		int width = 0;
		int height = 0;
		/**
		 * 璁板綍姣忎竴琛岀殑瀹藉害锛寃idth涓嶆柇鍙栨渶澶у搴�
		 */
		int lineWidth = 0;
		/**
		 * 姣忎竴琛岀殑楂樺害锛岀疮鍔犺嚦height
		 */
		int lineHeight = 0;

		int cCount = getChildCount();

		// 閬嶅巻姣忎釜瀛愬厓绱�
		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			// 娴嬮噺姣忎竴涓猚hild鐨勫鍜岄珮
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			// 寰楀埌child鐨刲p
			MarginLayoutParams lp = (MarginLayoutParams) child
					.getLayoutParams();
			// 褰撳墠瀛愮┖闂村疄闄呭崰鎹殑瀹藉害
			int childWidth = child.getMeasuredWidth() + lp.leftMargin
					+ lp.rightMargin;
			// 褰撳墠瀛愮┖闂村疄闄呭崰鎹殑楂樺害
			int childHeight = child.getMeasuredHeight() + lp.topMargin
					+ lp.bottomMargin;
			/**
			 * 濡傛灉鍔犲叆褰撳墠child锛屽垯瓒呭嚭鏈�澶у搴︼紝鍒欑殑鍒扮洰鍓嶆渶澶у搴︾粰width锛岀被鍔爃eight
			 * 鐒跺悗寮�鍚柊琛�
			 */
			if (lineWidth + childWidth > sizeWidth) {
				width = Math.max(lineWidth, childWidth);// 鍙栨渶澶х殑
				lineWidth = childWidth; // 閲嶆柊寮�鍚柊琛岋紝寮�濮嬭褰�
				// 鍙犲姞褰撳墠楂樺害锛�
				height += lineHeight;
				// 寮�鍚褰曚笅涓�琛岀殑楂樺害
				lineHeight = childHeight;
			} else
			// 鍚﹀垯绱姞鍊糽ineWidth,lineHeight鍙栨渶澶ч珮搴�
			{
				lineWidth += childWidth;
				lineHeight = Math.max(lineHeight, childHeight);
			}
			// 濡傛灉鏄渶鍚庝竴涓紝鍒欏皢褰撳墠璁板綍鐨勬渶澶у搴﹀拰褰撳墠lineWidth鍋氭瘮杈�
			if (i == cCount - 1) {
				width = Math.max(width, lineWidth);
				height += lineHeight;
			}

		}
		setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth
				: width, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight
				: height);

	}

	/**
	 * 瀛樺偍鎵�鏈夌殑View锛屾寜琛岃褰�
	 */
	private List<List<View>> mAllViews = new ArrayList<List<View>>();
	/**
	 * 璁板綍姣忎竴琛岀殑鏈�澶ч珮搴�
	 */
	private List<Integer> mLineHeight = new ArrayList<Integer>();

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mAllViews.clear();
		mLineHeight.clear();

		int width = getWidth();

		int lineWidth = 0;
		int lineHeight = 0;
		// 瀛樺偍姣忎竴琛屾墍鏈夌殑childView
		List<View> lineViews = new ArrayList<View>();
		int cCount = getChildCount();
		// 閬嶅巻鎵�鏈夌殑瀛╁瓙
		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			MarginLayoutParams lp = (MarginLayoutParams) child
					.getLayoutParams();
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();

			// 濡傛灉宸茬粡闇�瑕佹崲琛�
			if (childWidth + lp.leftMargin + lp.rightMargin + lineWidth > width) {
				// 璁板綍杩欎竴琛屾墍鏈夌殑View浠ュ強鏈�澶ч珮搴�
				mLineHeight.add(lineHeight);
				// 灏嗗綋鍓嶈鐨刢hildView淇濆瓨锛岀劧鍚庡紑鍚柊鐨凙rrayList淇濆瓨涓嬩竴琛岀殑childView
				mAllViews.add(lineViews);
				lineWidth = 0;// 閲嶇疆琛屽
				lineViews = new ArrayList<View>();
			}
			/**
			 * 濡傛灉涓嶉渶瑕佹崲琛岋紝鍒欑疮鍔�
			 */
			lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
			lineHeight = Math.max(lineHeight, childHeight + lp.topMargin
					+ lp.bottomMargin);
			lineViews.add(child);
		}
		// 璁板綍鏈�鍚庝竴琛�
		mLineHeight.add(lineHeight);
		mAllViews.add(lineViews);

		int left = 0;
		int top = 0;
		// 寰楀埌鎬昏鏁�
		int lineNums = mAllViews.size();
		for (int i = 0; i < lineNums; i++) {
			// 姣忎竴琛岀殑鎵�鏈夌殑views
			lineViews = mAllViews.get(i);
			// 褰撳墠琛岀殑鏈�澶ч珮搴�
			lineHeight = mLineHeight.get(i);

			Log.e(TAG, "绗�" + i + "琛� 锛�" + lineViews.size() + " , "
					+ lineViews);
			Log.e(TAG, "绗�" + i + "琛岋紝 锛�" + lineHeight);

			// 閬嶅巻褰撳墠琛屾墍鏈夌殑View
			for (int j = 0; j < lineViews.size(); j++) {
				View child = lineViews.get(j);
				if (child.getVisibility() == View.GONE) {
					continue;
				}
				MarginLayoutParams lp = (MarginLayoutParams) child
						.getLayoutParams();

				// 璁＄畻childView鐨刲eft,top,right,bottom
				int lc = left + lp.leftMargin;
				int tc = top + lp.topMargin;
				int rc = lc + child.getMeasuredWidth();
				int bc = tc + child.getMeasuredHeight();

				Log.e(TAG, child + " , l = " + lc + " , t = " + t + " , r ="
						+ rc + " , b = " + bc);

				child.layout(lc, tc, rc, bc);

				left += child.getMeasuredWidth() + lp.rightMargin
						+ lp.leftMargin;
			}
			left = 0;
			top += lineHeight;
		}

	}
}