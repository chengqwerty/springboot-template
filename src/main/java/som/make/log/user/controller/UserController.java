package som.make.log.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 一个简单的测试类，测试logback的配置
 */
@RestController
@RequestMapping("user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 测试不同级别的log
     */
    @RequestMapping()
    public String getUser() {
        logger.info("This is log info {}.", Thread.currentThread().getName());
        logger.warn("This is log warn {}.", Thread.currentThread().getName());
        logger.error("This is log error {}.", Thread.currentThread().getName());
        logger.debug("This is log debug {}.", Thread.currentThread().getName());
        return "user";
    }

}
