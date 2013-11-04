/*
 * Copyright (C) 2012 The Light OpenSource Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Lunar.java source code is only for Chinese users to calculate Lunar Date
 * and Festivals.
 *
 */


package com.android.calendar.lunar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

public class Lunar {
    private int year;
    private int month;
    private int day;
    private int dow;
    private int m;
    private int d;
    private int ye;
    private boolean leap;
    private Context mContext;
    private String[] chineseNumber;
    private String[] lunarMonthName;
    private SimpleDateFormat chineseDateFormat;
    private static final double D = 0.2422;
    private final static Map<String, Integer[]> INCREASE_OFFSETMAP = new HashMap<String, Integer[]>();
    private final static Map<String, Integer[]> DECREASE_OFFSETMAP = new HashMap<String, Integer[]>();
    final static long[] lunarInfo = new long[] { 0x04bd8, 0x04ae0, 0x0a570,
            0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
            0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0,
            0x0ada2, 0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50,
            0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566,
            0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0,
            0x1c8d7, 0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4,
            0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550,
            0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950,
            0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260,
            0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0,
            0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,
            0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40,
            0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3,
            0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960,
            0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0,
            0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9,
            0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0,
            0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65,
            0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0,
            0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0, 0x055b2,
            0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0 };

    final static String[] Festivals = new String[] { "0101元旦", "0214情人节",  "0308妇女节", 
           "0312植树节", "0401愚人节", "0422地球日", "0501劳动节", "0504青年节", "0531无烟日",
           "0601儿童节", "0606爱眼日", "0701建党日", "0801建军节", "0910教师节", "1001国庆节",
           "1031万圣节", "1111光棍节", "1224平安夜", "1225圣诞节"};

    final static String[] lunarFestivals = new String[] { "腊月三十除夕", "正月初一春节", "正月十五元宵节",
           "五月初五端午节", "七月初七七夕节", "七月十五中元节", "八月十五中秋节", "九月初九重阳节",
           "十月十五下元节", "腊月初八腊八节", "腊月廿四小年"};

    final static String[] SolarTerms = new String[] { "立春", "雨水", "惊蛰", "春分", "清明",
           "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", 
           "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至", "小寒", "大寒"};

    static {
        DECREASE_OFFSETMAP.put("雨水", new Integer[] { 2026 });
        INCREASE_OFFSETMAP.put("春分", new Integer[] { 2084 });
        INCREASE_OFFSETMAP.put("小满", new Integer[] { 2008 });
        INCREASE_OFFSETMAP.put("芒种", new Integer[] { 1902 });
        INCREASE_OFFSETMAP.put("夏至", new Integer[] { 1928 });
        INCREASE_OFFSETMAP.put("小暑", new Integer[] { 1925, 2016 });
        INCREASE_OFFSETMAP.put("大暑", new Integer[] { 1922 });
        INCREASE_OFFSETMAP.put("立秋", new Integer[] { 2002 });
        INCREASE_OFFSETMAP.put("白露", new Integer[] { 1927 });
        INCREASE_OFFSETMAP.put("秋分", new Integer[] { 1942 });
        INCREASE_OFFSETMAP.put("霜降", new Integer[] { 2089 });
        INCREASE_OFFSETMAP.put("立冬", new Integer[] { 2089 });
        INCREASE_OFFSETMAP.put("小雪", new Integer[] { 1978 });
        INCREASE_OFFSETMAP.put("大雪", new Integer[] { 1954 });
        DECREASE_OFFSETMAP.put("冬至", new Integer[] { 1918, 2021 });
        INCREASE_OFFSETMAP.put("小寒", new Integer[] { 1982 });
        DECREASE_OFFSETMAP.put("小寒", new Integer[] { 2019 });
        INCREASE_OFFSETMAP.put("大寒", new Integer[] { 2082 });
    }

    private static final double[][] CENTURY_ARRAY = {
            { 4.6295, 19.4599, 6.3826, 21.4155, 5.59, 20.888, 6.318, 21.86, 6.5, 22.2, 7.928, 23.65, 8.35, 23.95, 8.44,
                    23.822, 9.098, 24.218, 8.218, 23.08, 7.9, 22.6, 6.11, 20.84 },
            { 3.87, 18.73, 5.63, 20.646, 4.81, 20.1, 5.52, 21.04, 5.678, 21.37, 7.108, 22.83, 7.5, 23.13, 7.646,
                    23.042, 8.318, 23.438, 7.438, 22.36, 7.18, 21.94, 5.4055, 20.12 } };

     private  int yearDays(int y) {
        int i, sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {
            if ((lunarInfo[y - 1900] & i) != 0)
                sum += 1;
        }
        return (sum + leapDays(y));
    }

     private  int leapDays(int y) {
        if (leapMonth(y) != 0) {
            if ((lunarInfo[y - 1900] & 0x10000) != 0)
                return 30;
            else
                return 29;
        } else
            return 0;
    }

     private  int leapMonth(int y) {
        return (int) (lunarInfo[y - 1900] & 0xf);
    }

