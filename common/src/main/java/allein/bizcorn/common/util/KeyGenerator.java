package allein.bizcorn.common.util;

import java.util.UUID;

public class KeyGenerator {

    /**
     * 获得一个UUID
     *
     * @return String UUID
     */
    public static String getKey() {
        String uuid = UUID.randomUUID().toString();
        //去掉“-”符号
        return uuid.replaceAll("-", "");
    }
}
