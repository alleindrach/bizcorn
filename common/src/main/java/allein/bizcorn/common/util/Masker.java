package allein.bizcorn.common.util;

public class Masker {

    /**
     * 掩码缓存，当所需掩码数量在0-16范围之内的时候
     * 直接使用缓存,不需要创建内存对象，效率最高
     * <p>
     * 如果不理解这里，可以参考JDK类库的 Integer 源码 sizeTable
     */
    private static final String[] cache = {
            "*", "**", "***", "****", "*****",
            "******", "*******", "********", "*********",
            "**********", "***********", "************", "*************",
            "**************", "***************", "****************"};

    private static final char coverDefault = '*';

    /**
     * 欲掩盖的字符串
     */
    private String initialCode;
    /**
     * 掩码起始位置
     */
    private int beginIndex;
    /**
     * 掩码结束位置
     */
    private int endIndex;
    /**
     * 掩盖码，默认是 * 号
     */
    private char cover = coverDefault;

    /**
     * 无参构造器
     */
    public Masker() {
    }

    /**
     * 构造器
     *
     * @param beginIndex 初始掩码位置
     * @param endIndex   结束掩码位置
     */
    public Masker(String initialCode, int beginIndex, int endIndex) {
        this.initialCode = initialCode;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    /**
     * 构造器
     *
     * @param beginIndex 初始掩码位置
     * @param endIndex   结束掩码位置
     */
    public Masker(String initialCode, int beginIndex, int endIndex, char cover) {
        this.initialCode = initialCode;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.cover = cover;
    }

    /**
     * 提供四种静态方法（静态方法调用实例方法）
     * 两种实例方法
     */
    public static String getMaskSubWay(String initialCode, int beginIndex, int endIndex) {
        Masker Masker = new Masker(initialCode, beginIndex, endIndex);
        return Masker.getMaskSubWay();
    }

    public static String getMaskSubWay(String initialCode, int beginIndex, int endIndex, char cover) {
        Masker Masker = new Masker(initialCode, beginIndex, endIndex, cover);
        return Masker.getMaskSubWay();
    }

    public static String getMaskCharWay(String initialCode, int beginIndex, int endIndex, char cover) {
        Masker Masker = new Masker(initialCode, beginIndex, endIndex, cover);
        return Masker.getMaskCharWay();
    }

    public static String getMaskCharWay(String initialCode, int beginIndex, int endIndex) {
        Masker Masker = new Masker(initialCode, beginIndex, endIndex);
        return Masker.getMaskCharWay();
    }


    /**
     * 通过substring方式截取
     *
     * @return 完成掩码的字符串
     */
    public String getMaskSubWay() {
        if (null == initialCode || "".equals(initialCode) || "null".equals(initialCode)) {
            return "Error : variable is null. ";
        }
        return initialCode.substring(0, beginIndex - 1) +
                cover(beginIndex, endIndex, cover) +
                initialCode.substring(endIndex, initialCode.length());
    }

    /**
     * 通过char[]方式截取
     *
     * @return 完成掩码的字符串
     */
    public String getMaskCharWay() {
        if (null == initialCode || "".equals(initialCode) || "null".equals(initialCode)) {
            return "Error : variable is null. ";
        }
        char[] chars = initialCode.toCharArray();
        char[] tempBegin = new char[beginIndex - 1];
        char[] tempEnd = new char[initialCode.length() - endIndex];
        for (int varBegin = 0; varBegin < tempBegin.length; varBegin++)
        /**
         * 把掩码字符串前半段提取出来，放到一个临时变量中
         */
            tempBegin[varBegin] = chars[varBegin];
        for (int varEnd = 0; varEnd < tempEnd.length; varEnd++)
        /**
         * 把掩码字符串后半段提取出来，放到一个临时变量中
         */
            tempEnd[varEnd] = chars[endIndex + varEnd];
        /**
         * JDK系统自带的数组拷贝方法，效率要比for循环效率稍微低一些
         */
        //  System.arraycopy(chars,0,tempBegin,0,tempBegin.length);
        //  System.arraycopy(chars,0,tempEnd,0,tempEnd.length);
        return new String(tempBegin) +
                cover(beginIndex, endIndex, cover) +
                new String(tempEnd);
    }

    /**
     * 获取掩码
     *
     * @param beginIndex 起始掩码位置
     * @param endIndex   结束掩码位置
     * @return 一个掩码串
     */
    private static String cover(int beginIndex, int endIndex, char cover) {
        /**
         * 这2个值不能为负数
         */
        if (beginIndex < 0 || endIndex < 0)
            return "";
        if (beginIndex > endIndex) {
            /**
             * 方法容错
             *
             * If someone accidentally wrote it backwards
             * That doesn't matter, either
             *
             * 如果某人把参数写反了，那也没有关系
             * 程序会自动把参数切换过来
             */
            beginIndex = beginIndex ^ endIndex;
            endIndex = endIndex ^ beginIndex;
            beginIndex = beginIndex ^ endIndex;
        }
        /**
         * 如果可以使用缓存，那就使用缓存
         */
        if (endIndex - beginIndex < cache.length && cover == coverDefault)
            return cache[endIndex - beginIndex];
        /**
         * 指定 char[] 的长度是掩码的长度，最大化利用资源
         * 如果不指定，默认会是缓存最大值：16
         * 如果程序运行到这里，那么很有可能掩码数量大于16
         */
        StringBuilder sb = new StringBuilder(endIndex - beginIndex);
        for (; beginIndex <= endIndex; beginIndex++)
            sb.append(cover);
        return sb.toString();
    }

    /**
     * Getter and Setter in here
     */
    public static String[] getCache() {
        return cache;
    }

    public static char getCoverDefault() {
        return coverDefault;
    }

    public String getInitialCode() {
        return initialCode;
    }

    public void setInitialCode(String initialCode) {
        this.initialCode = initialCode;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public void setBeginIndex(int beginIndex) {
        this.beginIndex = beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public char getCover() {
        return cover;
    }

    public void setCover(char cover) {
        this.cover = cover;
    }
}