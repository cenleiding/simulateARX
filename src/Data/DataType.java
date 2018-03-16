package Data;

import org.omg.CORBA.PUBLIC_MEMBER;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.text.*;
import java.util.*;

/**
 * 框架所支持的数据类型
 * @Author CLD
 * @Date 2018/3/12 15:34
 **/
public abstract class DataType<T> implements Serializable,Comparator<T> {

    public static final String NULL_VALUE = "NULL";

    public static final String ANY_VALUE = "*";

    private static final long serialVersionUID = 1L;

    /** 时间类型数据默认格式 dd.mm.yyyy */
    public static final DataType<Date>               DATE    = new ARXDate();

    /**十进制数据类型*/
    public static final DataType<Double>             DECIMAL = new ARXDecimal();

    /**Integer数据类型*/
    public static final DataType<Long>               INTEGER = new ARXInteger();

    /**String数据类型*/
    public static final DataType<String>             STRING  = new ARXString();

    /**有序String数据类型*/
    public static final DataType<String>             ORDERED_STRING  = new ARXOrderedString();


    /**
     * 数据类型的格式
     * Locale 用于软件本地化
     */
    public interface DataTypeWithFormat {
        String getFormat();
        Locale getLocale();
    }

    /**
     * 数据类型的各个方法
     * @param <T>
     */
    public interface DataTypeWithRatioScale<T> {

        T add(T augend, T addend);

        int compare(String s1, String s2) throws NumberFormatException,ParseException;

        int compare(T t1, T t2);

        String divide(String dividend, String divisor);

        T divide(T dividend, T divisor);

        String format(T t);

        T fromDouble(Double d);

        DataTypeDescription<T> getDescription();

        T getMaximum();

        T getMinimum();

        boolean isValid(String s);

        String multiply(String multiplicand, String multiplicator);

        T multiply(T multiplicand, double multiplicator);

        T multiply(T multiplicand, int multiplicator);

        T multiply(T multiplicand, T multiplicator);

        T parse(String s);

        double ratio(T dividend, T divisor);

        T subtract(T minuend, T subtrahend);

        Double toDouble(T t);
    }

    /**
     * 数据类型的条目。
     * @param <T>
     */
    public static abstract class DataTypeDescription<T> implements Serializable {

        /** SVUID */
        private static final long serialVersionUID = 6369986224526795419L;

        /** The wrapped java class. */
        private Class<?>          clazz;

        /** If yes, a list of available formats. */
        private List<String>      exampleFormats;

        /** Can the type be parameterized with a format string. */
        private boolean           hasFormat;

        /** A human readable label. */
        private String            label;

        /** The associated scale of measure*/
        private DataScale         scale;

        /**
         * Internal constructor.
         *
         * @param clazz
         * @param label
         * @param scale
         * @param hasFormat
         * @param exampleFormats
         */
        private DataTypeDescription(Class<T> clazz,
                                    String label,
                                    DataScale scale,
                                    boolean hasFormat,
                                    List<String> exampleFormats) {
            this.clazz = clazz;
            this.label = label;
            this.scale = scale;
            this.hasFormat = hasFormat;
            this.exampleFormats = exampleFormats;
        }

        /**
         * Returns a list of example formats.
         *
         * @return
         */
        public List<String> getExampleFormats() {
            return exampleFormats;
        }

        /**
         * Returns a human readable label.
         *
         * @return
         */
        public String getLabel() {
            return label;
        }

        /**
         * Scale
         * @return
         */
        public DataScale getScale() {
            return scale;
        }

        /**
         * Returns the wrapped java class.
         *
         * @return
         */
        public Class<?> getWrappedClass() {
            return clazz;
        }

        /**
         * Returns whether the type be parameterized with a format string. Note that every data type
         * can be instantiated without a format string, using a default format.
         * @return
         */
        public boolean hasFormat() {
            return hasFormat;
        }

        /**
         * Creates a new instance with default format string and default locale.
         *
         * @return
         */
        public abstract DataType<T> newInstance();

        /**
         * Creates a new instance with the given format string and default locale.
         *
         * @param format
         * @return
         */
        public abstract DataType<T> newInstance(String format);

        /**
         * Creates a new instance with the given format string and the given locale.
         *
         * @param format
         * @param locale
         * @return
         */
        public abstract DataType<T> newInstance(String format, Locale locale);

        /**
         * Creates a new instance with default format and the given locale.
         *
         * @param locale
         * @return
         */
        public DataType<T> newInstance(Locale locale) {
            return newInstance(null, locale);
        }
    }

    @Override
    public abstract DataType<T> clone();

