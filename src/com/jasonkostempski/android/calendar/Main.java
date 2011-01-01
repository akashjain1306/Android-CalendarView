package com.jasonkostempski.android.calendar;

import com.jasonkostempski.android.calendar.CalendarView.OnMonthChangedListener;
import com.jasonkostempski.android.calendar.CalendarView.OnSelectedDayChangedListener;
import com.jasonkostempski.android.calendar.R;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Main extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		_calendar = (CalendarView) findViewById(R.id.calendar_view);
		
		_calendar.setOnMonthChangedListener(new OnMonthChangedListener() {
			public void onMonthChanged(CalendarView view) {
				markDays();
			}
		});
		
		_calendar.setOnSelectedDayChangedListener(new OnSelectedDayChangedListener() {
			public void onSelectedDayChanged(CalendarView view) {
				View[] vs = new View[2];
				
				TextView tv1 = new TextView(Main.this);
				tv1.setText("Hello");
				vs[0] = tv1;
				
				TextView tv2 = new TextView(Main.this);
				tv2.setText("Goodbye");
				vs[1] = tv2;
				
				view.setListViewItems(vs);
			}
		});
		
		markDays();
	}
	
	private void markDays() {
		CalendarDayMarker[] ms = new CalendarDayMarker[2];
		ms[0] = new CalendarDayMarker(2010, 11, 21, Color.CYAN);
		ms[1] = new CalendarDayMarker(2010, 11, 3, Color.CYAN);
		_calendar.setDaysWithEvents(ms);
	}
	
	private CalendarView _calendar;
}