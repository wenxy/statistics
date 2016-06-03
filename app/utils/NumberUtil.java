package utils;

import java.text.NumberFormat;
import java.util.regex.Pattern;

/**
 * 数字相关的类
 * Created by roy@warthog.cn on 2015/4/8.
 */
public class NumberUtil {

    /**
     * 判断包装类是否是null或0
     * @param t
     * @param <T>
     * @return
     */
    public static <T> boolean isNullOrZero(T t) {
        if (t == null) {
            return true;
        }

        if (t.getClass().isAssignableFrom(Integer.class)) {
            return ((Integer)t).intValue() == 0;
        } else if (t.getClass().isAssignableFrom(Long.class)) {
            return ((Long)t).longValue() == 0;
        } else if (t.getClass().isAssignableFrom(Double.class)) {
            return ((Double)t).doubleValue() == 0;
        } else if (t.getClass().isAssignableFrom(Short.class)) {
            return ((Short)t).shortValue() == 0;
        }

        return false;
    }

    public static boolean isNumeric(String str){ 
        Pattern pattern = Pattern.compile("[0-9]*"); 
        return pattern.matcher(str).matches();    
     } 

    
    final static char[] digits = {'0','1'};
    
    public static String toBinaryString(int i) {
        char[] buf = new char[32];
        int pos = 32;
        int mask = 1;
        do {
            buf[--pos] = digits[i & mask];
            i >>>= 1;
        } while (pos > 0);
         
        return new String(buf, pos, 32);
    }
    
    public static int intValue(String v, int def) {
        if (v == null || v.length() == 0) {
            return def;
        }
        try {
            return Integer.parseInt(v.trim());
        } catch (Exception e) {
            return def;
        }
    }
    
    /**
     * 保留小数点后4位
     * @param d
     * @return
     */
    public static String format(double d){
    	if(d == 0){
    		return "0.00";
    	}
    	java.text.DecimalFormat  df=new   java.text.DecimalFormat("##.####");   
    	return df.format(d);
    }
    
    /**
     * 百分数
     * @param d
     * @return
     */
    public static String formatP(double d){
    	if(d == 0){
    		return "0%";
    	}
    	NumberFormat nt = NumberFormat.getPercentInstance();
    	nt.setMinimumFractionDigits(2);
    	return nt.format(d);
    }
    
    public static void main(String[] args){
    	System.out.print(formatP(0.0002));
    }
}
