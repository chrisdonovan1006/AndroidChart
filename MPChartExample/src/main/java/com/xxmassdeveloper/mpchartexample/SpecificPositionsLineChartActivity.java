package com.xxmassdeveloper.mpchartexample;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.xxmassdeveloper.mpchartexample.custom.MyMarkerView;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;

public class SpecificPositionsLineChartActivity extends DemoBase implements OnSeekBarChangeListener, OnChartGestureListener, OnChartValueSelectedListener {

	private LineChart mChart;
	private SeekBar mSeekBarX, mSeekBarY;
	private TextView tvX, tvY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_linechart);

		tvX = (TextView) findViewById(R.id.tvXMax);
		tvY = (TextView) findViewById(R.id.tvYMax);

		mSeekBarX = (SeekBar) findViewById(R.id.seekBarX);
		mSeekBarY = (SeekBar) findViewById(R.id.seekBarY);

		mSeekBarX.setProgress(45);
		mSeekBarY.setProgress(100);

		mSeekBarY.setOnSeekBarChangeListener(this);
		mSeekBarX.setOnSeekBarChangeListener(this);

		mChart = (LineChart) findViewById(R.id.chart1);
		mChart.setOnChartGestureListener(this);
		mChart.setOnChartValueSelectedListener(this);
		mChart.setDrawGridBackground(false);

		// no description text
		mChart.getDescription().setEnabled(false);

		// enable touch gestures
		mChart.setTouchEnabled(true);

		// enable scaling and dragging
		mChart.setDragEnabled(true);
		mChart.setScaleEnabled(true);
		// mChart.setScaleXEnabled(true);
		// mChart.setScaleYEnabled(true);

		// if disabled, scaling can be done on x- and y-axis separately
		mChart.setPinchZoom(true);

		// set an alternative background color
		// mChart.setBackgroundColor(Color.GRAY);

		// create a custom MarkerView (extend MarkerView) and specify the layout
		// to use for it
		MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
		mv.setChartView(mChart); // For bounds control
		mChart.setMarker(mv); // Set the marker to the chart

		// x-axis limit line
		LimitLine llXAxis = new LimitLine(10f, "Index 10");
		llXAxis.setLineWidth(4f);
		llXAxis.enableDashedLine(10f, 10f, 0f);
		llXAxis.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
		llXAxis.setTextSize(10f);

		XAxis xAxis = mChart.getXAxis();
		xAxis.enableGridDashedLine(10f, 10f, 0f);

		xAxis.setShowSpecificPositions(true);
		xAxis.setSpecificPositions(new float[]{20, 30, 60});

		Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

		LimitLine ll1 = new LimitLine(150f, "Upper Limit");
		ll1.setLineWidth(4f);
		ll1.enableDashedLine(10f, 10f, 0f);
		ll1.setLabelPosition(LimitLabelPosition.RIGHT_TOP);
		ll1.setTextSize(10f);
		ll1.setTypeface(tf);

		LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
		ll2.setLineWidth(4f);
		ll2.enableDashedLine(10f, 10f, 0f);
		ll2.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
		ll2.setTextSize(10f);
		ll2.setTypeface(tf);

		YAxis leftAxis = mChart.getAxisLeft();
		leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
		leftAxis.addLimitLine(ll1);
		leftAxis.addLimitLine(ll2);
		leftAxis.setAxisMaximum(200f);
		leftAxis.setAxisMinimum(-50f);
		//leftAxis.setYOffset(20f);
		leftAxis.enableGridDashedLine(10f, 10f, 0f);
		leftAxis.setDrawZeroLine(false);

		leftAxis.setShowSpecificPositions(true);
		leftAxis.setSpecificPositions(new float[]{0, 10, 20, 50, 100, 300});

		// limit lines are drawn behind data (and not on top)
		leftAxis.setDrawLimitLinesBehindData(true);

		mChart.getAxisRight().setEnabled(false);

		setData(45, 100);

		mChart.animateX(2500);
		//mChart.invalidate();

		// get the legend (only possible after setting data)
		Legend l = mChart.getLegend();

		// modify the legend ...
		l.setForm(LegendForm.LINE);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.line, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.actionToggleValues -> {
				List<ILineDataSet> sets = mChart.getData().getDataSets();

				for (ILineDataSet iSet : sets) {

					LineDataSet set = (LineDataSet) iSet;
					set.setDrawValues(!set.isDrawValuesEnabled());
				}

				mChart.invalidate();
			}
			case R.id.actionToggleHighlight -> {
				if (mChart.getData() != null) {
					mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
					mChart.invalidate();
				}
			}
			case R.id.actionToggleFilled -> {

				List<ILineDataSet> sets = mChart.getData().getDataSets();

				for (ILineDataSet iSet : sets) {

					LineDataSet set = (LineDataSet) iSet;
					set.setDrawFilled(!set.isDrawFilledEnabled());
				}
				mChart.invalidate();
			}
			case R.id.actionToggleCircles -> {
				List<ILineDataSet> sets = mChart.getData().getDataSets();

				for (ILineDataSet iSet : sets) {

					LineDataSet set = (LineDataSet) iSet;
					set.setDrawCircles(!set.isDrawCirclesEnabled());
				}
				mChart.invalidate();
			}
			case R.id.actionToggleCubic -> {
				List<ILineDataSet> sets = mChart.getData().getDataSets();

				for (ILineDataSet iSet : sets) {

					LineDataSet set = (LineDataSet) iSet;
					set.setMode(set.getMode() == LineDataSet.Mode.CUBIC_BEZIER ? LineDataSet.Mode.LINEAR : LineDataSet.Mode.CUBIC_BEZIER);
				}
				mChart.invalidate();
			}
			case R.id.actionToggleStepped -> {
				List<ILineDataSet> sets = mChart.getData().getDataSets();

				for (ILineDataSet iSet : sets) {

					LineDataSet set = (LineDataSet) iSet;
					set.setMode(set.getMode() == LineDataSet.Mode.STEPPED ? LineDataSet.Mode.LINEAR : LineDataSet.Mode.STEPPED);
				}
				mChart.invalidate();
			}
			case R.id.actionToggleHorizontalCubic -> {
				List<ILineDataSet> sets = mChart.getData().getDataSets();

				for (ILineDataSet iSet : sets) {

					LineDataSet set = (LineDataSet) iSet;
					set.setMode(set.getMode() == LineDataSet.Mode.HORIZONTAL_BEZIER ? LineDataSet.Mode.LINEAR : LineDataSet.Mode.HORIZONTAL_BEZIER);
				}
				mChart.invalidate();
			}
			case R.id.actionTogglePinch -> {
				mChart.setPinchZoom(!mChart.isPinchZoomEnabled());

				mChart.invalidate();
			}
			case R.id.actionToggleAutoScaleMinMax -> {
				mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
				mChart.notifyDataSetChanged();
			}
			case R.id.animateX -> mChart.animateX(3000);
			case R.id.animateY -> mChart.animateY(3000, Easing.EaseInCubic);
			case R.id.animateXY -> mChart.animateXY(3000, 3000);
			case R.id.actionSave -> {
				if (mChart.saveToPath("title" + System.currentTimeMillis(), "")) {
					Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT).show();
				}

				// mChart.saveToGallery("title"+System.currentTimeMillis())
			}
		}
		return true;
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		tvX.setText("" + (mSeekBarX.getProgress() + 1));
		tvY.setText("" + (mSeekBarY.getProgress()));

		setData(mSeekBarX.getProgress() + 1, mSeekBarY.getProgress());

		// redraw
		mChart.invalidate();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	private void setData(int count, float range) {

		ArrayList<Entry> values = new ArrayList<>();

		for (int i = 0; i < count; i++) {

			float val = (float) (Math.random() * range) + 3;
			values.add(new Entry(i, val));
		}

		LineDataSet set1;

		if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
			set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
			set1.setValues(values);
			mChart.getData().notifyDataChanged();
			mChart.notifyDataSetChanged();
		} else {
			// create a dataset and give it a type
			set1 = new LineDataSet(values, "DataSet 1");

			// set the line to be drawn like this "- - - - - -"
			set1.enableDashedLine(10f, 5f, 0f);
			set1.enableDashedHighlightLine(10f, 5f, 0f);
			set1.setColor(Color.BLACK);
			set1.setCircleColor(Color.BLACK);
			set1.setLineWidth(1f);
			set1.setCircleRadius(3f);
			set1.setDrawCircleHole(false);
			set1.setValueTextSize(9f);
			set1.setDrawFilled(true);
			set1.setFormLineWidth(1f);
			set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
			set1.setFormSize(15.f);

			if (Utils.getSDKInt() >= 18) {
				// fill drawable only supported on api level 18 and above
				Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
				set1.setFillDrawable(drawable);
			} else {
				set1.setFillColor(Color.BLACK);
			}

			ArrayList<ILineDataSet> dataSets = new ArrayList<>();
			dataSets.add(set1); // add the datasets

			// create a data object with the datasets
			LineData data = new LineData(dataSets);

			// set data
			mChart.setData(data);
		}
	}

	@Override
	public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
		Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
	}

	@Override
	public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
		Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

		// un-highlight values after the gesture is finished and no single-tap
		if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP) {
			mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
		}
	}

	@Override
	public void onChartLongPressed(MotionEvent me) {
		Log.i("LongPress", "Chart longpressed.");
	}

	@Override
	public void onChartDoubleTapped(MotionEvent me) {
		Log.i("DoubleTap", "Chart double-tapped.");
	}

	@Override
	public void onChartSingleTapped(MotionEvent me) {
		Log.i("SingleTap", "Chart single-tapped.");
	}

	@Override
	public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
		Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
	}

	@Override
	public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
		Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
	}

	@Override
	public void onChartTranslate(MotionEvent me, float dX, float dY) {
		Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
	}

	@Override
	public void onValueSelected(Entry e, Highlight h) {
		Log.i("Entry selected", e.toString());
		Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleX() + ", high: " + mChart.getHighestVisibleX());
		Log.i("MIN MAX", "xmin: " + mChart.getXChartMin() + ", xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", ymax: " + mChart.getYChartMax());
	}

	@Override
	public void onNothingSelected() {
		Log.i("Nothing selected", "Nothing selected.");
	}

	@Override
	protected void saveToGallery() {
		saveToGallery(mChart, "SpecificPositionsLineChartActivity");
	}
}
