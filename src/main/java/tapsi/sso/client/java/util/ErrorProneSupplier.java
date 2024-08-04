package tapsi.sso.client.java.util;

@FunctionalInterface
public interface ErrorProneSupplier<T> {
    T get() throws Exception;
}
