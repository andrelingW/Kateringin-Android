package project.skripsi.kateringin.Util;

import java.text.DecimalFormat;

public class IdrFormat {
    public static String format(long value){
        DecimalFormat df = new DecimalFormat("#,###,###,###");
        return "Rp " + df.format(value) + ",00";
    }

}
