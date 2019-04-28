package allein.bizcorn.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtil {
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    public static boolean isUrl(String url){
        String regex = "http(s)?://([\\w-]+\\.)+[\\w-]+(\\:[\\d]+)*(/[\\w- ./?%&=]*)?";
        return match(regex, url);
    }
}
