# LCN分布式事务框架v4.0 for springboot2.0.x

  "LCN并不生产事务，LCN只是本地事务的协调者啊"

## 框架介绍   

  LCN分布式事务框架的核心功能是对本地事务的协调控制，框架本身并不创建事务，只是对本地事务做协调控制。因此该框架与其他第三方的框架兼容性强，支持所有的关系型数据库事务，支持多数据源，支持与第三方数据库框架一块使用（例如 sharding-jdbc），在使用框架的时候只需要添加分布式事务的注解即可，对业务的侵入性低。LCN框架主要是为微服务框架提供分布式事务的支持，在微服务框架上做了进一步的事务机制优化，在一些负载场景上LCN事务机制要比本地事务机制的性能更好，4.0以后框架开方了插件机制可以让更多的第三方框架支持进来。


## 官方网址

[https://www.txlcn.org](https://www.txlcn.org)


## 框架特点

1. 支持各种基于spring的db框架
2. 兼容SpringCloud、Dubbo、motan
3. 使用简单，低依赖，代码完全开源
4. 基于切面的强一致性事务框架
5. 高可用，模块可以依赖RPC模块做集群化，TxManager也可以做集群化
6. 支持本地事务和分布式事务共存
7. 支持事务补偿机制，增加事务补偿决策提醒
8. 添加插件拓展机制


## 原理介绍

[原理介绍](https://github.com/codingapi/tx-lcn/wiki)  [视频讲解](https://www.txlcn.org/v4/index.html)

## 目录说明

extensions feign扩展配置类

springcloud-lcn-demo demo案例

transaction-springcloud LCN springcloud rpc框架扩展支持

tx-client 是LCN核心tx模块端控制框架

tx-manager 是LCN 分布式事务协调器

tx-plugins-db 是LCN 对关系型数据库的插件支持


## 使用说明

分布式事务发起方：

```
    @Override
    @TxTransaction(isStart=true)
    @Transactional
    public boolean hello() {
        //本地调用
        testDao.save();
        //远程调用方
        boolean res =  test2Service.test();
        //模拟异常
        int v = 100/0;
        return true;
    }
```

分布式事务被调用方(test2Service的业务实现类)
```
    @Override
    @Transactional
    @TxTransaction
    public boolean test() {
        //本地调用
        testDao.save();
        return true;
    }
```

如上代码执行完成以后两个模块都将回滚事务。

说明：在使用LCN分布式事务时，只需要将事务的开始方法添加`@TxTransaction(isStart=true)`注解即可,在参与方添加`@TxTransaction`或者实现`ITxTransaction`接口即可。详细见demo教程

## 关于@TxTransaction 使用说明

  @TxTransaction注解是分布式事务的标示。
  
  若存在业务方法：a->b b->c b->d，那么开启分布式事务注解的话，需要在各个模块方法上添加@TxTransaction即可。
  
```
    @TxTransaction(isStart=true)
    @Transactional
    public void a(){
        b();
    }
    
    @TxTransaction
    public void b(){
        c();
        d();
    }
    
    @TxTransaction
    public void c(){}
    
    @TxTransaction
    public void d(){}
```

## maven 中心库地址


```
<dependency>
    <groupId>com.codingapi</groupId>
    <artifactId>tx-client</artifactId>
    <version>${lcn.last.version}</version>
</dependency>

<dependency>
    <groupId>com.codingapi</groupId>
    <artifactId>tx-plugins-db</artifactId>
    <version>${lcn.last.version}</version>
</dependency>

<dependency>
    <groupId>com.codingapi</groupId>
    <artifactId>transaction-springcloud</artifactId>
    <version>${lcn.last.version}</version>
</dependency>    
        
```

## demo演示教程

[springcloud版本](https://github.com/yizhishang/tx-lcn/tree/springcloud-2.0.4/springcloud-lcn-demo)

软件准备
ElasticSearch：elasticsearch-5.0.0.tar.gz

Logstash：logstash-5.0.0.tar.gz

Kibana：kibana-5.0.0-linux-x86_64.tar.gz

jdk8：jdk-8u201-linux-x64.tar.gz（附带snmp.acl）

以上文件请在公司的ftp目录下获取，path:\\10.112.61.23\允许上传\elk相关

安装JDK
解压jdk8到路径：/usr/java/jdk1.8.0_201/

将snmp.acl文件复制到 /usr/java/jdk1.8.0_201/jre/lib/management

安装ElasticSearch
创建elk用户（es必须非root用户启动，es的配置/启动都以elk用户完成）

groupadd elk
useradd -g elk elk
passwd elk


解压elasticsearch-5.0.0.tar.gz到/usr/local/elk/elasticsearch-5.0.0

修改配置文件/config/elasticsearch.yml，设置集群名称、节点名称、ip和端口（改为9207）

资产管理平台 > 测试环境ELK简易搭建指南 > es配置文件.png

资产管理平台 > 测试环境ELK简易搭建指南 > es配置文件2.png

修改jvm配置/config/jvm.options，把jvm的大小调整为2G

资产管理平台 > 测试环境ELK简易搭建指南 > es修改jvm大小.png

修改启动文件/bin/elasticsearch，首行增加以下配置

JAVA_HOME="/usr/java/jdk1.8.0_201/"
JAVA_OPTS=""


启动es

$ /usr/local/elk/elasticsearch-5.0.0/bin/elasticsearch &
如果出现这个异常：

java.lang.UnsupportedOperationException: seccomp unavailable: CONFIG_SECCOMP not compiled into kernel, CONFIG_SECCOMP and CONFIG_SECCOMP_FILTER are needed
	at org.elasticsearch.bootstrap.SystemCallFilter.linuxImpl(SystemCallFilter.java:342) ~[elasticsearch-5.6.16.jar:5.6.16]
	at org.elasticsearch.bootstrap.SystemCallFilter.init(SystemCallFilter.java:617) ~[elasticsearch-5.6.16.jar:5.6.16]
	at org.elasticsearch.bootstrap.JNANatives.tryInstallSystemCallFilter(JNANatives.java:258) [elasticsearch-5.6.16.jar:5.6.16]
	at org.elasticsearch.bootstrap.Natives.tryInstallSystemCallFilter(Natives.java:113) [elasticsearch-5.6.16.jar:5.6.16]
	at org.elasticsearch.bootstrap.Bootstrap.initializeNatives(Bootstrap.java:111) [elasticsearch-5.6.16.jar:5.6.16]
	at org.elasticsearch.bootstrap.Bootstrap.setup(Bootstrap.java:195) [elasticsearch-5.6.16.jar:5.6.16]
	at org.elasticsearch.bootstrap.Bootstrap.init(Bootstrap.java:342) [elasticsearch-5.6.16.jar:5.6.16]
	at org.elasticsearch.bootstrap.Elasticsearch.init(Elasticsearch.java:132) [elasticsearch-5.6.16.jar:5.6.16]
	at org.elasticsearch.bootstrap.Elasticsearch.execute(Elasticsearch.java:123) [elasticsearch-5.6.16.jar:5.6.16]
	at org.elasticsearch.cli.EnvironmentAwareCommand.execute(EnvironmentAwareCommand.java:70) [elasticsearch-5.6.16.jar:5.6.16]
	at org.elasticsearch.cli.Command.mainWithoutErrorHandling(Command.java:134) [elasticsearch-5.6.16.jar:5.6.16]
	at org.elasticsearch.cli.Command.main(Command.java:90) [elasticsearch-5.6.16.jar:5.6.16]
	at org.elasticsearch.bootstrap.Elasticsearch.main(Elasticsearch.java:91) [elasticsearch-5.6.16.jar:5.6.16]
	at org.elasticsearch.bootstrap.Elasticsearch.main(Elasticsearch.java:84) [elasticsearch-5.6.16.jar:5.6.16]


增加以下配置

bootstrap.memory_lock: false
bootstrap.system_call_filter: false
如果出现以下提示：
资产管理平台 > 测试环境ELK简易搭建指南 > image2019-5-23_18-38-59.png

需要修改系统的连接数，在/etc/sysctl.conf 文件中直接修改，增加配置项

vm.max_map_count=262144


安装Logstash
解压到/usr/local/elk/logstash-5.0.0/

修改config/jvm.options，增加以下配置：

-Dcom.sun.management.snmp.port=8046


修改bin/logstash.lib.sh，首行增加以下配置：

export JAVA_CMD="/usr/java/jdk1.8.0_201/bin"
export JAVA_HOME="/usr/java/jdk1.8.0_201/"


修改/config/logstash.conf，配置es和grok表达式（以下配置为ampots的日志收集脚本，仅供参考）

input {
 file {
   type => "ampots-test1"
   path => "/usr/local/tomcat/logs/catalina.out"
   codec => multiline {
     pattern => "^\s"
     what => "previous"
   }
   start_position => "beginning"
 }
}
filter {
 grok {
   match => { "message" => "(?<rpcType>[RPC\-IN]{6})\](?<RPC-IN>.*)" }
 }
 grok {
   match => { "message" => "(?<rpcType>[RPC\-VEH]{7})\](?<RPC-VEH>.*)" }
 }
 grok {
   match => { "message" => "(?<rpcType>[RPC\-ORG]{7})\](?<RPC-ORG>.*)" }
 }
 grok {
   match => { "message" => "(?<rpcType>[RPC\-TCS]{7})\](?<RPC-TCS>.*)" }
 }
 grok {
   match => { "message" => "(?<rpcType>[RPC\-OUT]{7})\](?<RPC-OUT>.*)" }
 }
 grok {
   match => { "message" => "(?<rpcType>[RPC\-SYS]{7})\](?<RPC-SYS>.*)" }
 }
 grok {
   match => { "message" => "(?<rpcType>[RPC\-FCAR]{8})\](?<RPC-FCAR>.*)" }
 }
 grok {
   match => { "message" => "(?<rpcType>[RPC\-MMC]{7})\](?<RPC-MMC>.*)" }
 }
 grok {
   match => { "message" => ".*\.(?<errorType>(\w+Exception))" } 
 }
 grok {
   match => { "message" => "\=\>\s(?<serviceId>(\w+.*))\s执行耗时\s\=\>\s(?<executeTime>.*)" } 
 }
 mutate {
   convert => ["executeTime", "integer"]
 }
}
output {
 elasticsearch { 
   hosts => ["10.104.111.161:9207"] 
   action => "index"
   codec => rubydebug
   index => "%{type}-%{+YYYY.MM.dd}"
   template_name => "%{type}"
 }
}
资产管理平台 > 测试环境ELK简易搭建指南 > logstash配置1.png
资产管理平台 > 测试环境ELK简易搭建指南 > logstash配置2.png

启动logstash

nohup /usr/local/elk/logstash-5.0.0/bin/logstash -f /usr/local/elk/logstash-5.0.0/config/logstash.conf &


安装Kibana
解压到/usr/local/elk/kibana-5.0.0-linux-x86_64

修改/config/kibana.yml

资产管理平台 > 测试环境ELK简易搭建指南 > kibana配置.png

启动kibana

/usr/local/elk/kibana-5.0.0-linux-x86_64/bin/kibana &


启动后，打开Kibana面板，配置index
资产管理平台 > 测试环境ELK简易搭建指南 > kibana配置2.png点击create即可生成日志

最后
1、其他机器要采集日志，只需安装logstash并修改logstash.conf文件将日志推送到es即可

2、防止日志文件过大，es按日期每天生成一个实例，请定期清理es的日志实例（可用脚本实现）

安装过程中如果出现问题，请联系我，欢迎指正和补充！