#!/usr/bin/env bash

kubectl delete -f mysql.yaml
kubectl delete -f redis.yaml
kubectl delete -f kubernetes.yaml
kubectl create -f kubernetes.yaml
kubectl create -f mysql.yaml
kubectl create -f redis.yaml