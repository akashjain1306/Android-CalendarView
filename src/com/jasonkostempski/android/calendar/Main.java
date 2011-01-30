package com.jasonkostempski.android.calendar;

import java.util.Calendar;
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
				tv1.setText("TODO:");
				vs[0] = tv1;

				TextView tv2 = new TextView(Main.this);
				tv2.setText("Put events for this day here.");
				vs[1] = tv2;

				view.setListViewItems(vs);
			}
		});

		markDays();
	}
	
	private void markDays() {
		// TODO: Find items in the range of _calendar.getVisibleStartDate() and _calendar.getVisibleEndDate().
		// TODO: Create CalendarDayMarker for each item found.
		// TODO: Pass CalendarDayMarker array to _calendar.setDaysWithEvents(markers)
		
		//Example of setting just today with an event
		_calendar.setDaysWithEvents(new CalendarDayMarker[] { new CalendarDayMarker(Calendar.getInstance(), Color.CYAN) });
	}

	private CalendarView _calendar;
}