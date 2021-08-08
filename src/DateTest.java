import org.junit.Test;

import java.sql.Timestamp;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

public class DateTest {
    /**
     * 会出现异常
     */
    @Test
    public void TestSimpleDateFormatThread(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(int i=0 ; i<10 ;i++){
            new Thread(()->{
                try {
                    System.out.println(simpleDateFormat.parse("2021-08-08 00:00:00"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * 2021-08-08T01:31:34.841928Z  格林尼治时间，非当前时间
     * 2021-08-08
     * 09:31:34.862206
     * 2021-08-08T09:31:34.862258
     * 2021-08-08T09:31:34.863247+08:00[Asia/Shanghai]
     */
    @Test
    public void testNewDateClass(){
        Instant instant = Instant.now();
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        System.out.println(instant);
        System.out.println(localDate);
        System.out.println(localTime);
        System.out.println(localDateTime);
        System.out.println(zonedDateTime);

    }

    /**
     * 2021  年
     * 2021-08 年月
     * --08-08  月日
     *
     */
    @Test
    public void testYMD(){
        Year year = Year.now();
        YearMonth yearMonth = YearMonth.now();
        MonthDay monthDay = MonthDay.now();

        System.out.println(year);
        System.out.println(yearMonth);
        System.out.println(monthDay);
    }

    /**
     * 通过 of 创建日期
     */
    @Test
    public void ofDate(){
        LocalDate date = LocalDate.of(2020, 8, 8);
        System.out.println(date); //2020-08-08

        LocalTime localTime = LocalTime.of(9, 44, 11);
        System.out.println(localTime); //09:44:11

        LocalDateTime localDateTime = LocalDateTime.of(2020, 8, 8, 11, 12, 1);
        System.out.println(localDateTime);  //2020-08-08T11:12:01

    }

    /**
     * 时区遍历
     */
    @Test
    public void timeZoneList(){
        Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
        //遍历
       // availableZoneIds.stream().forEach(System.out::println);
        System.out.println("当前系统时区：");
        ZoneId zoneId = ZoneId.systemDefault(); // Asia/Shanghai
        System.out.println(zoneId);
    }

    /**
     * 为localdatetime设置时区
     */
    @Test
    public void setZoneIdToLocalDateTime(){
        LocalDateTime localDateTime = LocalDateTime.of(2020,11,11,11,11,11);
        System.out.println(localDateTime);
        //通过 localdatetime atZone() 方法放回zonedDateTime
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        System.out.println(zonedDateTime);

        //查看当前时间的其他时区的时间
        ZonedDateTime zonedDateTimetk = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
        System.out.println(zonedDateTimetk);
    }

    @Test
    public void TestMontEnum(){
        LocalDateTime of = LocalDateTime.of(2011, Month.NOVEMBER, 11, 11, 1, 1);
        System.out.println(of);
    }

    /**
     * 时间偏移
     *1微秒等于一百万分之一秒（10-6秒）
     * 0.000 001 微秒 = 1皮秒
     * 0.001 微秒 = 1纳秒
     * 1,000 微秒 = 1毫秒
     * 1,000,000 微秒 = 1秒
     */
    @Test
    public void timeMove(){
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);
        // 1000_000 java 支持的数字定义 1000000
        LocalDateTime localDateTimeNew = localDateTime.plusDays(1).plusMonths(-1).minusMonths(-1).plusHours(24).plusNanos(1000_000);
        System.out.println(localDateTimeNew);

        //只要是有时分秒就可以设置纳秒localDateTimeNew
        LocalDateTime localDateTime1 = LocalDateTime.now();
        System.out.println(localDateTime1); //2021-08-08T10:15:59.227091   227091 这个单位是微秒
        System.out.println(localDateTime1.plusNanos(1000_000)); //2021-08-08T10:15:59.228091
    }

    /**
     * 通过period修改时间
     */
    @Test
    public void timeperiod(){
        LocalDateTime localDateTime = LocalDateTime.now().withDayOfMonth(31);
        System.out.println(localDateTime);
        Period period = Period.of(1,1,1);
        LocalDateTime plus = localDateTime.plus(period);
        System.out.println(plus);

        //基础时间单元
        LocalDateTime plus1 = plus.plus(1, ChronoUnit.DECADES);
        System.out.println(plus1);

        //也可以用with 修改
        LocalDateTime localDateTime1 = plus1.withYear(1998);
        System.out.println(localDateTime1);
        //也可以用temporalField 效果一样
        LocalDateTime with = plus1.with(ChronoField.YEAR, 1998);
        System.out.println(with);
    }


    /**
     * 通过TemporalAdjusters修改时间
     */
    @Test
    public void timeTemporalAdjusters(){
        LocalDate localDate = LocalDate.now();
        LocalDate with = localDate.with(TemporalAdjusters.firstDayOfMonth());
        System.out.println(with);

        //下一个周一
        LocalDate with1 = localDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        System.out.println(with1);
    }

    /**
     * 自定义TemporalAdjusters
     */
    @Test
    public void defineTemporalAdjusters(){
        //当月发薪水日期
        TemporalAdjuster temporalAdjuster = (temporal -> {
            //通过from 方法转出成相应的类型
            LocalDate date = LocalDate.from(temporal);
            LocalDate localDate = date.withDayOfMonth(15);
            if(localDate.getDayOfWeek() == DayOfWeek.SATURDAY || localDate.getDayOfWeek() == DayOfWeek.SUNDAY){
                return localDate.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
            }else{
                return  localDate;
            }
        });

        LocalDate localDate = LocalDate.now().with(temporalAdjuster);
        System.out.println(localDate);

//        注意异常，上面的方法无法自动转换成localDateTime
//        localDateLocalDateTime localDateTime = LocalDateTime.now().with(temporalAdjuster);
//        System.out.println(localDateTime);
    }


    /**
     * 查询距离国庆还有多少天
     * ChronoUnit 类
     */
    @Test
    public void testqueryFrom(){
        TemporalQuery<Long> query = (temporal -> {
            LocalDate from = LocalDate.from(temporal);
            LocalDate guoqing = LocalDate.of(from.getYear(), Month.OCTOBER, 1);
            if(guoqing.isAfter(from)){
               return ChronoUnit.DAYS.between(from,guoqing);
            }else{
                LocalDate localDate = guoqing.plusYears(1);
                return ChronoUnit.DAYS.between(from,localDate);
            }
        });

        Long query1 = LocalDate.now().query(query);
        System.out.println(query1);
     }

    /**
     * 老时间转换成新时间
     */
     @Test
    public void testOldDateTrans(){
        //java.util.date
         java.util.Date date = new Date();
         //先转换成instant
         Instant instant = date.toInstant();
         ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
         LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
         //zonedDateTime.toLocalDate();
//         zonedDateTime.toLocalTime();
         System.out.println(localDateTime);

         java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
         LocalDate localDate = sqlDate.toLocalDate();
         System.out.println(localDate);

         java.sql.Timestamp timestamp = new Timestamp(System.currentTimeMillis());
         LocalDateTime localDateTime1 = timestamp.toLocalDateTime();
         System.out.println(localDateTime1);

         Calendar calendar = Calendar.getInstance();
         TimeZone timeZone = calendar.getTimeZone();
         ZoneId zoneId = timeZone.toZoneId();
         System.out.println(timeZone);
         System.out.println(zoneId);
         ZonedDateTime zonedDateTime1 = ZonedDateTime.ofInstant(calendar.toInstant(),zoneId);
         System.out.println(zonedDateTime1);

     }

    /**
     * 自带的格式化
     * 21:32:45.776148
     * 2021-08-08
     * 2021-08-08T21:32:45.776148
     * 2021-W31-7
     * 2021-08-08T21:32:45.776148
     */
    @Test
    public void formateNewDate(){
        LocalDateTime localDateTime = LocalDateTime.now();
         System.out.println(localDateTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
         System.out.println(localDateTime.format(DateTimeFormatter.ISO_DATE));
         System.out.println(localDateTime.format(DateTimeFormatter.ISO_DATE_TIME));
         System.out.println(localDateTime.format(DateTimeFormatter.ISO_WEEK_DATE));
         System.out.println(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
     }

    /**
     * 2021年8月8日星期日
     * 2021年8月8日
     * 2021年8月8日
     * 2021/8/8
     */
    @Test
     public void definNewDateFormate(){
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));
        System.out.println(now.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
        System.out.println(now.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        System.out.println(now.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
    }

    /**
     * pattern 模式
     */
    @Test
    public void patterFormat(){
        LocalDateTime now = LocalDateTime.now();
        String format = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(format);
    }
}
