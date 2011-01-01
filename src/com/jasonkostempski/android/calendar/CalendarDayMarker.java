package com.jasonkostempski.android.calendar;

public class CalendarDayMarker {
	private int _year;
	private int _month;
	private int _day;
	private int _color;
	
	public CalendarDayMarker(int year, int month, int day, int color) {
		_year = year;
		_month = month;
		_day = day;
		_color = color;
	}
	
	public void setYear(int year) {
		_year = year;
	}
	
	public int getYear() {
		return _year;
	}

	public void setMonth(int month) {
		_month = month;
	}

	public int getMonth() {
		return _month;
	}

	public void setDay(int day) {
		_day = day;
	}

	public int getDay() {
		return _day;
	}

	public void setColor(int color) {
		_color = color;
	}

	public int getColor() {
		return _color;
	}	
}
