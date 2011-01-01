package com.jasonkostempski.android.calendar;

import java.util.Calendar;
import com.jasonkostempski.android.calendar.R;
import com.jasonkostempski.android.calendar.CalendarWrapper.OnDateChangedListener;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CalendarView extends LinearLayout {
	public CalendarView(Context context) {
		super(context);
		init(context);
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public interface OnMonthChangedListener {
		public void onMonthChanged(CalendarView view);
	}

	public void setOnMonthChangedListener(OnMonthChangedListener l) {
		_onMonthChangedListener = l;
	}

	public interface OnSelectedDayChangedListener {
		public void onSelectedDayChanged(CalendarView view);
	}

	public void setOnSelectedDayChangedListener(OnSelectedDayChangedListener l) {
		_onSelectedDayChangedListener = l;
	}

	public Calendar getVisibleStartDate() {
		return _calendar.getVisibleStartDate();
	}

	public Calendar getVisibleEndDate() {
		return _calendar.getVisibleEndDate();
	}

	public Calendar getSelectedDay() {
		return _calendar.getSelectedDay();
	}

	public void setDaysWithEvents(CalendarDayMarker[] markers) {
		for (int i = 0; i < markers.length; i++) {
			CalendarDayMarker m = markers[i];

			int row = 1; // Skip weekday header row
			int col = 0;

			for (int j = 0; j < 42; j++) {
				TableRow tr = (TableRow) _days.getChildAt(row);
				TextView tv = (TextView) tr.getChildAt(col);
				int[] tag = (int[]) tv.getTag();
				int monthAdd = tag[0];
				int day = tag[1];

				if (monthAdd == 0 && _calendar.getYear() == m.getYear() && _calendar.getMonth() == m.getMonth() && day == m.getDay()) {
					tv.setBackgroundColor(m.getColor());
				}
				else if (monthAdd == -1) {
					tv.setBackgroundDrawable(null);
				}
				else if (monthAdd == 1) {
					tv.setBackgroundDrawable(null);
				}
				else {
					tv.setBackgroundDrawable(null);
				}

				col++;

				if (col == 7) {
					col = 0;
					row++;
				}
			}
		}
	}

	public void setListViewItems(View[] views) {
		_events.removeAllViews();

		for (int i = 0; i < views.length; i++) {
			_events.addView(views[i]);
		}
	}

	private void init(Context context) {
		View v = LayoutInflater.from(context).inflate(R.layout.calendar, this, true);

		_calendar = new CalendarWrapper();
		_days = (TableLayout) v.findViewById(R.id.days);
		_months = (TableLayout) v.findViewById(R.id.months);
		_years = (TableLayout) v.findViewById(R.id.years);
		_up = (Button) v.findViewById(R.id.up);
		_prev = (Button) v.findViewById(R.id.previous);
		_next = (Button) v.findViewById(R.id.next);
		_events = (LinearLayout) v.findViewById(R.id.events);

		refreshCurrentDate();

		// Days Table
		String[] shortWeekDayNames = _calendar.getShortDayNames();

		for (int i = 0; i < 7; i++) { // Rows
			TableRow tr = (TableRow) _days.getChildAt(i);

			for (int j = 0; j < 7; j++) { // Columns
				Boolean header = i == 0; // First row is weekday headers
				TextView tv = (TextView) tr.getChildAt(j);

				if (header)
					tv.setText(shortWeekDayNames[j]);
				else
					tv.setOnClickListener(_dayClicked);
			}
		}

		refreshDayCells();

		// Months Table
		String[] shortMonthNames = _calendar.getShortMonthNames();
		int monthNameIndex = 0;

		for (int i = 0; i < 3; i++) { // Rows
			TableRow tr = (TableRow) _months.getChildAt(i);

			for (int j = 0; j < 4; j++) { // Columns
				TextView tv = (TextView) tr.getChildAt(j);
				tv.setOnClickListener(_monthClicked);
				tv.setText(shortMonthNames[monthNameIndex]);
				tv.setTag(monthNameIndex);

				monthNameIndex++;
			}
		}

		// Years Table
		for (int i = 0; i < 3; i++) { // Rows
			TableRow tr = (TableRow) _years.getChildAt(i);

			for (int j = 0; j < 4; j++) { // Columns
				TextView tv = (TextView) tr.getChildAt(j);
				tv.setOnClickListener(_yearClicked);
			}
		}

		// Listeners
		_calendar.setOnDateChangedListener(_dateChanged);
		_up.setOnClickListener(_upClicked);
		_prev.setOnClickListener(_incrementClicked);
		_next.setOnClickListener(_incrementClicked);

		setView(MONTH_VIEW);
	}

	private OnDateChangedListener _dateChanged = new OnDateChangedListener() {
		public void onDateChanged(CalendarWrapper sc) {
			Boolean monthChanged = _currentYear != sc.getYear() || _currentMonth != sc.getMonth();
			
			if (monthChanged) {
				refreshDayCells();
				invokeMonthChangedListener();
			}

			refreshCurrentDate();
			refreshUpText();
		}
	};

	private OnClickListener _incrementClicked = new OnClickListener() {
		public void onClick(View v) {
			int inc = (v == _next ? 1 : -1);

			if (_currentView == MONTH_VIEW)
				_calendar.addMonth(inc);
			else if (_currentView == DAY_VIEW) {
				_calendar.addDay(inc);
				invokeSelectedDayChangedListener();
			}
			else if (_currentView == YEAR_VIEW) {
				_currentYear += inc;
				refreshUpText();
			}
		}
	};

	private OnClickListener _dayClicked = new OnClickListener() {
		public void onClick(View v) {
			int[] tag = (int[]) v.getTag();
			_calendar.addMonthSetDay(tag[0], tag[1]);
			invokeSelectedDayChangedListener();
			setView(DAY_VIEW);
		}
	};

	private OnClickListener _monthClicked = new OnClickListener() {
		public void onClick(View v) {
			_calendar.setYearAndMonth(_currentYear, (Integer) v.getTag());
			setView(MONTH_VIEW);
		}
	};

	private OnClickListener _yearClicked = new OnClickListener() {
		public void onClick(View v) {
			_currentYear = (Integer) v.getTag();
			setView(YEAR_VIEW);
		}
	};

	private OnClickListener _upClicked = new OnClickListener() {
		public void onClick(View v) {
			setView(_currentView + 1);
		}
	};

	private void refreshDayCells() {
		int[] dayGrid = _calendar.get7x6DayArray();
		int monthAdd = -1;
		int row = 1; // Skip weekday header row
		int col = 0;

		for (int i = 0; i < dayGrid.length; i++) {
			int day = dayGrid[i];

			if (day == 1)
				monthAdd++;

			TableRow tr = (TableRow) _days.getChildAt(row);
			TextView tv = (TextView) tr.getChildAt(col);

			tv.setText(dayGrid[i] + "");

			if (monthAdd == 0)
				tv.setTextColor(Color.LTGRAY);
			else
				tv.setTextColor(Color.DKGRAY);

			tv.setTag(new int[] { monthAdd, dayGrid[i] });

			col++;

			if (col == 7) {
				col = 0;
				row++;
			}
		}
	}

	private void setView(int view) {
		if (_currentView != view) {
			_currentView = view;
			_events.setVisibility(_currentView == DAY_VIEW ? View.VISIBLE : View.GONE);
			_years.setVisibility(_currentView == DECADE_VIEW ? View.VISIBLE : View.GONE);
			_months.setVisibility(_currentView == YEAR_VIEW ? View.VISIBLE : View.GONE);
			_days.setVisibility(_currentView == MONTH_VIEW ? View.VISIBLE : View.GONE);
			_up.setEnabled(_currentView != YEAR_VIEW);

			refreshUpText();
		}
	}

	private void refreshUpText() {
		switch (_currentView) {
			case MONTH_VIEW:
				_up.setText(_calendar.toString("MMMM yyyy"));
				break;
			case YEAR_VIEW:
				_up.setText(_currentYear + "");
				break;
			case CENTURY_VIEW:
				_up.setText("CENTURY_VIEW");
				break;
			case DECADE_VIEW:
				_up.setText("DECADE_VIEW");
				break;
			case DAY_VIEW:
				_up.setText(_calendar.toString("EEEE, MMMM dd, yyyy"));
				break;
			case ITEM_VIEW:
				_up.setText("ITEM_VIEW");
				break;
			default:
				break;
		}
	}

	private void refreshCurrentDate() {
		_currentYear = _calendar.getYear();
		_currentMonth = _calendar.getMonth();
		_calendar.getDay();
	}

	private void invokeMonthChangedListener() {
		if (_onMonthChangedListener != null)
			_onMonthChangedListener.onMonthChanged(this);
	}

	private void invokeSelectedDayChangedListener() {
		if (_onSelectedDayChangedListener != null)
			_onSelectedDayChangedListener.onSelectedDayChanged(this);
	}

	private final int CENTURY_VIEW = 5;
	private final int DECADE_VIEW = 4;
	private final int YEAR_VIEW = 3;
	private final int MONTH_VIEW = 2;
	private final int DAY_VIEW = 1;
	private final int ITEM_VIEW = 0;

	private CalendarWrapper _calendar;
	private TableLayout _days;
	private TableLayout _months;
	private TableLayout _years;
	private LinearLayout _events;
	private Button _up;
	private Button _prev;
	private Button _next;
	private OnMonthChangedListener _onMonthChangedListener;
	private OnSelectedDayChangedListener _onSelectedDayChangedListener;
	private int _currentView;
	private int _currentYear;
	private int _currentMonth;
}
