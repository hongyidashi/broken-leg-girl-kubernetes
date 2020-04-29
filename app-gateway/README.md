# app-gateway

## 介绍
  &emsp;&emsp;服务网关  
  &emsp;&emsp;更新于2020-4-29

## 软件架构
  &emsp;&emsp;采用 kubernetes + Spring Cloud Gateway

## 安装教程
  &emsp;&emsp;提交config-map，将其部署到k8s即可使用

## 使用网关
  ### 路由配置
  1. 参数说明
   - routes：路由
   - id：路由id，保持唯一
   - uri：路由到微服务的uri，目标微服务请求地址
   - predicates：断言（判断条件）
   - filters：过滤器
   
  2. 补充说明
   - 若要调用不同命名空间的资源，需要采用如 `uri: http://app-demo-datasource.demo:8080` 的方式，即采用 `http://` 前缀并使用全路径名+端口号的方式
   - 同一个命名空间的资源间请求则只需 `lb://` + 资源名即可
   -  `filters: - StripPrefix=1` 的作用是截断原始请求路径，使用数字表示要截断的请求路径的数量
   
  ### 限流配置
  1. 在路由信息下加一个过滤器，名字必须为 `RequestRateLimiter`
  2. `redis-rate-limiter.replenishRate`：允许用户每秒处理多少个请求
  3. `redis-rate-limiter.burstCapacity`：令牌桶的容量，允许在一秒钟内完成的最大请求数
  4. `key-resolver`：使用SpEL按名称引用bean，具体使用的bean可以在 `config.GatewayConfig` 中配置
   
  ### 重试机制
  1. 参数说明
   - name：Retry
   - retries：重试次数，默认值是3次
   - series：状态码配置（分段），符合的某段状态码才会进行重试逻辑，默认值是`SERVER_ERROR`，值是5，也就是5XX(5开头的状态码)
   - statuses：状态码配置，和series不同的是这边是具体状态码的配置 
   - methods：指定哪些方法的请求需要进行重试逻辑，默认值是 `GET` 方法 
   - exceptions：指定哪些异常需要进行重试逻辑，默认值是`java.io.IOException`