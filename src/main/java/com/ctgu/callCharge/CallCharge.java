package com.ctgu.callCharge;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CallCharge {
    // 错误信息
    private static final String ERROR_INFO = "Invalid Date";
    // 是否发生了时制转换
    private boolean isTransform;

    public void setTransform(boolean transform) {
        isTransform = transform;
    }

    /**
     * 计算通话费用
     *
     * @param startDate 开始打电话的时刻
     * @param endDate   结束打电话的时刻
     * @return 通话费用
     * @throws ParseException
     */
    public double charge(String startDate, String endDate) throws ParseException {
        // 将输入的时间字符串转化为Calendar对象
        Calendar startCalendar = convertDate(startDate);
        Calendar endCalendar = convertDate(endDate);
        int year = startCalendar.get(Calendar.YEAR);
        // 判断传入的参数是否符合要求
        if (!isCorrectDate(startCalendar) || !isCorrectDate(endCalendar) || (!isTransform && startCalendar.getTimeInMillis() > endCalendar.getTimeInMillis())) {
            throw new IllegalArgumentException(ERROR_INFO);
        }
        // 得到时间差
        long duration = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
        // 得到4月的第一个星期天
        Calendar firstSundayCalendar = sunday(year, 4, 1, true);
        // 得到10月的最后一个星期天
        Calendar lastSundayCalendar = sunday(year, 10, 31, false);
        // 计算因为时制转换要变化的时间
        long diffValue = differenceValue(startCalendar, endCalendar, firstSundayCalendar, lastSundayCalendar);
        long total = duration + diffValue;
        // 转化为分钟
        long totalMinute = total % 60000 == 0 ? total / 60000 : total / 60000 + 1;
        // 计算花费
        double pay = calCost(totalMinute);
        return pay;
    }

    /**
     * 获得因为时制转化而变化的时间
     *
     * @param start 开始打电话的时间
     * @param end   结束打电话的时间
     * @param first 4月的第一个星期天
     * @param last  10月的最后一个星期天
     * @return 变化的时间
     */
    public long differenceValue(Calendar start, Calendar end, Calendar first, Calendar last) {
        long startTime = start.getTimeInMillis();
        long endTime = end.getTimeInMillis();
        // 2:00:00
        long firstTimeStart = first.getTimeInMillis();
        // 3:00:00
        long firstTimeEnd = firstTimeStart + 3600000;
        // 2:00:00
        long lastTimeStart = last.getTimeInMillis();
        // 2:59:59
        long lastTimeEnd = lastTimeStart + 3600000 - 1000;
        // 判断是否是4月第一个星期天的2点-3点时间段
        if (startTime <= firstTimeStart && endTime >= firstTimeEnd) {
            return -3600000;
        }
        // 判断是否在10月最后一个星期天的2点到2点59时间段
        if ((startTime >= lastTimeStart && startTime <= lastTimeEnd || endTime >= lastTimeStart && endTime <= lastTimeEnd || startTime <= lastTimeStart && endTime >= lastTimeEnd) && isTransform) {
            return 3600000;
        }
        return 0;
    }

    /**
     * 输入通话时间，计算应付金额
     *
     * @param minute 通话时间
     * @return 通话费用
     */
    public Double calCost(long minute) {
        Double cost = 0d;
        if (minute <= 20) {
            cost = minute * 0.05;
        } else {
            cost = 1 + (minute - 20) * 0.1;
        }
        return cost;
    }

    /**
     * 计算4月的第一个星期天或10月的最后一个星期天
     *
     * @param year    某年
     * @param month   某月
     * @param day     某天
     * @param isFirst true: 第一个星期天; false: 最后一个星期天
     * @return 该星期天的Calendar对象
     * @throws ParseException
     */
    public Calendar sunday(int year, int month, int day, boolean isFirst) throws ParseException {
        // 基姆拉尔森计算公式
        int w = (day + 2 * month + 3 * (month + 1) / 5 + year + year / 4 - year / 100 + year / 400) % 7 + 1;
        int sunday = isFirst ? day + 7 - w : day - w;
        Calendar cal = convertDate(year + "-" + month + "-" + sunday + " 02:00:00");
        return cal;
    }

    /**
     * 计算某年某月有多少天
     *
     * @param year  某年
     * @param month 某月
     * @return 该月的天数
     */
    public int dayOfMonth(int year, int month) {
        int two = 28;
        if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
            two = 29;
        }
        int day = 31;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = two;
                break;
            default:
                day = 30;
        }
        return day;
    }

    /**
     * 判断输入的日期是否有效
     *
     * @param cal Calendar对象
     * @return 是否有效
     */
    public boolean isCorrectDate(Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        /**
         * 1. 年份不得小于0
         * 2. 月份在1-12
         * 3. 天数在1-31
         * 4. 小时在0-24
         * 5. 分钟在0-60
         * 6. 秒在0-60
         */
        if (!(year > 0) || !(1 <= month && month <= 12) || !(1 <= day && day <= 31) && !(0 <= hour && hour <= 24) && !(0 <= minute && minute <= 60) && !(0 <= second && second <= 60)) {
            return false;
        }
        // 判断月份和天数是否一致
        int d = dayOfMonth(year, month);
        if (day > d) {
            return false;
        }
        return true;
    }

    /**
     * 将输入的日期字符串转化为Calendar对象
     *
     * @param str 一个日期字符串
     * @return 一个Calendar对象
     * @throws ParseException
     */
    public Calendar convertDate(String str) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(str));
        return cal;
    }
}
