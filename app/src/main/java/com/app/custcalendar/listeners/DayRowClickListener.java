package com.app.custcalendar.listeners;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.app.custcalendar.CalendarUtils;
import com.app.custcalendar.CalendarView;
import com.app.custcalendar.EventDay;
import com.app.custcalendar.GlobalApplication;
import com.app.custcalendar.R;
import com.app.custcalendar.adapters.CalendarPageAdapter;
import com.app.custcalendar.utils.CalendarProperties;
import com.app.custcalendar.utils.DateUtils;
import com.app.custcalendar.utils.DayColorsUtils;
import com.app.custcalendar.utils.SelectedDay;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * This class is responsible for handle click events
 * <p>
 * Created by Mateusz Kornakiewicz on 24.05.2017.
 */

public class DayRowClickListener implements AdapterView.OnItemClickListener {

    boolean conflic = false;
    private CalendarPageAdapter mCalendarPageAdapter;
    private CalendarProperties mCalendarProperties;
    private int mPageMonth;

    public DayRowClickListener(CalendarPageAdapter calendarPageAdapter, CalendarProperties calendarProperties, int pageMonth) {
        mCalendarPageAdapter = calendarPageAdapter;
        mCalendarProperties = calendarProperties;
        mPageMonth = pageMonth < 0 ? 11 : pageMonth;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Calendar day = new GregorianCalendar();
        day.setTime((Date) adapterView.getItemAtPosition(position));

        if (mCalendarProperties.getOnDayClickListener() != null) {
            onClick(day);
        }

        switch (mCalendarProperties.getCalendarType()) {
            case CalendarView.ONE_DAY_PICKER:
                selectOneDay(view, day);
                break;

            case CalendarView.MANY_DAYS_PICKER:
                selectManyDays(view, day);
                break;

            case CalendarView.RANGE_PICKER:
                selectRange(view, day);
                break;

            case CalendarView.CLASSIC:
                mCalendarPageAdapter.setSelectedDay(new SelectedDay(view, day));
        }
    }

    private void selectOneDay(View view, Calendar day) {
        SelectedDay previousSelectedDay = mCalendarPageAdapter.getSelectedDay();

        TextView dayLabel = (TextView) view.findViewById(R.id.dayLabel);

        if (isAnotherDaySelected(previousSelectedDay, day)) {
            selectDay(dayLabel, day);
            reverseUnselectedColor(previousSelectedDay);
        }
    }

    private void selectManyDays(View view, Calendar day) {
        TextView dayLabel = (TextView) view.findViewById(R.id.dayLabel);

        if (isCurrentMonthDay(day) && isActiveDay(day) && isBookedDay(day)) {
            SelectedDay selectedDay = new SelectedDay(dayLabel, day);

            if (!mCalendarPageAdapter.getSelectedDays().contains(selectedDay)) {
                DayColorsUtils.setSelectedDayColors(dayLabel, mCalendarProperties);

            } else {
                reverseUnselectedColor(selectedDay);
            }

            mCalendarPageAdapter.addSelectedDay(selectedDay);
        }
    }

    private void selectRange(View view, Calendar day) {
        TextView dayLabel = (TextView) view.findViewById(R.id.dayLabel);

        if (!isCurrentMonthDay(day) || !isActiveDay(day) || !isBookedDay(day)) {
            return;
        }

        List<SelectedDay> selectedDays = mCalendarPageAdapter.getSelectedDays();

        if (selectedDays.size() > 1) {
            clearAndSelectOne(dayLabel, day);
        }

        if (selectedDays.size() == 1) {
            selectOneAndRange(dayLabel, day);
        }

        if (selectedDays.isEmpty()) {
            selectDay(dayLabel, day);
        }
    }

    private void clearAndSelectOne(TextView dayLabel, Calendar day) {
        Stream.of(mCalendarPageAdapter.getSelectedDays()).forEach(this::reverseUnselectedColor);
        selectDay(dayLabel, day);
    }

