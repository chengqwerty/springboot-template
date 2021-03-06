# springboot-template

## 注意事项

### application.properties 配置

- app.log.path 配置日志的根目录
- app.log.prefix 配置日志文件名的前缀

### spring-logback.xml 配置

- spring web有时需要打印debug内容，比如sql语句，在springProfile标签下的logger标签配置需要打印的package eg：
  ```
  <logger name="com.sxkj.source.trace" level="debug">
    <appender-ref ref="production_debug_appender"/>
  </logger>
  
  ```
  
### spring项目启动参数

- 开发环境，添加-Dspring.profiles.active=development使用开发环境配置的日志
- 生产环境，添加-Dspring.profiles.active=production使用生成环境配置的日志
- 生成环境，可以看情况添加--app.log.path=/home/chengcheng/programs/source-trace/logs/覆盖application.properties中的配置
- -D参数在jar包的前面，--app.log.path是运行参数在jar后面。eg：

  ```
  
  nohup java -jar -XX:+UseG1GC -Dspring.profiles.active=production source-trace-0.0.1-SNAPSHOT.jar --app.log.path=/home/chengcheng/programs/source-trace/logs/ >nohup.out &

  ```
