package com.app.custcalendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.app.custcalendar.builders.DatePickerBuilder;
import com.app.custcalendar.listeners.OnSelectDateListener;
import com.app.custcalendar.utils.DateUtils;
import com.github.badoualy.datepicker.DatePickerTimeline;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnSelectDateListener {

    Button datePickerTimeline, btnRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        datePickerTimeline = findViewById(R.id.datepicker);
        btnRange = findViewById(R.id.btnRange);

        datePickerTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openManyDaysPicker();
                startActivity(new Intent(MainActivity.this, ManyDaysPickerActivity.class));
            }
        });

        btnRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openManyDaysPicker();
                startActivity(new Intent(MainActivity.this, RangePickerActivity.class));
            }
        });
    }

    private void openManyDaysPicker() {
        Calendar min = Calendar.getInstance();
        min.add(Calendar.DAY_OF_MONTH, -5);

        Calendar max = Calendar.getInstance();
        max.add(Calendar.DAY_OF_MONTH, 3);

        List<Calendar> selectedDays = new ArrayList<>(getDisabledDays());
        selectedDays.add(min);
        selectedDays.add(max);

        DatePickerBuilder manyDaysBuilder = new DatePickerBuilder(this, this)
                .pickerType(CalendarView.MANY_DAYS_PICKER)
                .headerColor(android.R.color.holo_green_dark)
                .selectionColor(android.R.color.holo_green_dark)
                .todayLabelColor(android.R.color.holo_green_dark)
                .dialogButtonsColor(android.R.color.holo_green_dark)
                .selectedDays(selectedDays)
                .disabledDays(getDisabledDays());

        DatePicker manyDaysPicker = manyDaysBuilder.build();
        manyDaysPicker.show();
    }

    private List<Calendar> getDisabledDays() {
        Calendar firstDisabled = DateUtils.getCalendar();
        firstDisabled.add(Calendar.DAY_OF_MONTH, 2);

        Calendar secondDisabled = DateUtils.getCalendar();
        secondDisabled.add(Calendar.DAY_OF_MONTH, 1);

        Calendar thirdDisabled = DateUtils.getCalendar();
        thirdDisabled.add(Calendar.DAY_OF_MONTH, 18);

        List<Calendar> calendars = new ArrayList<>();
        calendars.add(firstDisabled);
        calendars.add(secondDisabled);
        calendars.add(thirdDisabled);
        return calendars;
    }

    @Override
    public void onSelect(List<Calendar> calendars) {
        Stream.of(calendars).forEach(calendar ->
                Toast.makeText(this,
                        calendar.getTime().toString(),
                        Toast.LENGTH_SHORT).show());
    }
}