    private void selectOneAndRange(TextView dayLabel, Calendar day) {
        SelectedDay previousSelectedDay = mCalendarPageAdapter.getSelectedDay();

        if (isConflict(previousSelectedDay.getCalendar(), day)) {
            mCalendarPageAdapter.getSelectedDays().clear();
//            mCalendarPageAdapter.getSelectedDay().getCalendar().clear();
            mCalendarPageAdapter.notifyDataSetChanged();
            Toast.makeText(GlobalApplication.getAppContext(), "Dates have conflict with booked dates or unavailable dates ", Toast.LENGTH_SHORT).show();

        } else {
            Stream.of(CalendarUtils.getDatesRange(previousSelectedDay.getCalendar(), day))
                    .filter(calendar -> !mCalendarProperties.getDisabledDays().contains(calendar)).filter(calendar -> !mCalendarProperties.getBookedDays().contains(calendar))
                    .forEach(calendar -> mCalendarPageAdapter.addSelectedDay(new SelectedDay(calendar)));

            DayColorsUtils.setSelectedDayColors(dayLabel, mCalendarProperties);

            mCalendarPageAdapter.addSelectedDay(new SelectedDay(dayLabel, day));
            mCalendarPageAdapter.notifyDataSetChanged();
        }

    }

    public boolean isConflict(Calendar calendar, Calendar day) {
        List<Calendar> list = CalendarUtils.getDatesRange(calendar, day);
        for (Calendar cal : list) {
            if (cal.equals(calendar) || cal.equals(day))
                continue;
            else if (mCalendarProperties.getDisabledDays().contains(cal) || mCalendarProperties.getBookedDays().contains(cal)) {
                conflic = true;
                break;

            }
        }
        return conflic;

    }

    private void selectDay(TextView dayLabel, Calendar day) {
        DayColorsUtils.setSelectedDayColors(dayLabel, mCalendarProperties);
        mCalendarPageAdapter.setSelectedDay(new SelectedDay(dayLabel, day));
    }

    private void reverseUnselectedColor(SelectedDay selectedDay) {
        DayColorsUtils.setCurrentMonthDayColors(selectedDay.getCalendar(),
                DateUtils.getCalendar(), (TextView) selectedDay.getView(), mCalendarProperties);
    }

    private boolean isCurrentMonthDay(Calendar day) {
        return day.get(Calendar.MONTH) == mPageMonth && isBetweenMinAndMax(day);
    }

    private boolean isActiveDay(Calendar day) {
        return !mCalendarProperties.getDisabledDays().contains(day);
    }

    private boolean isBookedDay(Calendar day) {
        return !mCalendarProperties.getBookedDays().contains(day);
    }

    private boolean isBetweenMinAndMax(Calendar day) {
        return !((mCalendarProperties.getMinimumDate() != null && day.before(mCalendarProperties.getMinimumDate()))
                || (mCalendarProperties.getMaximumDate() != null && day.after(mCalendarProperties.getMaximumDate())));
    }

    private boolean isAnotherDaySelected(SelectedDay selectedDay, Calendar day) {
        return selectedDay != null && !day.equals(selectedDay.getCalendar())
                && isCurrentMonthDay(day) && isActiveDay(day);
    }

    private void onClick(Calendar day) {
        if (mCalendarProperties.getEventDays() == null) {
            createEmptyEventDay(day);
            return;
        }

        Stream.of(mCalendarProperties.getEventDays())
                .filter(eventDate -> eventDate.getCalendar().equals(day))
                .findFirst()
                .ifPresentOrElse(this::callOnClickListener, () -> createEmptyEventDay(day));
    }

    private void createEmptyEventDay(Calendar day) {
        EventDay eventDay = new EventDay(day);
        callOnClickListener(eventDay);
    }

    private void callOnClickListener(EventDay eventDay) {
        boolean enabledDay = mCalendarProperties.getDisabledDays().contains(eventDay.getCalendar())
                || !isBetweenMinAndMax(eventDay.getCalendar());

        eventDay.setEnabled(enabledDay);
        mCalendarProperties.getOnDayClickListener().onDayClick(eventDay);
    }
}
