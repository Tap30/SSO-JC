package tapsi.sso.client.java.util;

public class ErrorProneFunctionalInterfaceExecutor {

    public static <T> T getOrDefault(ErrorProneSupplier<T> supplier, T defaultValue) {
        try {
            return getOrDefault(supplier.get(), defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static <T> T getOrDefault(T nullableValue, T fallbackValue) {
        return nullableValue == null ? fallbackValue : nullableValue;
    }

    public static void ignoreException(ErrorProneRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception ignore) {
        }
    }

}