package com.example.garbage.tools;

import android.content.Context;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;

public class GarbageTools {

    public static int convertDpsTpPixels(int dps, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public static int convertAmountToInt(BigDecimal amount) {
        return amount.multiply(new BigDecimal(100)).intValue();
    }

    public static BigDecimal convertIntToAmount(int amount) {
        return new BigDecimal(amount).divide(new BigDecimal(100)).setScale(2);
    }

    public static String getFormatAmount(BigDecimal amount) {
        if (amount == null) {
            return new DecimalFormat("#,##0.00").format(BigDecimal.ZERO);
        }
        return new DecimalFormat("#,##0.00").format(amount);
    }

    public static long getCurrentMonthStartTime() {
        return getCurrentMonthStartCalendar().getTimeInMillis();
    }

    public static Calendar getCurrentMonthStartCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}