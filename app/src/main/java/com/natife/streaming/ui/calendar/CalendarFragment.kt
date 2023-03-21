package com.natife.streaming.ui.calendar

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.Size
import com.natife.streaming.R
import com.natife.streaming.base.BaseFragment
import com.natife.streaming.ext.daysOfWeekFromLocale
import com.natife.streaming.ext.dp
import com.natife.streaming.ext.fromCalendar
import com.natife.streaming.ext.subscribe
import com.natife.streaming.utils.findLexicText
import kotlinx.android.synthetic.main.calendar_header.view.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.item_day.view.*
import kotlinx.android.synthetic.main.view_calendar_header.*
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*


class CalendarFragment : BaseFragment<CalendarViewModel>() {
    override fun getLayoutRes(): Int = R.layout.fragment_calendar
    private var currentMonth: YearMonth = YearMonth.now()
    private var currentLang = "RU"

    //    private var firstMonth = currentMonth.minusMonths(0)
//    private var lastMonth = currentMonth.plusMonths(0)
    private var firstDayOfWeek = WeekFields.of(Locale("ru")).firstDayOfWeek
    private var monthsList = arrayOfNulls<String>(12)

    override fun onStart() {
        super.onStart()
        subscribe(viewModel.strings) { listLexics ->
            proceed_button.text = listLexics.findLexicText(context, R.integer.proceed)
            monthsList[0] = (
                listLexics.findLexicText(context, R.integer.january)
                    ?: resources.getString(R.string.january)
            )
            monthsList[1] = (
                listLexics.findLexicText(context, R.integer.february)
                    ?: resources.getString(R.string.february)
            )
            monthsList[2] = (
                listLexics.findLexicText(context, R.integer.march)
                    ?: resources.getString(R.string.march)
            )
            monthsList[3] = (
                listLexics.findLexicText(context, R.integer.april)
                    ?: resources.getString(R.string.april)
            )
            monthsList[4] = (
                listLexics.findLexicText(context, R.integer.may)
                    ?: resources.getString(R.string.may)
            )
            monthsList[5] = (
                listLexics.findLexicText(context, R.integer.june)
                    ?: resources.getString(R.string.june)
            )
            monthsList[6] = (
                listLexics.findLexicText(context, R.integer.july)
                    ?: resources.getString(R.string.july)
            )
            monthsList[7] = (
                listLexics.findLexicText(context, R.integer.august)
                    ?: resources.getString(R.string.august)
            )
            monthsList[8] = (
                listLexics.findLexicText(context, R.integer.september)
                    ?: resources.getString(R.string.september)
            )
            monthsList[9] = (
                listLexics.findLexicText(context, R.integer.october)
                    ?: resources.getString(R.string.october)
            )
            monthsList[10] = (
                listLexics.findLexicText(context, R.integer.november)
                    ?: resources.getString(R.string.november)
            )
            monthsList[11] = (
                listLexics.findLexicText(context, R.integer.december)
                    ?: resources.getString(R.string.december)
            )
        }
        subscribe(viewModel.lang) {
            currentLang = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        include.requestFocus()
        calendarView.doOnPreDraw {
            calendarView.daySize = Size(calendarView.width / 7, (calendarView.height - 40.dp) / 6)
        }
        val daysOfWeek = daysOfWeekFromLocale()

        subscribe(viewModel.date) { date ->
            // start calendar
            Calendar.getInstance().let {
                it.time = date
                val year = it.get(Calendar.YEAR)
                val month = it.get(Calendar.MONTH)
                currentMonth = (YearMonth.of(year, month.plus(1)))
//                        .plusMonths(1)
            }
            setup()
            calendarView.notifyCalendarChanged()
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            val textView = view.calendarDayText
            val backgroundview = view.dayBackground
        }

        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {

                container.textView.text = day.date.dayOfMonth.toString()
                val cal1 = Calendar.getInstance()
                val cal2 = Calendar.getInstance()
                cal1.time = day.date.fromCalendar()
                cal2.time = viewModel.date.value

                if (day.owner != DayOwner.THIS_MONTH) {
                    container.backgroundview.setBackgroundColor(Color.TRANSPARENT)
                    container.textView.setTextColor(Color.TRANSPARENT)
                    container.view.isFocusable = false
                } else if (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                ) {
                    container.view.isFocusable = true
                    container.backgroundview.setBackgroundResource(R.drawable.calendar_current_day)
                    container.view.requestFocus()
                } else {
                    container.view.isFocusable = true
                    container.backgroundview.setBackgroundResource(R.drawable.day_item_background)
                }
                container.view.setOnClickListener {
                    viewModel.select(day.date)
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {}
        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                val mMonth = when (month.yearMonth.month.getDisplayName(
                    TextStyle.FULL_STANDALONE,
                    Locale(currentLang)
                ).capitalize(Locale.ROOT)) {
                    "1" -> monthsList[0] ?: resources.getString(R.string.january)
                    "2" -> monthsList[1] ?: resources.getString(R.string.february)
                    "3" -> monthsList[2] ?: resources.getString(R.string.march)
                    "4" -> monthsList[3] ?: resources.getString(R.string.april)
                    "5" -> monthsList[4] ?: resources.getString(R.string.may)
                    "6" -> monthsList[5] ?: resources.getString(R.string.june)
                    "7" -> monthsList[6] ?: resources.getString(R.string.july)
                    "8" -> monthsList[7] ?: resources.getString(R.string.august)
                    "9" -> monthsList[8] ?: resources.getString(R.string.september)
                    "10" -> monthsList[9] ?: resources.getString(R.string.october)
                    "11" -> monthsList[10] ?: resources.getString(R.string.november)
                    "12" -> monthsList[11] ?: resources.getString(R.string.december)
                    else -> month.yearMonth.month.getDisplayName(
                        TextStyle.FULL_STANDALONE,
                        Locale(currentLang)
                    ).capitalize(Locale.ROOT)
                }
                monthText.text = mMonth
                yearText.text = month.year.toString()

                (container.view.legendLayout as LinearLayout).children.map { it as TextView }
                    .forEachIndexed { index, tv ->
                        tv.text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale(currentLang))
                            .capitalize()
                    }

            }
        }

        yearLeft.setOnClickListener {
            currentMonth = YearMonth.of(currentMonth.year - 1, currentMonth.month)
            setup()
        }
        yearRight.setOnClickListener {
            currentMonth = YearMonth.of(currentMonth.year + 1, currentMonth.month)
            setup()
        }
        monthLeft.setOnClickListener {
            currentMonth = YearMonth.of(currentMonth.year, currentMonth.month - 1)
            setup()
        }
        monthRight.setOnClickListener {
            currentMonth = YearMonth.of(currentMonth.year, currentMonth.month + 1)
            setup()
        }
        proceed_button.setOnClickListener {
            viewModel.onProceedButtonClicked()
        }


    }

    private fun setup() {
        val firstMonth = currentMonth.minusMonths(0)
        val lastMonth = currentMonth.plusMonths(0)
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)
    }

}
