kubectl delete -f kubernetes-mysql.yaml
kubectl delete -f elastic.yaml
kubectl delete -f kubernetes-redis.yaml
kubectl delete -f kubernetes-amqp.yaml
kubectl delete -f kubernetes.yaml
kubectl create -f kubernetes.yaml
kubectl create -f kubernetes-mysql.yaml
kubectl create -f kubernetes-redis.yaml
kubectl create -f kubernetes-amqp.yaml
kubectl create -f elastic.yaml