docker exec -it redis-c1 /bin/sh

redis-cli -a 123456 --cluster create 172.100.0.11:6379 172.100.0.12:6379 172.100.0.13:6379 172.100.0.14:6379 172.100.0.15:6379 172.100.0.16:6379 --cluster-replicas 1