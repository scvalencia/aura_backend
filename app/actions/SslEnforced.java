package actions;

import play.mvc.With;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by scvalencia on 5/7/15.
 */
@With(SslEnforcerAction.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SslEnforced {
    SslEnforcedResponse response() default SslEnforcedResponse.SELF;
}