    /**
     * Compares two values. The result is 0 if both values are equal,
     * less than 0 if the first value is less than the second argument,
     * and greater than 0 if the first value is greater than the second argument.
     * @param s1
     * @param s2
     * @return
     * @throws NumberFormatException
     * @throws ParseException
     */
    public abstract int compare(String s1, String s2) throws NumberFormatException, ParseException;

    /**
     * Compare.
     *
     * @param t1
     * @param t2
     * @return
     */
    public abstract int compare(T t1, T t2);

//    /**
//     * Returns a new function builder.
//     *
//     * @return
//     */
//    public AggregateFunctionBuilder<T> createAggregate(){
//        return AggregateFunction.forType(this);
//    }

    @Override
    public abstract boolean equals(Object other);

    /**
     * Converts a value into a string.
     *
     * @param t
     * @return
     */
    public abstract String format(T t);

    /**
     * Returns a description of the data type.
     *
     * @return
     */
    public abstract DataTypeDescription<T> getDescription();

    @Override
    public abstract int hashCode();

    /**
     * Checks whether the given string conforms to the data type's format.
     *
     * @param s
     * @return
     */
    public abstract boolean isValid(String s);

    /**
     * Converts a string into a value.
     *
     * @param s
     * @return
     */
    public abstract T parse(String s);

    /**
     * Date类型数据
     */
    public static class ARXDate extends DataType<Date> implements DataTypeWithFormat,DataTypeWithRatioScale<Date>{

        private static final long serialVersionUID = 1L;

        /** SimpleDateFormat为DateFormat子类用于进行时间格式的转换*/
        private final SimpleDateFormat format;

        private final Locale locale;

        private final String string;

        private static final DataTypeDescription<Date> description = new DataTypeDescription<Date>(Date.class, "Date/Time",  DataScale.INTERVAL, true, listDateFormats()){
            private static final long serialVersionUID = 1L;
            @Override public DataType<Date> newInstance() { return DATE; }
            @Override public DataType<Date> newInstance(String format) {return createDate(format);}
            @Override public DataType<Date> newInstance(String format, Locale locale) {return createDate(format, locale);}
        };

        /**
         * 构造函数
         * 默认"dd.MM.yyyy"格式时间和默认地址
         */
        private ARXDate() {
            this("Default");
        }

        private ARXDate(final String formatString) {
            if (formatString == null || formatString.equals("Default")) {
                this.string = "dd.MM.yyyy";
                this.format = new SimpleDateFormat(string);
                this.locale = null;
            } else {
                this.format = new SimpleDateFormat(formatString);
                this.string = formatString;
                this.locale = null;
            }
        }

        private ARXDate(String formatString, Locale locale) {
            if (formatString == null || formatString.equals("Default")) {
                this.string = "dd.MM.yyyy";
                this.format = new SimpleDateFormat(string, locale);
                this.locale = locale;
            } else {
                this.format = new SimpleDateFormat(formatString, locale);
                this.string = formatString;
                this.locale = locale;
            }
        }

        @Override
        public Date add(Date augend, Date addend) {
            long d1 = augend.getTime();
            long d2 = addend.getTime();
            return new Date(d1 + d2);
        }

        @Override
        public DataType<Date> clone() {
            return this;
        }

        @Override
        public int compare(Date t1, Date t2){
            if(t1==null && t2==null){
                return 0;
            }else if(t1==null){
                return +1;
            }else if(t2==null){
                return -1;
            }
            return t1.compareTo(t2);
        }

        @Override
        public int compare(String s1,String s2) throws ParseException{
            try {
                Date t1=parse(s1);
                Date t2=parse(s2);
                if(t1==null && t2==null){
                    return 0;
                }else if(t1==null){
                    return +1;
                }else if(t2==null){
                    return -1;
                }
                return t1.compareTo(t2);
            }catch (Exception e){
                throw new IllegalArgumentException("Invalid value", e);
            }
        }

        @Override
        public Date divide(Date dividend,Date divisor){
            long t1=dividend.getTime();
            long t2=divisor.getTime();
            return new Date(t1/t2);
        }

        @Override
        public String divide(String dividend, String divisor) {
            long d1 = parse(dividend).getTime();
            long d2 = parse(divisor).getTime();
            return format(new Date(d1 / d2));
        }

        @Override
        public boolean equals(final Object obj){
            if(this == obj) return true;
            if(obj ==null) return false;
            if(this.getClass() != obj.getClass())return false;
            final ARXDate other = (ARXDate) obj;
            if (string == null) { if (other.string != null) { return false; }
            } else if (!string.equals(other.string)) { return false; }
            if (getLocale() == null) { if (other.getLocale() != null) { return false; }
            } else if (!getLocale().equals(other.getLocale())) { return false; }
            return true;
        }