     private  int monthDays(int y, int m) {
        if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0)
            return 29;
        else
            return 30;
    }

     public String animalsYear() {
        final String[] Animals =  new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
        return Animals[(year - 4) % 12];
    }

     private  String cyclicalm(int num) {
        final String[] Gan = new String[]{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
        final String[] Zhi = new String[]{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
        return (Gan[num % 10] + Zhi[num % 12]);
    }

     public String cyclical() {
        int num = year - 1900 + 36;
        return (cyclicalm(num));
    }


    public Lunar(Calendar cal,Context context) {
        @SuppressWarnings("unused")
        int yearCyl, monCyl, dayCyl;
        int leapMonth = 0;
        mContext = context;
        m = cal.get(Calendar.MONTH) + 1;
        d = cal.get(Calendar.DAY_OF_MONTH);
        dow = cal.get(Calendar.DAY_OF_WEEK) + 1;
        ye = cal.get(Calendar.YEAR);
        chineseNumber = new String[]{"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
        lunarMonthName = new String[]{"正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊"};
        String format1 = "yyyy年MM月dd日";
        chineseDateFormat = new SimpleDateFormat(format1);
        Date baseDate = null;
        try {
            String format2 = "1900年1月31日";
            baseDate = chineseDateFormat.parse(format2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int offset = (int) ((cal.getTime().getTime() - baseDate.getTime()) / 86400000L);
        dayCyl = offset + 40;
        monCyl = 14;
        int iYear, daysOfYear = 0;
        for (iYear = 1900; iYear < 2050 && offset > 0; iYear++) {
            daysOfYear = yearDays(iYear);
            offset -= daysOfYear;
            monCyl += 12;
        }
        if (offset < 0) {
            offset += daysOfYear;
            iYear--;
            monCyl -= 12;
        }
        year = iYear;
        yearCyl = iYear - 1864;
        leapMonth = leapMonth(iYear);
        leap = false;
        int iMonth, daysOfMonth = 0;
        for (iMonth = 1; iMonth < 13 && offset > 0; iMonth++) {
            if (leapMonth > 0 && iMonth == (leapMonth + 1) && !leap) {
                --iMonth;
                leap = true;
                daysOfMonth = leapDays(year);
            } else
                daysOfMonth = monthDays(year, iMonth);
            offset -= daysOfMonth;
            if (leap && iMonth == (leapMonth + 1))
                leap = false;
            if (!leap)
                monCyl++;
        }
        if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
            if (leap) {
                leap = false;
            } else {
                leap = true;
                --iMonth;
                --monCyl;
            }
        }
        if (offset < 0) {
            offset += daysOfMonth;
            --iMonth;
            --monCyl;
        }
        month = iMonth;
        day = offset + 1;
    }

    public  String getChinaDayString(int day) {
        String chineseTen[] = new String[]{"初", "十", "廿", "卅"};
        int n = day % 10 == 0 ? 9 : day % 10 - 1;
        if (day > 30)
            return "";
        else if (day == 10)
            return "初十";
        else if (day == 20)
            return "二十";
        else if (day == 30)
            return "三十";
        else
            return chineseTen[day / 10] + chineseNumber[n];
    }

    public String getFestival() {
        String lunarMonth = lunarMonthName[month - 1] + "月";
        String lunarDay = getChinaDayString(day);
        String lunarDate = lunarMonth + lunarDay;
        String MonthText = null;
        String DayText = null;
        String DateText = null;
        String ret = null;
        Calendar cal = Calendar.getInstance();
        if (m < 10) MonthText = "0" + String.valueOf(m);
        else              MonthText = String.valueOf(m);

        if (d < 10) DayText = "0" + String.valueOf(d);
        else              DayText = String.valueOf(d);

        DateText = MonthText + DayText;
        int i;
        for (i = 0; i < Festivals.length; i++) {
            if (Festivals[i].substring(0, 4).equals(DateText)) {
                ret = Festivals[i].substring(4, Festivals[i].length());
                break;
            }
        }
        if (monthDays(year, 12) == 29) lunarFestivals[0] = "腊月廿九除夕";
        else                           lunarFestivals[0] = "腊月三十除夕";
        if (ret == null) {
            for (i = 0; i < lunarFestivals.length; i++) {
                if (lunarFestivals[i].substring(0, 4).equals(lunarDate)) {
                    ret = lunarFestivals[i].substring(4, lunarFestivals[i].length());
                    break;
                }
            }
        }
        if (ret == null) {
            if (m == 5) {
                cal.set(ye, 4, 1);
                int dow1 = cal.get(Calendar.DAY_OF_WEEK);
                int secondsunday;
                if(dow1 == Calendar.SUNDAY)       secondsunday = 8;
                else secondsunday = 7 - dow1 + Calendar.SUNDAY + 8;
                if (d == secondsunday) ret = "母亲节";
            } else if (m == 6) {
                cal.set(ye, 5, 1);
                int dow1 = cal.get(Calendar.DAY_OF_WEEK);
                int thirdsunday;
                if(dow1 == Calendar.SUNDAY)       thirdsunday = 15;
                else thirdsunday = 7 - dow1 + Calendar.SUNDAY + 15;
                if (d == thirdsunday) ret = "父亲节";
            } else if (m == 11) {
                cal.set(ye, 10, 1);
                int dow1 = cal.get(Calendar.DAY_OF_WEEK);
                int forththursday;
                if(dow1 == Calendar.THURSDAY)       forththursday = 22;
                else forththursday = 7 - dow1 + Calendar.THURSDAY + 22;
                if (d == forththursday) ret = "感恩节";
            }
        }
        if (ret == null) {
            int solarday;
            switch (m) {
                case 1:
                    solarday = getSolarTermNum("小寒");
                    if (solarday == d) ret = "小寒";
                    solarday = getSolarTermNum("大寒");
                    if (solarday == d) ret = "大寒";
                    break;
                case 2:
                    solarday = getSolarTermNum("立春");
                    if (solarday == d) ret = "立春";
                    solarday = getSolarTermNum("雨水");
                    if (solarday == d) ret = "雨水";
                    break;
                case 3:
                    solarday = getSolarTermNum("惊蛰");
                    if (solarday == d) ret = "惊蛰";
                    solarday = getSolarTermNum("春分");
                    if (solarday == d) ret = "春分";
                    break;
                case 4:
                    solarday = getSolarTermNum("清明");
                    if (solarday == d) ret = "清明";
                    solarday = getSolarTermNum("谷雨");
                    if (solarday == d) ret = "谷雨";
                    break;
                case 5:
                    solarday = getSolarTermNum("立夏");
                    if (solarday == d) ret = "立夏";
                    solarday = getSolarTermNum("小满");
                    if (solarday == d) ret = "小满";
                    break;
                case 6:
                    solarday = getSolarTermNum("芒种");
                    if (solarday == d) ret = "芒种";
                    solarday = getSolarTermNum("夏至");
                    if (solarday == d) ret = "夏至";
                    break;
                case 7:
                    solarday = getSolarTermNum("小暑");
                    if (solarday == d) ret = "小暑";
                    solarday = getSolarTermNum("大暑");
                    if (solarday == d) ret = "大暑";
                    break;
                case 8:
                    solarday = getSolarTermNum("立秋");
                    if (solarday == d) ret = "立秋";
                    solarday = getSolarTermNum("处暑");
                    if (solarday == d) ret = "处暑";
                    break;
                case 9:
                    solarday = getSolarTermNum("白露");
                    if (solarday == d) ret = "白露";
                    solarday = getSolarTermNum("秋分");
                    if (solarday == d) ret = "秋分";
                    break;
                case 10:
                    solarday = getSolarTermNum("寒露");
                    if (solarday == d) ret = "寒露";
                    solarday = getSolarTermNum("霜降");
                    if (solarday == d) ret = "霜降";
                    break;
                case 11:
                    solarday = getSolarTermNum("立冬");
                    if (solarday == d) ret = "立冬";
                    solarday = getSolarTermNum("小雪");
                    if (solarday == d) ret = "小雪";
                    break;
                case 12:
                    solarday = getSolarTermNum("大雪");
                    if (solarday == d) ret = "大雪";
                    solarday = getSolarTermNum("冬至");
                    if (solarday == d) ret = "冬至";
                    break;
            }
        }
        return ret;
    }

    public String toString() {
    	String year1 = "年";
    	String run1 = "闰";
    	String month1 = "月";
        return cyclical() + animalsYear() + year1 + (leap ? run1 : "") + lunarMonthName[month - 1] + month1 + getChinaDayString(day);
    }

  public int getSolarTermNum(String name) {


        double centuryValue = 0;

        int centuryIndex = -1;
        if (ye >= 1901 && ye <= 2000) {
            centuryIndex = 0;
        } else if (ye >= 2001 && ye <= 2100) {
            centuryIndex = 1;
        } else {
            throw new RuntimeException("Not supported year：" + ye);
        }
        int i;
        int ordinal = 0;
        for (i = 0; i < SolarTerms.length; i++) {
            if (SolarTerms[i].equals(name)) {
                ordinal = i;
                break;
            }
        }
        centuryValue = CENTURY_ARRAY[centuryIndex][ordinal];
        int dateNum = 0;
        int y = ye % 100;
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            if (name == "小寒" || name == "大寒" || name == "立春" || name == "雨水") {
                y = y - 1;
            }
        }
        dateNum = (int) (y * D + centuryValue) - (int) (y / 4);
        dateNum += specialYearOffset(ye, name);
        return dateNum;
    }

    public int specialYearOffset(int year, String name) {
        int offset = 0;
        offset += getOffset(DECREASE_OFFSETMAP, year, name, -1);
        offset += getOffset(INCREASE_OFFSETMAP, year, name, 1);


        return offset;
    }


    public int getOffset(Map<String, Integer[]> map, int year, String name, int offset) {
        int off = 0;
        Integer[] years = map.get(name);
        if (null != years) {
            for (int i : years) {
                if (i == year) {
                    off = offset;
                    break;
                }
            }
        }
        return off;
    }
}
