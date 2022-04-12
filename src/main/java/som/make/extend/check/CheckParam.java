package som.make.extend.check;

import java.lang.annotation.*;

@Documented
@Repeatable(CheckParams.class)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckParam {
    String name() default "";

    String[] notAllNull() default {};

    String[] notNull() default {};

    String[] notEmpty() default {};
}
