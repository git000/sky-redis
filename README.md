## sky-redis / redis实战
### 参考文章
https://github.com/redisson/redisson

### 第三方组件
redisson:3.15.2

### 目录说明
1. doc包括redis多种模式的配置文件

### 单节点
#### 安装及启动redis
docker run -d --name redis -p 6300:6379 redis:5.0 --requirepass "123456"

### 启动工程
SkyRedisApplication，启动single环境

### 主从节点
#### 规划

|  服务器IP | 端口 | 备注  |
| ---- | ---- | ---- |
| 172.100.0.11    |  6379 | 主   |
| 172.100.0.12     | 6379 | 从   |
| 172.100.0.13     | 6379 | 从   |
| 172.100.0.14     | 6379 | 从   |

#### 安装及启动redis
1. 在doc目录中1master3slave中修改磁盘路径
   
2.在1master3slave修改redis-s.conf中的slaveof 改成本地地址

3.然后在1master3slave中执行docker-compose



