package com.app.custcalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RangePickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_range_picker);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);

        Calendar maxCal = Calendar.getInstance();
        maxCal.add(Calendar.MONTH, 2);
        calendarView.setMinimumDate(Calendar.getInstance());
        calendarView.setMaximumDate(maxCal);
//        calendarView.setSelectedDates(getSelectedDays());
        calendarView.setDisabledDays(getDisabledDays());
        calendarView.setBookedDays(getSelectedDays());

        Button getDateButton = (Button) findViewById(R.id.getDateButton);
        getDateButton.setOnClickListener(v -> {
            for (Calendar calendar : calendarView.getSelectedDates()) {
                System.out.println(calendar.getTime().toString());

                Toast.makeText(getApplicationContext(),
                        calendar.getTime().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private List<Calendar> getSelectedDays() {
        List<String> datelist = new ArrayList<>();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");

        List<Calendar> calendars = new ArrayList<>();

        for (int i = 2; i <= 10; i++) {
            datelist.add("2019-05-" + i);
            try {

                Date date = parser.parse(datelist.get(i - 2));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
//                calendar.add(Calendar.DAY_OF_MONTH, i);
                calendars.add(calendar);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return calendars;
    }

    private List<Calendar> getDisabledDays() {
        List<String> datelist = new ArrayList<>();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");

        List<Calendar> calendars = new ArrayList<>();

        for (int i = 2; i <= 10; i++) {
            datelist.add("2019-06-" + i);
            try {

                Date date = parser.parse(datelist.get(i - 2));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
//                calendar.add(Calendar.DAY_OF_MONTH, i);
                calendars.add(calendar);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return calendars;
    }
}