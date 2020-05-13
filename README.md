# broken-leg-girl-kubernetes

# 介绍
  &emsp;&emsp;断腿少女kubernetes版
  &emsp;&emsp;2020-4-8开始开发

# 软件架构
  &emsp;&emsp;基于springboot + springcloud + kubernetes的微服务架构

# 安装教程
  
## 安装Kubernetes

### 安装Docker Desktop
本架构将采用Docker Desktop的方式来安装 Docker 和 Kubernetes。  


安装参考：https://www.jianshu.com/p/e5c056baa8ab 或 https://github.com/AliyunContainerService/k8s-for-docker-desktop  

## 调整docker下kubernetes的运行内存
在docker配置进行调整，将内存调整为8192MB。

# 使用说明

## 基础设施搭建
执行kubernetes/startKubernetes.sh或startKubernetes.bat文件用以搭建MySQL等基础设施；

## 部署前置动作
### 安装父项目
如果父项目的依赖发生变更，需要安装父项目，创建maven运行操作，执行命令：clean install -N

### 安装framework项目
如果framework发生变化，需要安装framework，执行命令：clean install

### 安装api项目
如果api发生变化，需要安装api，执行命令：clean install

## 运行和部署demo
### 本地运行app-demo-datasource
1. 项目的源目录的resources下面，执行如下文件：sh config-map/submit.sh，将配置文件提交
2. 在kubernetes-dashboard上找到mysql容器组，在控制台上执行：mysql -p888888 ，然后在项目resources下面执行database/init.sql文件内容，创建数据库和用户
3. 在VM option上添加参数-Dspring.profiles.active=dev，以表明使用的配置文件

### k8s上部署app-demo-datasource
1. 项目的源目录的resources下面，执行如下文件：sh config-map/submit.sh，将配置文件提交
2. 在kubernetes-dashboard上找到mysql容器组，在控制台上执行：mysql -p888888 ，然后在项目resources下面执行database/init.sql文件内容，创建数据库和用户
3. 执行命令：clean install fabric8:deploy -Dfabric8.generator.from=fabric8/java-jboss-openjdk8-jdk -Pkubernetes -Dfabric8.namespace=demo

### 错误日志查看
`kubectl describe pods -n blg`

## 搭建一个微服务
TODO

# 各组件说明
TODO  
[app-gateway](https://github.com/hongyidashi/broken-leg-girl-kubernetes/tree/master/app-gateway)  

# 参与贡献
  &emsp;&emsp;断腿少女  
  &emsp;&emsp;公司大佬  
  &emsp;&emsp;2020/4/9 成功搭建maven环境   
  &emsp;&emsp;2020/4/10 成功成功整合数据源，并在kubernetes上部署  
  &emsp;&emsp;2020/4/28 整合swagger2  
  &emsp;&emsp;2020/4/29 整合SpringCloud Gateway  
  