        @Override
        public String format(Date s) {
            if (s == null) {
                return NULL_VALUE;
            }
            return format.format(s);
        }

        /**TimeZone可以用来获取或者规定时区,也可以用来计算时差*/
        public String format(Date s, TimeZone zone){
            if (s == null) {
                return NULL_VALUE;
            }
            SimpleDateFormat sdf = format;
            if (zone != null) {
                sdf = (SimpleDateFormat) format.clone();
                sdf.setTimeZone(zone);
            }
            return sdf.format(s);
        }

        @Override
        public Date fromDouble(Double d) {
            if (d == null) {
                return null;
            } else {
                return new Date(Math.round(d));
            }
        }

        @Override
        public DataTypeDescription<Date> getDescription(){
            return description;
        }

        @Override
        public String getFormat() {
            return string;
        }

        public Locale getLocale() {
            if (this.locale == null) {
                return Locale.getDefault();
            } else {
                return locale;
            }
        }

        @Override
        public Date getMaximum() {
            return new Date(Long.MAX_VALUE);
        }

        @Override
        public Date getMinimum() {
            return new Date(Long.MIN_VALUE);
        }

        /**hashcode用于查找，equals相同hashcode一定相同，反之不一定*/
        @Override
        public int hashCode() {
            if (string == null) {
                return getLocale().hashCode();
            }
            else {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((string == null) ? 0 : string.hashCode());
                result = prime * result + ((getLocale() == null) ? 0 : getLocale().hashCode());
                return result;
            }
        }

        @Override
        public boolean isValid(String s) {
            try {
                parse(s);
                return true;
            } catch (Exception e){
                return false;
            }
        }

        @Override
        public Date multiply(Date multiplicand, Date multiplicator) {
            long d1 = multiplicand.getTime();
            long d2 = multiplicator.getTime();
            return new Date(d1 * d2);
        }

        @Override
        public Date multiply(Date multiplicand, int multiplicator) {
            long d1 = multiplicand.getTime();
            return new Date(d1 * multiplicator);
        }

        @Override
        public Date multiply(Date multiplicand, double multiplicator) {
            long d1 = multiplicand.getTime();
            return new Date((long)((double)d1 * multiplicator));
        }

        @Override
        public String multiply(String multiplicand, String multiplicator) {
            long d1 = parse(multiplicand).getTime();
            long d2 = parse(multiplicator).getTime();
            return format(new Date(d1 * d2));
        }

        @Override
        public Date parse(String s) {
            if(s.length() == NULL_VALUE.length() && s.toUpperCase().equals(NULL_VALUE)) {
                return null;
            }
            try {
                ParsePosition pos = new ParsePosition(0);
                Date parsed = format.parse(s, pos);
                if (pos.getIndex() != s.length() || pos.getErrorIndex() != -1) {
                    throw new IllegalArgumentException("Parse error");
                }
                return parsed;
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage() + ": " + s, e);
            }
        }

        @Override
        public double ratio(Date dividend, Date divisor) {
            long d1 = dividend.getTime();
            long d2 = divisor.getTime();
            return (double)d1 / (double)d2;
        }

        @Override
        public Date subtract(Date minuend, Date subtrahend) {
            long d1 = minuend.getTime();
            long d2 = subtrahend.getTime();
            return new Date(d1 - d2);
        }

        @Override
        public Double toDouble(Date date) {
            return (date == null) ? null : new Long(date.getTime()).doubleValue();
        }

