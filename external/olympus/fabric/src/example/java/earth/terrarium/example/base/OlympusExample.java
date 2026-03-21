package earth.terrarium.example.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OlympusExample {

    /**
     * The unique identifier of the example.
     */
    String id();

    /**
     * The description of the example.
     */
    String description();
}
