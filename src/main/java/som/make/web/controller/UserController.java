package som.make.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import som.make.extend.check.CheckParam;
import som.make.extend.wrapper.ResultBean;
import som.make.web.bean.User;

import java.util.Map;

/**
 * 一个简单的测试类
 * 1、测试logback的配置
 * 2、测试参数校验
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

    @RequestMapping("save")
    @CheckParam(notNull = {"userName"})
    public ResultBean<String> saveUser(@RequestBody User user) {
        return new ResultBean<>(ResultBean.SUCCESS, "用户保存成功");
    }

    @RequestMapping("saveMap")
    @CheckParam(notNull = {"userName"}, notAllNull = {"userId", "userCode-用户Code不能为空。&&userName-用户名称不能为空。"})
    public ResultBean<String> saveUser(@RequestBody Map<String, Object> user) {
        return new ResultBean<>(ResultBean.SUCCESS, "用户保存成功");
    }

    @RequestMapping("save2")
    @CheckParam(notNull = {"userId-用户id不能为空。"})
    @CheckParam(notAllNull = {"userCode", "loginName&&userName"})
    public ResultBean<String> saveUser2(@RequestBody User user) {
        return new ResultBean<>(ResultBean.SUCCESS, "用户保存成功");
    }

    @RequestMapping("save3")
    @CheckParam(notNull = {"userId-用户id不能为空。"})
    @CheckParam(notAllNull = {"userCode", "loginName&&userName"})
    public ResultBean<String> saveUser3(User user) {
        return new ResultBean<>(ResultBean.SUCCESS, "用户保存成功");
    }

}