        @Override
        public String toString() {
            return "Date(" + string + ")";
        }


    }

    /**
     * 十进制类型数据
     */
    public static class ARXDecimal extends DataType<Double> implements DataTypeWithFormat, DataTypeWithRatioScale<Double> {

        private static final long serialVersionUID = 1L;

        private final DecimalFormat format;

        private final String                             string;

        private final Locale                             locale;

        private static final DataTypeDescription<Double> description = new DataTypeDescription<Double>(Double.class, "Decimal", DataScale.RATIO, true, listDecimalFormats()){
            private static final long serialVersionUID =1L;
            @Override public DataType<Double> newInstance() { return DECIMAL; }
            @Override public DataType<Double> newInstance(String format) {return createDecimal(format);}
            @Override public DataType<Double> newInstance(String format, Locale locale) {return createDecimal(format, locale);}
        };

        private ARXDecimal(){
            this("Default");
        }

        private ARXDecimal(String format){
            if (format == null || format.equals("Default")){
                this.format = null;
                this.string = null;
                this.locale = null;
            } else {
                this.format = new DecimalFormat(format);
                this.string = format;
                this.locale = null;
            }
        }

        private ARXDecimal(String format, Locale locale) {
            if (format == null || format.equals("Default")){
                this.format = null;
                this.string = null;
                this.locale = locale;
            } else {
                this.format = new DecimalFormat(format, new DecimalFormatSymbols(locale));
                this.string = format;
                this.locale = locale;
            }
        }

        @Override
        public Double add(Double augend, Double addend) {
            return parse(format(augend + addend));
        }

        @Override
        public DataType<Double> clone() {
            return this;
        }

        @Override
        public int compare(Double t1, Double t2) {
            if (t1 == null && t2 == null) {
                return 0;
            } else if (t1 == null) {
                return +1;
            } else if (t2 == null) {
                return -1;
            }
            double d1 = parse(format(t1));
            double d2 = parse(format(t2));
            d1 = d1 == -0.0d ? 0d : d1;
            d2 = d2 == -0.0d ? 0d : d2;
            return Double.valueOf(d1).compareTo(Double.valueOf(d2));
        }

        @Override
        public int compare(final String s1, final String s2) throws NumberFormatException {

            try {
                Double d1 = parse(s1);
                Double d2 = parse(s2);
                if (d1 == null && d2 == null) {
                    return 0;
                } else if (d1 == null) {
                    return +1;
                } else if (d2 == null) {
                    return -1;
                }
                d1 = d1.doubleValue() == -0.0d ? 0d : d1;
                d2 = d2.doubleValue() == -0.0d ? 0d : d2;
                return d1.compareTo(d2);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid value: '"+s1+"' or '"+s2+"'", e);
            }
        }

        @Override
        public Double divide(Double dividend, Double divisor) {
            return parse(format(dividend / divisor));
        }

        @Override
        public String divide(String dividend, String divisor) {
            Double d1 = parse(dividend);
            Double d2 = parse(divisor);
            return format(d1 / d2);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) { return true; }
            if (obj == null) { return false; }
            if (getClass() != obj.getClass()) { return false; }
            final ARXDecimal other = (ARXDecimal) obj;
            if (string == null) { if (other.string != null) { return false; }
            } else if (!string.equals(other.string)) { return false; }
            if (getLocale() == null) { if (other.getLocale() != null) { return false; }
            } else if (!getLocale().equals(other.getLocale())) { return false; }
            return true;
        }

        @Override
        public String format(Double s){
            if (s == null) {
                return NULL_VALUE;
            }
            if (format==null){
                return String.valueOf(s);
            } else {
                return format.format(s);
            }
        }

        @Override
        public Double fromDouble(Double d) {
            if (d == null) {
                return null;
            } else {
                return d;
            }
        }

        @Override
        public DataTypeDescription<Double> getDescription(){
            return description;
        }

        @Override
        public String getFormat() {
            return string;
        }

        public Locale getLocale() {
            if (this.locale == null) {
                return Locale.getDefault();
            } else {
                return locale;
            }
        }

        @Override
        public Double getMaximum() {
            return Double.MAX_VALUE;
        }

        @Override
        public Double getMinimum() {
            return -Double.MAX_VALUE;
        }

        @Override
        public int hashCode() {
            if (string==null) {
                return getLocale().hashCode();
            }
            else {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((string == null) ? 0 : string.hashCode());
                result = prime * result + ((getLocale() == null) ? 0 : getLocale().hashCode());
                return result;
            }
        }

        @Override
        public boolean isValid(String s) {
            try {
                parse(s);
                return true;
            } catch (Exception e){
                return false;
            }
        }

        @Override
        public Double multiply(Double multiplicand, double multiplicator) {
            return parse(format(multiplicand * multiplicator));
        }

        @Override
        public Double multiply(Double multiplicand, Double multiplicator) {
            return parse(format(multiplicand * multiplicator));
        }

        @Override
        public Double multiply(Double multiplicand, int multiplicator) {
            return parse(format(multiplicand* multiplicator));
        }

        @Override
        public String multiply(String multiplicand, String multiplicator) {
            Double d1 = parse(multiplicand);
            Double d2 = parse(multiplicator);
            return format(d1 * d2);
        }

        @Override
        public Double parse(String s) {
            if(s.length() == NULL_VALUE.length() && s.toUpperCase().equals(NULL_VALUE)) {
                return null;
            }
            try {
                if (format == null) {
                    return Double.valueOf(s);
                } else {
                    ParsePosition pos = new ParsePosition(0);
                    double parsed = format.parse(s, pos).doubleValue();
                    if (pos.getIndex() != s.length() || pos.getErrorIndex() != -1) {
                        throw new IllegalArgumentException("Parse error");
                    }
                    return parsed;
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage() + ": " + s, e);
            }
        }

        @Override
        public double ratio(Double dividend, Double divisor) {
            return dividend / divisor;
        }

        @Override
        public Double subtract(Double minuend, Double subtrahend) {
            return parse(format(minuend - subtrahend));
        }

        @Override
        public Double toDouble(Double val) {
            return val;
        }

        @Override
        public String toString() {
            return "Decimal";
        }
    }

    /**
     *整型数据
     *
     * @author Fabian Prasser
     */
    public static class ARXInteger extends DataType<Long> implements DataTypeWithFormat, DataTypeWithRatioScale<Long>  {

        private static final long serialVersionUID = 1L;

        private static final DataTypeDescription<Long> description = new DataTypeDescription<Long>(Long.class, "Integer", DataScale.RATIO, false, new ArrayList<String>()){
            private static final long serialVersionUID = -4498725217659811835L;
            @Override public DataType<Long> newInstance() { return INTEGER; }
            @Override public DataType<Long> newInstance(String format) {return createInteger(format);}
            @Override public DataType<Long> newInstance(String format, Locale locale) {return createInteger(format, locale);}
        };

        private final DecimalFormat format;

        private final String string;

        private final Locale locale;

        private ARXInteger(){
            this("Default");
        }

        private ARXInteger(String format){
            if (format == null || format.equals("Default")){
                this.format = null;
                this.string = null;
                this.locale = null;
            } else {
                this.format = new DecimalFormat(format);
                this.string = format;
                this.locale = null;
            }
        }

        private ARXInteger(String format, Locale locale){
            if (format == null || format.equals("Default")){
                this.format = null;
                this.string = null;
                this.locale = locale;
            } else {
                this.format = new DecimalFormat(format, new DecimalFormatSymbols(locale));
                this.string = format;
                this.locale = locale;
            }
        }

        @Override
        public Long add(Long augend, Long addend) {
            return augend + addend;
        }

        @Override
        public DataType<Long> clone() {
            return this;
        }

        @Override
        public int compare(Long t1, Long t2) {
            if (t1 == null && t2 == null) {
                return 0;
            } else if (t1 == null) {
                return +1;
            } else if (t2 == null) {
                return -1;
            }
            return t1.compareTo(t2);
        }

        @Override
        public int compare(final String s1, final String s2) throws NumberFormatException {

            try {
                Long d1 = parse(s1);
                Long d2 = parse(s2);
                if (d1 == null && d2 == null) {
                    return 0;
                } else if (d1 == null) {
                    return +1;
                } else if (d2 == null) {
                    return -1;
                }
                return d1.compareTo(d2);

            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage() + ": " + s1 +" or " + s2, e);
            }
        }

        @Override
        public Long divide(Long dividend, Long divisor) {
            return (long)Math.round((double)dividend / (double)divisor);
        }

        @Override
        public String divide(String dividend, String divisor) {
            Long d1 = parse(dividend);
            Long d2 = parse(divisor);
            return format(d1 / d2);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) { return true; }
            if (obj == null) { return false; }
            if (getClass() != obj.getClass()) { return false; }
            final ARXInteger other = (ARXInteger) obj;
            if (string == null) { if (other.string != null) { return false; }
            } else if (!string.equals(other.string)) { return false; }
            if (getLocale() == null) { if (other.getLocale() != null) { return false; }
            } else if (!getLocale().equals(other.getLocale())) { return false; }
            return true;
        }

        @Override
        public String format(Long s){
            if (s == null) {
                return NULL_VALUE;
            }
            if (format==null){
                return String.valueOf(s);
            } else {
                return format.format(s);
            }
        }

        @Override
        public Long fromDouble(Double d) {
            if (d == null) {
                return null;
            } else {
                return Math.round(d);
            }
        }

        @Override
        public DataTypeDescription<Long> getDescription(){
            return description;
        }

        @Override
        public String getFormat() {
            return string;
        }

        public Locale getLocale() {
            if (this.locale == null) {
                return Locale.getDefault();
            } else {
                return locale;
            }
        }

        @Override
        public Long getMaximum() {
            return Long.MAX_VALUE;
        }

        @Override
        public Long getMinimum() {
            return Long.MIN_VALUE;
        }

        @Override
        public int hashCode() {
            if (string == null) {
                return getLocale().hashCode();
            }
            else {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((string == null) ? 0 : string.hashCode());
                result = prime * result + ((getLocale() == null) ? 0 : getLocale().hashCode());
                return result;
            }
        }

        @Override
        public boolean isValid(String s) {
            try {
                parse(s);
                return true;
            } catch (Exception e){
                return false;
            }
        }

        @Override
        public Long multiply(Long multiplicand, double multiplicator) {
            return (long)((double)multiplicand * multiplicator);
        }

        @Override
        public Long multiply(Long multiplicand, int multiplicator) {
            return multiplicand * multiplicator;
        }

        @Override
        public Long multiply(Long multiplicand, Long multiplicator) {
            return (long)Math.round((double)multiplicand * (double)multiplicator);
        }

        @Override
        public String multiply(String multiplicand, String multiplicator) {
            Long d1 = parse(multiplicand);
            Long d2 = parse(multiplicator);
            return format(d1 * d2);
        }

        @Override
        public Long parse(String s) {
            if(s.length() == NULL_VALUE.length() && s.toUpperCase().equals(NULL_VALUE)) {
                return null;
            }
            try {
                if (format == null) {
                    return Long.valueOf(s);
                } else {
                    return format.parse(s).longValue();
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage() + ": " + s, e);
            }
        }

        @Override
        public double ratio(Long dividend, Long divisor) {
            return (double)dividend / (double)divisor;
        }

        @Override
        public Long subtract(Long minuend, Long subtrahend) {
            return minuend - subtrahend;
        }

        @Override
        public Double toDouble(Long val) {
            return (val == null) ? null : val.doubleValue();
        }

        @Override
        public String toString() {
            return "Integer";
        }
    }

    /**
     * 有序字符串组类型
     */
    public static class ARXOrderedString extends DataType<String> implements DataTypeWithFormat {

        private static final long serialVersionUID = 1L;

        private final Map<String, Integer> order;

        private static final DataTypeDescription<String> description = new DataTypeDescription<String>(String.class, "Ordinal", DataScale.ORDINAL, true, new ArrayList<String>()){
            private static final long serialVersionUID = -6300869938311742699L;
            @Override public DataType<String> newInstance() { return ORDERED_STRING; }
            @Override public DataType<String> newInstance(String format) {return createOrderedString(format);}
            @Override public DataType<String> newInstance(String format, Locale locale) {return createOrderedString(format);}
        };

        private ARXOrderedString(){
            this("Default");
        }

        private ARXOrderedString(List<String> format){
            if (format.size()==0) {
                this.order = null;
            } else {
                this.order = new HashMap<String, Integer>();
                for (int i=0; i< format.size(); i++){
                    if (this.order.put(format.get(i), i) != null) {
                        throw new IllegalArgumentException("Duplicate value '"+format.get(i)+"'");
                    }
                }
            }
        }

        private ARXOrderedString(String format){
            if (format==null || format.equals("Default") || format.equals("")) {
                this.order = null;
            } else {
                try {
                    this.order = new HashMap<String, Integer>();
                    BufferedReader reader = new BufferedReader(new StringReader(format));
                    int index = 0;
                    String line = reader.readLine();
                    while (line != null) {
                        if (this.order.put(line, index) != null) {
                            throw new IllegalArgumentException("Duplicate value '"+line+"'");
                        }
                        line = reader.readLine();
                        index++;
                    }
                    reader.close();
                } catch (IOException e) {
                    throw new IllegalArgumentException("Error reading input data");
                }
            }
        }

        /** @param format Ordered list of strings */
        private ARXOrderedString(String[] format){
            if (format.length == 0) {
                this.order = null;
            } else {
                this.order = new HashMap<String, Integer>();
                for (int i=0; i< format.length; i++){
                    if (this.order.put(format[i], i) != null) {
                        throw new IllegalArgumentException("Duplicate value '"+format[i]+"'");
                    }
                }
            }
        }

        @Override
        public DataType<String> clone() {
            return this;
        }

        @Override
        public int compare(String s1, String s2) {

            s1 = parse(s1);
            s2 = parse(s2);
            if (s1 == null && s2 == null) {
                return 0;
            } else if (s1 == null) {
                return +1;
            } else if (s2 == null) {
                return -1;
            }
            if (order != null){
                try {
                    return order.get(s1).compareTo(order.get(s2));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid value", e);
                }
            } else {
                return s1.compareTo(s2);
            }
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) { return true; }
            if (obj == null) { return false; }
            if (getClass() != obj.getClass()) { return false; }
            if (this.order == null) {
                if (((ARXOrderedString)obj).order != null) {
                    return false;
                }
            } else {
                if (!((ARXOrderedString)obj).order.equals(this.order)) {
                    return false;
                }
            }
            return true;
        }

        public String format(String s){
            if (s == null) {
                return NULL_VALUE;
            }
            if (order != null && !order.containsKey(s)) {
                throw new IllegalArgumentException("Unknown string '"+s+"'");
            }
            return s;
        }

        public DataTypeDescription<String> getDescription(){
            return description;
        }

        public List<String> getElements() {
            List<String> result = new ArrayList<String>();
            if (order == null) {
                return result;
            }
            result.addAll(order.keySet());
            Collections.sort(result, new Comparator<String>(){
                @Override public int compare(String arg0, String arg1) {
                    return order.get(arg0).compareTo(order.get(arg1));
                }
            });
            return result;
        }

        @Override
        public String getFormat() {
            if (order == null) return "";
            List<String> list = new ArrayList<String>();
            list.addAll(order.keySet());
            Collections.sort(list, new Comparator<String>(){
                @Override
                public int compare(String arg0, String arg1) {
                    return order.get(arg0).compareTo(order.get(arg1));
                }
            });
            StringBuilder b = new StringBuilder();
            for (int i=0; i<list.size(); i++) {
                b.append(list.get(i));
                if (i<list.size()-1) {
                    b.append("\n");
                }
            }
            return b.toString();
        }

        public Locale getLocale() {
            return Locale.getDefault();
        }

        @Override
        public int hashCode() {
            return ARXOrderedString.class.hashCode();
        }

        public boolean isValid(String s) {
            if (s.length() == NULL_VALUE.length() && s.toUpperCase().equals(NULL_VALUE)) {
                return true;
            } else if (order != null && !order.containsKey(s)) {
                return false;
            } else {
                return true;
            }
        }

        public String parse(String s) {
            if(s.length() == NULL_VALUE.length() && s.toUpperCase().equals(NULL_VALUE)) {
                return null;
            }
            if (order != null && !order.containsKey(s)) {
                throw new IllegalArgumentException("Unknown string '"+s+"'");
            }
            return s;
        }

        @Override
        public String toString() {
            return "Ordinal";
        }
    }

    /**
     * 字符串类型
     */
    public static class ARXString extends DataType<String> {

        private static final long serialVersionUID = 1L;

        private static final DataTypeDescription<String> description = new DataTypeDescription<String>(String.class, "String", DataScale.NOMINAL, false, new ArrayList<String>()){
            private static final long serialVersionUID = -6679110898204862834L;
            @Override public DataType<String> newInstance() { return STRING; }
            @Override public DataType<String> newInstance(String format) {return STRING;}
            @Override public DataType<String> newInstance(String format, Locale locale) {return STRING;}
        };

        @Override
        public DataType<String> clone() {
            return this;
        }

        @Override
        public int compare(final String s1, final String s2) {
            if (s1 == null || s2 == null) {
                throw new IllegalArgumentException("Null is not a string");
            }
            return s1.compareTo(s2);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) { return true; }
            if (obj == null) { return false; }
            if (getClass() != obj.getClass()) { return false; }
            return true;
        }

        public String format(String s){
            if (s == null) {
                throw new IllegalArgumentException("Null is not a string");
            }
            return s;
        }

        public DataTypeDescription<String> getDescription(){
            return description;
        }

        @Override
        public int hashCode() {
            return ARXString.class.hashCode();
        }

        public boolean isValid(String s) {
            return s != null;
        }

        public String parse(String s) {
            if (s == null) {
                throw new IllegalArgumentException("Null is not a string");
            }
            return s;
        }

        @Override
        public String toString() {
            return "String";
        }
    }

    /** 生成时间数据 */
    public static final DataType<Date> createDate(final String format) {
        return new ARXDate(format);
    }

    public static final DataType<Date> createDate(final String format, final Locale locale) {
        return new ARXDate(format, locale);
    }

    /** 生成十进制数据 */
    public static final DataType<Double> createDecimal(final String format) {
        return new ARXDecimal(format);
    }

    public static DataType<Double> createDecimal(String format, Locale locale) {
        return new ARXDecimal(format, locale);
    }

    /** 生成整型数据*/
    public static final DataType<Long> createInteger(final String format) {
        return new ARXInteger(format);
    }

    public static final DataType<Long> createInteger(final String format, Locale locale) {
        return new ARXInteger(format, locale);
    }

    /** 生成有序数据*/
    public static final DataType<String> createOrderedString(final List<String> format) {
        return new ARXOrderedString(format);
    }

    public static final DataType<String> createOrderedString(final String format) {
        return new ARXOrderedString(format);
    }

    public static final DataType<String> createOrderedString(final String[] format) {
        return new ARXOrderedString(format);
    }

    /**
     *判断是否为ANY值
     * @param value
     * @return
     */
    public static final boolean isAny(String value) {
        return value != null && value.equals(ANY_VALUE);
    }

    /**
     * 判断是否为NULL值
     * @param value
     * @return
     */
    public static final boolean isNull(String value) {
        return value == null || (value.length() == NULL_VALUE.length() && value.toUpperCase().equals(NULL_VALUE));
    }

    /**
     * 获得所有数据类型列表
     * @return
     */
    public static final List<DataTypeDescription<?>> list(){
        List<DataTypeDescription<?>> list = new ArrayList<DataTypeDescription<?>>();
        list.add(STRING.getDescription());
        list.add(ORDERED_STRING.getDescription());
        list.add(DATE.getDescription());
        list.add(DECIMAL.getDescription());
        list.add(INTEGER.getDescription());
        return list;
    }

    /**
     *
     * Returns a datatype for the given class.
     * @param <U>
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final <U> DataTypeDescription<U> list(Class<U> clazz){
        for (DataTypeDescription<?> entry : list()) {
            if (entry.getWrappedClass() == clazz) {
                return (DataTypeDescription<U>)entry;
            }
        }
        return null;
    }

    /**
     * Provides a list of example formats for the <code>Date</code> data type.
     *
     * @return
     */
    private static List<String> listDateFormats(){
        List<String> result = new ArrayList<String>();
        result.add("yyyy-MM-dd'T'HH:mm:ss'Z'");
        result.add("yyyy-MM-ddZZ");
        result.add("yyyy-MM-dd'T'HH:mm:ssz");
        result.add("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        result.add("EEE MMM d hh:mm:ss z yyyy");
        result.add("EEE MMM dd HH:mm:ss yyyy");
        result.add("EEEE, dd-MMM-yy HH:mm:ss zzz");
        result.add("EEE, dd MMM yyyy HH:mm:ss zzz");
        result.add("EEE, dd MMM yy HH:mm:ss z");
        result.add("EEE, dd MMM yy HH:mm z");
        result.add("EEE, dd MMM yyyy HH:mm:ss z");
        result.add("yyyy-MM-dd'T'HH:mm:ss");
        result.add("EEE, dd MMM yyyy HH:mm:ss Z");
        result.add("dd MMM yy HH:mm:ss z");
        result.add("dd MMM yy HH:mm z");
        result.add("'T'HH:mm:ss");
        result.add("'T'HH:mm:ssZZ");
        result.add("HH:mm:ss");
        result.add("HH:mm:ssZZ");
        result.add("yyyy-MM-dd");
        result.add("yyyy-MM-dd hh:mm:ss");
        result.add("yyyy-MM-dd HH:mm:ss");
        result.add("yyyy-MM-dd'T'HH:mm:ssz");
        result.add("yyyy-MM-dd'T'HH:mm:ss");
        result.add("yyyy-MM-dd'T'HH:mm:ssZZ");
        result.add("dd.MM.yyyy");
        result.add("dd.MM.yyyy hh:mm:ss");
        result.add("dd.MM.yyyy HH:mm:ss");
        result.add("dd.MM.yyyy'T'HH:mm:ssz");
        result.add("dd.MM.yyyy'T'HH:mm:ss");
        result.add("dd.MM.yyyy'T'HH:mm:ssZZ");
        result.add("dd.MM.yyyy hh:mm");
        result.add("dd.MM.yyyy HH:mm");
        result.add("dd/MM/yyyy");
        result.add("dd/MM/yy");
        result.add("MM/dd/yyyy");
        result.add("MM/dd/yy");
        result.add("MM/dd/yyyy hh:mm:ss");
        result.add("MM/dd/yy hh:mm:ss");
        return result;
    }

    /**
     * Provides a list of example formats for the <code>Decimal</code> data type.
     *
     * @return
     */
    private static List<String> listDecimalFormats(){
        List<String> result = new ArrayList<String>();
        result.add("0.###");
        result.add("0.00");
        result.add("#,##0.###");
        result.add("#,##0.00");
        result.add("#,##0");
        result.add("#,##0%");

        // Create list of common patterns
        Set<String> set = new HashSet<String>();
        set.addAll(result);
        for (Locale locale: NumberFormat.getAvailableLocales()) {
            for (NumberFormat format : new NumberFormat[] { NumberFormat.getNumberInstance(locale),
                    NumberFormat.getIntegerInstance(locale),
                    NumberFormat.getCurrencyInstance(locale),
                    NumberFormat.getPercentInstance(locale) }) {

                // Add pattern
                if (format instanceof DecimalFormat) {
                    String pattern = ((DecimalFormat)format).toPattern();
                    if (!set.contains(pattern)) {
                        set.add(pattern);
                        result.add(pattern);
                    }
                }
            }

        }
        return result;
    }


}
