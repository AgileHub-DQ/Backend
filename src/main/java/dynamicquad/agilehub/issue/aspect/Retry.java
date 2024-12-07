package dynamicquad.agilehub.issue.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {
    int maxRetries() default 5;

    long delay() default 100;

    Class<? extends Throwable>[] retryFor() default Exception.class;
}
