package m.m.com.m.utils;

    /*
    /
    Allow debug options to logcat
    /
    */

public class DebugUtil {

    public static final boolean DEBUG = true;

    private static final String LOG_PREFIX = "Tag_application";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    public static void log(String message) {
        if(DebugUtil.DEBUG) {
            System.out.println("LOG: " + message);
        }
    }

    public static String makeLogTag( String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }
        return LOG_PREFIX + str;
    }

    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }


}
