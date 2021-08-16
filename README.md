# Spring Bookinfo Demo Quickstart

## 架构

![typology](docs/images/demo/spring-demo-architect.png)

## 环境搭建

搭建 Kubernetes 环境，可以选择 kubeadm 进行集群搭建。也可以选择 minikube、k3s、Kind 等，本文使用 k3s。

使用 [k3d](https://k3d.io/) 安装 [k3s](https://github.com/k3s-io/k3s)。k3d 将在 Docker 容器中运行 k3s，因此需要保证已经安装了 Docker。

```shell
$ k3d cluster create spring-demo -p "81:80@loadbalancer" --k3s-server-arg '--no-deploy=traefik'
```

## 安装 Flomesh

从仓库 `https://github.com/addozhang/flomesh-bookinfo-demo.git` 克隆代码。进入到 `flomesh-bookinfo-demo/kubernetes`目录。

所有 Flomesh 组件以及用于 demo 的 yamls 文件都位于这个目录中。

### 安装 Cert Manager

```shell
$ kubectl apply -f artifacts/cert-manager-v1.3.1.yaml
customresourcedefinition.apiextensions.k8s.io/certificaterequests.cert-manager.io created
customresourcedefinition.apiextensions.k8s.io/certificates.cert-manager.io created
customresourcedefinition.apiextensions.k8s.io/challenges.acme.cert-manager.io created
customresourcedefinition.apiextensions.k8s.io/clusterissuers.cert-manager.io created
customresourcedefinition.apiextensions.k8s.io/issuers.cert-manager.io created
customresourcedefinition.apiextensions.k8s.io/orders.acme.cert-manager.io created
namespace/cert-manager created
serviceaccount/cert-manager-cainjector created
serviceaccount/cert-manager created
serviceaccount/cert-manager-webhook created
clusterrole.rbac.authorization.k8s.io/cert-manager-cainjector created
clusterrole.rbac.authorization.k8s.io/cert-manager-controller-issuers created
clusterrole.rbac.authorization.k8s.io/cert-manager-controller-clusterissuers created
clusterrole.rbac.authorization.k8s.io/cert-manager-controller-certificates created
clusterrole.rbac.authorization.k8s.io/cert-manager-controller-orders created
clusterrole.rbac.authorization.k8s.io/cert-manager-controller-challenges created
clusterrole.rbac.authorization.k8s.io/cert-manager-controller-ingress-shim created
clusterrole.rbac.authorization.k8s.io/cert-manager-view created
clusterrole.rbac.authorization.k8s.io/cert-manager-edit created
clusterrole.rbac.authorization.k8s.io/cert-manager-controller-approve:cert-manager-io created
clusterrole.rbac.authorization.k8s.io/cert-manager-webhook:subjectaccessreviews created
clusterrolebinding.rbac.authorization.k8s.io/cert-manager-cainjector created
clusterrolebinding.rbac.authorization.k8s.io/cert-manager-controller-issuers created
clusterrolebinding.rbac.authorization.k8s.io/cert-manager-controller-clusterissuers created
clusterrolebinding.rbac.authorization.k8s.io/cert-manager-controller-certificates created
clusterrolebinding.rbac.authorization.k8s.io/cert-manager-controller-orders created
clusterrolebinding.rbac.authorization.k8s.io/cert-manager-controller-challenges created
clusterrolebinding.rbac.authorization.k8s.io/cert-manager-controller-ingress-shim created
clusterrolebinding.rbac.authorization.k8s.io/cert-manager-controller-approve:cert-manager-io created
clusterrolebinding.rbac.authorization.k8s.io/cert-manager-webhook:subjectaccessreviews created
role.rbac.authorization.k8s.io/cert-manager-cainjector:leaderelection created
role.rbac.authorization.k8s.io/cert-manager:leaderelection created
role.rbac.authorization.k8s.io/cert-manager-webhook:dynamic-serving created
rolebinding.rbac.authorization.k8s.io/cert-manager-cainjector:leaderelection created
rolebinding.rbac.authorization.k8s.io/cert-manager:leaderelection created
rolebinding.rbac.authorization.k8s.io/cert-manager-webhook:dynamic-serving created
service/cert-manager created
service/cert-manager-webhook created
deployment.apps/cert-manager-cainjector created
deployment.apps/cert-manager created
deployment.apps/cert-manager-webhook created
mutatingwebhookconfiguration.admissionregistration.k8s.io/cert-manager-webhook created
validatingwebhookconfiguration.admissionregistration.k8s.io/cert-manager-webhook created
```

注意: 要保证 `cert-manager` 命名空间中所有的 pod 都正常运行：

```shell
$ kubectl get pod -n cert-manager
NAME                                       READY   STATUS    RESTARTS   AGE
cert-manager-webhook-56fdcbb848-q7fn5      1/1     Running   0          98s
cert-manager-59f6c76f4b-z5lgf              1/1     Running   0          98s
cert-manager-cainjector-59f76f7fff-flrr7   1/1     Running   0          98s
```

### 安装 Pipy Operator

```shell
$ kubectl apply -f artifacts/pipy-operator.yaml
```

执行完命令后会看到类似的结果：

```
namespace/flomesh created
customresourcedefinition.apiextensions.k8s.io/proxies.flomesh.io created
customresourcedefinition.apiextensions.k8s.io/proxyprofiles.flomesh.io created
serviceaccount/operator-manager created
role.rbac.authorization.k8s.io/leader-election-role created
clusterrole.rbac.authorization.k8s.io/manager-role created
clusterrole.rbac.authorization.k8s.io/metrics-reader created
clusterrole.rbac.authorization.k8s.io/proxy-role created
rolebinding.rbac.authorization.k8s.io/leader-election-rolebinding created
clusterrolebinding.rbac.authorization.k8s.io/manager-rolebinding created
clusterrolebinding.rbac.authorization.k8s.io/proxy-rolebinding created
configmap/manager-config created
service/operator-manager-metrics-service created
service/proxy-injector-svc created
service/webhook-service created
deployment.apps/operator-manager created
deployment.apps/proxy-injector created
certificate.cert-manager.io/serving-cert created
issuer.cert-manager.io/selfsigned-issuer created
mutatingwebhookconfiguration.admissionregistration.k8s.io/mutating-webhook-configuration created
mutatingwebhookconfiguration.admissionregistration.k8s.io/proxy-injector-webhook-cfg created
validatingwebhookconfiguration.admissionregistration.k8s.io/validating-webhook-configuration created
```

注意：要保证 `flomesh` 命名空间中所有的 pod 都正常运行：

```shell
$ kubectl get pod -n flomesh
NAME                               READY   STATUS    RESTARTS   AGE
proxy-injector-5bccc96595-spl6h    1/1     Running   0          39s
operator-manager-c78bf8d5f-wqgb4   1/1     Running   0          39s
```

### 安装 Ingress 控制器

```shell
$ kubectl apply -f ingress/ingress-pipy.yaml
namespace/ingress-pipy created
customresourcedefinition.apiextensions.k8s.io/ingressparameters.flomesh.io created
serviceaccount/ingress-pipy created
role.rbac.authorization.k8s.io/ingress-pipy-leader-election-role created
clusterrole.rbac.authorization.k8s.io/ingress-pipy-role created
rolebinding.rbac.authorization.k8s.io/ingress-pipy-leader-election-rolebinding created
clusterrolebinding.rbac.authorization.k8s.io/ingress-pipy-rolebinding created
configmap/ingress-config created
service/ingress-pipy-cfg created
service/ingress-pipy-controller created
service/ingress-pipy-defaultbackend created
service/webhook-service created
deployment.apps/ingress-pipy-cfg created
deployment.apps/ingress-pipy-controller created
deployment.apps/ingress-pipy-manager created
certificate.cert-manager.io/serving-cert created
issuer.cert-manager.io/selfsigned-issuer created
mutatingwebhookconfiguration.admissionregistration.k8s.io/mutating-webhook-configuration configured
validatingwebhookconfiguration.admissionregistration.k8s.io/validating-webhook-configuration configured
```

检查 `ingress-pipy` 命名空间下 pod 的状态：

```shell
$ kubectl get pod -n ingress-pipy
NAME                                       READY   STATUS    RESTARTS   AGE
svclb-ingress-pipy-controller-8pk8k        1/1     Running   0          71s
ingress-pipy-cfg-6bc649cfc7-8njk7          1/1     Running   0          71s
ingress-pipy-controller-76cd866d78-m7gfp   1/1     Running   0          71s
ingress-pipy-manager-5f568ff988-tw5w6      0/1     Running   0          70s
```

至此，你已经成功安装 Flomesh 的所有组件，包括 operator 和 ingress 控制器。

## 中间件

Demo 需要用到 clickhouse（用于存储入站和出站的请求信息），有两种方案：使用 pipy 模拟 clickhouse 接收请求；使用 Docker 运行 clickhouse（需要安装 docker-compose）。

这里为了方便，建议使用第一种方案。

### 使用 Pipy 模拟

```shell
$ cat > mock.js <<EOF
pipy()
.listen(8123)
    .link('mock')

.listen(9001)
    .link('mock')
.pipeline('mock')
    .decodeHttpRequest()
    .replaceMessage(
        req => (
            console.log(req.body.toString()),
            new Message('OK')
        )
    )
    .encodeHttpResponse()
EOF

$ docker run --rm --name mock --entrypoint "pipy" -v ${PWD}:/script -p 8123:8123 -p 9001:9001 flomesh/pipy-pjs:0.4.0-118 /script/mock.js
```


### Docker 中运行 

将如下内容保存到

```shell
$ cat > clickhouse.yaml <<EOF
version: "3"
services:
  server:
    container_name: clickhouse-server
    image: yandex/clickhouse-server
    ports:
      - "8123:8123"
      - "9000:9000"
      - "9009:9009"

    ulimits:
      nproc: 65535
      nofile:
        soft: 262144
        hard: 262144
  client:
    container_name: clickhouse-client
    image: yandex/clickhouse-client
    command: ["--host", "server"]
    depends_on:
      - server
EOF

$ docker-compose -f clickhouse.yaml up -d
```

然后再初始化表 `log`，这里需要用到 [init-log.sql](scripts/init-log.sql) 中定义的 schema：

```sql
create table log (
    rid UInt64 default JSONExtractInt(message,'rid'),
    sid UInt64 default JSONExtractInt(message,'sid'),
    iid String default JSONExtractString(message,'iid'),
    dir String default JSONExtractString(message,'dir'),
    proto String default JSONExtractString(message,'proto'),
    req String default JSONExtractRaw(message,'req'),
    `req.id` String default JSONExtractString(req,'id'),
    `req.protocol` String default JSONExtractString(req,'protocol'),
    `req.version` String default JSONExtractString(req,'version'),
    `req.service.name` String default JSONExtractString(req,'service.name'),
    `req.service.version` String default JSONExtractString(req,'service.version'),
    `req.method.name` String default JSONExtractString(req,'method.name'),
    `req.method.type` String default JSONExtractString(req,'method.type'),
    `req.method` String default JSONExtractString(req,'method'),
    `req.path` String default JSONExtractString(req,'path'),
    `req.headers` String default JSONExtractRaw(req,'headers'),
    `req.body` String default JSONExtractString(req,'body'),
    `req.arguments` Array(String) default JSONExtractArrayRaw(req,'arguments'),
    res String default JSONExtractRaw(message,'res'),
    `res.protocol` String default JSONExtractString(res,'protocol'),
    `res.type` Int32 default JSONExtractInt(res,'type'),
    `res.value` String default JSONExtractString(res,'value'),
    `res.status` UInt32 default JSONExtractInt(res,'status'),
    `res.statusText` String default JSONExtractString(res,'statusText'),
    `res.headers` String default JSONExtractRaw(res,'headers'),
    `res.body` String default JSONExtractString(res,'body'),
    reqTime UInt64 default JSONExtractInt(message,'reqTime'),
    resTime UInt64 default JSONExtractInt(message,'resTime'),
    reqSize UInt64 default JSONExtractInt(message,'reqSize'),
    resSize UInt64 default JSONExtractInt(message,'resSize'),
    localAddr String default JSONExtractString(message, 'localAddr'),
    localPort UInt32 default JSONExtractInt(message, 'localPort'),
    remoteAddr String default JSONExtractString(message, 'remoteAddr'),
    remotePort UInt32 default JSONExtractInt(message, 'remotePort'),
    node String default JSONExtractRaw(message,'node'),
    `node.ip` String default JSONExtractString(node,'ip'),
    `node.name` String default JSONExtractString(node,'name'),
    pod String default JSONExtractRaw(message,'pod'),
    `pod.ns` String default JSONExtractString(pod,'ns'),
    `pod.ip` String default JSONExtractString(pod,'ip'),
    `pod.name` String default JSONExtractString(pod,'name'),
    service String default JSONExtractRaw(message,'service'),
    `service.name` String default JSONExtractString(service,'name'),
    target String default JSONExtractRaw(message,'target'),
    `target.address` String default JSONExtractString(target,'address'),
    `target.port` UInt32 default JSONExtractInt(target,'port'),
    trace String default JSONExtractRaw(message,'trace'),
    `trace.id` String default JSONExtractString(trace,'id'),
    `trace.span` String default JSONExtractString(trace,'span'),
    `trace.parent` String default JSONExtractString(trace,'parent'),
    `trace.sampled` String default JSONExtractString(trace,'sampled'),
    `env` String DEFAULT JSONExtractRaw(message, 'env'),
    id UUID default generateUUIDv4(),
    targetId UUID,
    timestamp DateTime default now(),
    message String
) engine=MergeTree()
partition by toYYYYMM(toDateTime(reqTime/1000))
order by reqTime;
```

## 运行 Demo

Demo 运行在另一个独立的命名空间 `flomesh-spring` 中，执行命令 `kubectl apply -f base/namespace.yaml` 来创建该命名空间。如果你 `describe` 该命名空间你会发现其使用了 `flomesh.io/inject=true` 注解。

这个注解告知 operator 的 admission webHook 拦截标注的命名空间下 pod 的创建。

```shell
$ kubectl describe ns flomesh-spring
Name:         flomesh-spring
Labels:       app.kubernetes.io/name=spring-mesh
              app.kubernetes.io/version=1.19.0
              flomesh.io/inject=true
              kubernetes.io/metadata.name=flomesh-spring
Annotations:  <none>
Status:       Active

No resource quota.

No LimitRange resource.
```

我们首先看下 Flomesh 提供的 CRD `ProxyProfile`。这个 demo 中，其定义了 sidecar 容器片段以及所使用的的脚本。检查 `sidecar/proxy-profile.yaml` 获取更多信息。执行下面的命令，创建 CRD 资源。

```shell
$ kubectl apply -f sidecar/proxy-profile.yaml
```

检查是否创建成功：

```shell
$ kubectl get pf -o wide
NAME                         NAMESPACE        DISABLED   SELECTOR                                     CONFIG                                                                AGE
proxy-profile-002-bookinfo   flomesh-spring   false      {"matchLabels":{"sys":"bookinfo-samples"}}   {"flomesh-spring":"proxy-profile-002-bookinfo-fsmcm-b67a9e39-0418"}   27s
```

As the services has startup dependencies, you need to deploy it one by one following the strict order. Before starting, check the **Endpoints** section of **base/clickhouse.yaml**.

提供中间件的访问 endpoid，将 `base/clickhouse.yaml` 和 `base/metrics.yaml` 中的 ip 地址改为本机的 ip 地址（不是 127.0.0.1）。

修改之后，执行如下命令：

```shell
$ kubectl apply -f base/clickhouse.yaml
$ kubectl apply -f base/metrics.yaml

$ kubectl get endpoints samples-clickhouse samples-metrics
NAME                 ENDPOINTS            AGE
samples-clickhouse   192.168.1.101:8123   105m
samples-metrics      192.168.1.101:9001   2m24s
```

### 部署注册中心

```shell
$ kubectl apply -f base/discovery-server.yaml
```

检查注册中心 pod 的状态，确保 3 个容器都运行正常。

```shell
$ kubectl get pod
NAME                                           READY   STATUS        RESTARTS   AGE
samples-discovery-server-v1-85798c47d4-dr72k   3/3     Running       0          96s
```

### 部署配置中心

```shell
$ kubectl apply -f base/config-service.yaml
```

### 部署 API 网关以及 bookinfo 相关的服务

```shell
$ kubectl apply -f base/bookinfo-v1.yaml
$ kubectl apply -f base/bookinfo-v2.yaml
$ kubectl apply -f base/productpage-v1.yaml
$ kubectl apply -f base/productpage-v2.yaml
```

检查 pod 状态，可以看到所有 pod 都注入了容器。

```shell
$ kubectl get pods
samples-discovery-server-v1-85798c47d4-p6zpb       3/3     Running   0          19h
samples-config-service-v1-84888bfb5b-8bcw9         1/1     Running   0          19h
samples-api-gateway-v1-75bb6456d6-nt2nl            3/3     Running   0          6h43m
samples-bookinfo-ratings-v1-6d557dd894-cbrv7       3/3     Running   0          6h43m
samples-bookinfo-details-v1-756bb89448-dxk66       3/3     Running   0          6h43m
samples-bookinfo-reviews-v1-7778cdb45b-pbknp       3/3     Running   0          6h43m
samples-api-gateway-v2-7ddb5d7fd9-8jgms            3/3     Running   0          6h37m
samples-bookinfo-ratings-v2-845d95fb7-txcxs        3/3     Running   0          6h37m
samples-bookinfo-reviews-v2-79b4c67b77-ddkm2       3/3     Running   0          6h37m
samples-bookinfo-details-v2-7dfb4d7c-jfq4j         3/3     Running   0          6h37m
samples-bookinfo-productpage-v1-854675b56-8n2xd    1/1     Running   0          7m1s
samples-bookinfo-productpage-v2-669bd8d9c7-8wxsf   1/1     Running   0          6m57s
```

### 添加 Ingress 规则

执行如下命令添加 Ingress 规则。

```shell
$ kubectl apply -f ingress/ingress.yaml
```

### 测试前的准备

访问 demo 服务都要通过 ingress 控制器。因此需要先获取 LB 的 ip 地址。
```shell
//Obtain the controller IP
//Here, we append port. 
ingressAddr=`kubectl get svc ingress-pipy-controller -n ingress-pipy -o jsonpath='{.spec.clusterIP}'`:81
```

这里我们使用了是 k3d 创建的 k3s，命令中加入了 `-p 81:80@loadbalancer` 选项。我们可以使用 `127.0.0.1:81` 来访问 ingress 控制器。这里执行命令 `ingressAddr=127.0.0.1:81`。

Ingress 规则中，我们为每个规则指定了 `host`，因此每个请求中需要通过 HTTP 请求头 `Host` 提供对应的 `host`。

或者在 `/etc/hosts` 添加记录：

```shell
$ kubectl get ing ingress-pipy-bookinfo -n flomesh-spring -o jsonpath="{range .spec.rules[*]}{.host}{'\n'}"
api-v1.flomesh.cn
api-v2.flomesh.cn
fe-v1.flomesh.cn
fe-v2.flomesh.cn

//添加记录到 /etc/hosts
127.0.0.1 api-v1.flomesh.cn api-v2.flomesh.cn fe-v1.flomesh.cn fe-v2.flomesh.cn
```

#### 验证

```shell
$ curl http://127.0.0.1:81/actuator/health -H 'Host: api-v1.flomesh.cn'
{"status":"UP","groups":["liveness","readiness"]}
//OR
$ curl http://api-v1.flomesh.cn:81/actuator/health
{"status":"UP","groups":["liveness","readiness"]}
```

## 测试

在 v1 版本的服务中，我们为 book 添加 rating 和 review。

```shell
# rate a book
$ curl -X POST http://$ingressAddr/bookinfo-ratings/ratings \
	-H "Content-Type: application/json" \
	-H "Host: api-v1.flomesh.cn" \
	-d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"2099a055-1e21-46ef-825e-9e0de93554ea","rating":3}' 

$ curl http://$ingressAddr/bookinfo-ratings/ratings/2099a055-1e21-46ef-825e-9e0de93554ea -H "Host: api-v1.flomesh.cn"

# review a book
$ curl -X POST http://$ingressAddr/bookinfo-reviews/reviews \
	-H "Content-Type: application/json" \
	-H "Host: api-v1.flomesh.cn" \
	-d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"2099a055-1e21-46ef-825e-9e0de93554ea","review":"This was OK.","rating":3}'

$ curl http://$ingressAddr/bookinfo-reviews/reviews/2099a055-1e21-46ef-825e-9e0de93554ea -H "Host: api-v1.flomesh.cn"
```

执行上面的命令之后，我们可以在浏览器中访问前端服务（`http://fe-v1.flomesh.cn:81/productpage?u=normal`、 `http://fe-v1.flomesh.cn:82/productpage?u=normal`），只有 v1 版本的前端中才能看到刚才添加的记录。

![page v1](./docs/images/demo/page-v1.png)

![page v2](./docs/images/demo/page-v2.png)
