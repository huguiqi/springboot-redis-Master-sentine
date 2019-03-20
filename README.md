# redis高可用主从复制+哨兵模式

## docker搭建redis主从+哨兵高可用

docker-compose:

    version: '3'
    services:
        master:
            image: registry.cn-shanghai.aliyuncs.com/rios/redis
            container_name: master
            command: /bin/bash -c "chmod 644 /usr/local/etc/redis/redis.conf && redis-server /usr/local/etc/redis/redis.conf"
            volumes:
                - ./master_data:/data
                - ./redis-master.conf:/usr/local/etc/redis/redis.conf
            ports:
              - "6379:6379"
            restart: always
        slave6380:
            image: registry.cn-shanghai.aliyuncs.com/rios/redis
            container_name: slave01
            ports:
              - "6380:6379"
            restart: always
            command: /bin/bash -c "sleep 2 && redis-server --slaveof master 6379"
            volumes:
                - ./slave01_data:/data
                - ./redis-slave.conf:/usr/local/etc/redis/redis.conf
            depends_on: 
              - master
        redis-sentinel:
            image: registry.cn-shanghai.aliyuncs.com/rios/redis
            container_name: redis-sentinel
            ports:
              - "6382:6379"
            restart: always
            command: /bin/bash -c "sleep 2 && chmod 644 /etc/redis/sentinel.conf && redis-sentinel /etc/redis/sentinel.conf"
            volumes:
                - ./sentinel_data:/data
                - ./redis-sentinel.conf:/etc/redis/sentinel.conf
            links: 
              - master
    
    networks:
        default:
          external:
              name: riosNetwork


## springboot连接高可用redis

必须通过docker访问redis，否则会出现操作redis读写时报redis connection timeout

原因可能是client端与docker服务器的不在同一个网络的问题，暂时未找到解决方法，网上有说通过配置hosts方式指向docker服务器内网，经测试无效，可能与我的情况不一样吧。
暂时无解，只能通过同网络的docker容器访问。


构建java容器：

    mvn clean package docker:build

![paste image](http://blog.huguiqi.com/1552815327766vzrd4sag.png?imageslim)     
     
通过镜像启动容器：

    docker run --name=appredis --network riosNetwork -p 9999:8888 -d e60534acc998
    


 ![paste image](http://blog.huguiqi.com/1552815969221h6qhh35v.png?imageslim)

[参考文章](https://juejin.im/post/5b7d226a6fb9a01a1e01ff64)


## 实现redis消息队列(手动消费)

[要实现的流程](https://www.processon.com/view/link/5c8f3a96e4b0ab74ecdbc8de)


[redis实现队列原理](https://www.kancloud.cn/hfpp2012/redisbook/467188)


现在要做的就是，设计一个对象，将它作为list的中的对象元素push进去，而redis里的数据基本上都是用String存储，那就将对象转成json字符串(需要将object转成字符流)存到redis，取出来时将string再转成object


具体看代码 。。。。。。
   