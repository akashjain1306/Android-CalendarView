package com.jasonkostempski.android.calendar;

import java.util.Calendar;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

public class CalendarWrapper {
	public interface OnDateChangedListener {
		public void onDateChanged(CalendarWrapper sc);
	}

	public CalendarWrapper() {
		_calendar = Calendar.getInstance();
		
		_shortDayNames = new String[_calendar.getActualMaximum(Calendar.DAY_OF_WEEK)];
		_shortMonthNames = new String[_calendar.getActualMaximum(Calendar.MONTH) + 1]; // Months are 0-based so size is Max + 1
		
		for (int i = 0; i < _shortDayNames.length; i++) {
			_shortDayNames[i] = DateUtils.getDayOfWeekString(i + 1, DateUtils.LENGTH_SHORT);
		}
		
		for (int i = 0; i < _shortMonthNames.length; i++) {
			_shortMonthNames[i] = DateUtils.getMonthString(i, DateUtils.LENGTH_SHORT);
		}
	}

	public int getYear() {
		return _calendar.get(Calendar.YEAR);
	}

	public int getMonth() {
		return _calendar.get(Calendar.MONTH);
	}
	
	public int getDayOfWeek() {
		return _calendar.get(Calendar.DAY_OF_WEEK);
	}
	
	public int getDay() {
		return _calendar.get(Calendar.DAY_OF_MONTH);
	}

	public void setYear(int value) {
		_calendar.set(Calendar.YEAR, value);
		invokeDateChangedListener();
	}
	
	public void setYearAndMonth(int year, int month) {
		_calendar.set(Calendar.YEAR, year);
		_calendar.set(Calendar.MONTH, month);
		invokeDateChangedListener();
	}

	public void setMonth(int value) {
		_calendar.set(Calendar.MONTH, value);
		invokeDateChangedListener();
	}

	public void setDay(int value) {
		_calendar.set(Calendar.DAY_OF_MONTH, value);
		invokeDateChangedListener();
	}
	
	public void addYear(int value) {
		if(value != 0) {
			_calendar.add(Calendar.YEAR, value);
			invokeDateChangedListener();
		}
	}

	public void addMonth(int value) {
		if(value != 0) {
			_calendar.add(Calendar.MONTH, value);
			invokeDateChangedListener();
		}
	}
	
	public void addMonthSetDay(int monthAdd, int day) {
		_calendar.add(Calendar.MONTH, monthAdd);
		_calendar.set(Calendar.DAY_OF_MONTH, day);
		
		invokeDateChangedListener();
	}

	public void addDay(int value) {
		if(value != 0) {
			_calendar.add(Calendar.DAY_OF_MONTH, value);
			invokeDateChangedListener();
		}
	}

	public String[] getShortDayNames() {
		return _shortDayNames;
	}

	public String[] getShortMonthNames() {
		return _shortMonthNames;
	}

	public int[] get7x6DayArray() {
		_visibleStartDate = null;
		_visibleEndDate = null;
		
		int[] days = new int[42];

		Calendar tempCal = (Calendar) _calendar.clone();
		tempCal.set(Calendar.DAY_OF_MONTH, 1);
		
		int dayOfWeekOn1st = tempCal.get(Calendar.DAY_OF_WEEK);
		int maxDay = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);
		int previousMonthCount = dayOfWeekOn1st - 1;
		int index = 0;

		if (previousMonthCount > 0) {
			tempCal.set(Calendar.DAY_OF_MONTH, -1);
			
			int previousMonthMax = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);

			for (int i = previousMonthCount; i > 0; i--) {
				int day = previousMonthMax - i + 1; 
				
				if(i == previousMonthCount) {
					_visibleStartDate = (Calendar)tempCal.clone();
					_visibleStartDate.set(Calendar.DAY_OF_MONTH, day);
				}
				
				days[index] = day;
				index++;
			}
		}

		for (int i = 0; i < maxDay; i++) {
			if(i == 0 && _visibleStartDate == null)
				_visibleStartDate = (Calendar)tempCal.clone();
			
			days[index] = (i + 1);
			index++;
		}

		int nextMonthDay = 1;

		for (int i = index; i < days.length; i++) {
			if(i == index)
				
			
			days[index] = nextMonthDay;
			nextMonthDay++;
			index++;
		}
		
		_visibleEndDate = (Calendar) _calendar.clone();
		_visibleEndDate.add(Calendar.MONTH, 1);
		_visibleEndDate.set(Calendar.DAY_OF_MONTH, days[41]);
		
		return days;
	}
	
	public Calendar getSelectedDay() {
		return (Calendar)_calendar.clone();
	}
	
	public Calendar getVisibleStartDate() {
		return (Calendar) _visibleStartDate.clone();
	}
	
	public Calendar getVisibleEndDate() {
		return (Calendar) _visibleEndDate.clone();
	}

	public void setOnDateChangedListener(OnDateChangedListener l) {
		_onDateChangedListener = l;
	}
	
	public String toString(CharSequence format) {
		return DateFormat.format(format, _calendar).toString();
	}

	private void invokeDateChangedListener() {
		if (_onDateChangedListener != null)
			_onDateChangedListener.onDateChanged(this);
	}

	private Calendar _calendar;
	private String[] _shortDayNames;
	private String[] _shortMonthNames;
	private OnDateChangedListener _onDateChangedListener;
	private Calendar _visibleStartDate;
	private Calendar _visibleEndDate;
}