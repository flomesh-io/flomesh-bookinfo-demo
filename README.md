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

从仓库 `https://github.com/flomesh-io/flomesh-bookinfo-demo.git` 克隆代码。进入到 `flomesh-bookinfo-demo/kubernetes`目录。

所有 Flomesh 组件以及用于 demo 的 yamls 文件都位于这个目录中。

### 安装 FSM

```shell
$ kubectl apply -f fsm/fsm.yaml
```

注意：要保证 `flomesh` 命名空间中所有的 pod 都正常运行：

```shell
$ kubectl get pod -n flomesh
NAME                                       READY   STATUS      RESTARTS   AGE
bootstrap-57b7df55f5-f4gvd                 1/1     Running     0          14m
cluster-connector-local-69c675d78d-54k7p   1/1     Running     0          13m
ingress-pipy-78878cf4b9-6l8cv              1/1     Running     0          14m
install-default-components-skdlz           0/1     Completed   0          14m
manager-74b5bb7bd4-bc9b2                   1/1     Running     0          14m
repo-66d784cdcd-ggtxh                      1/1     Running     0          14m
```


至此，你已经成功安装 Flomesh FSM 的所有组件，包括 ingress 控制器。

## 中间件

Demo 需要用到中间件完成日志和统计数据的存储，这里为了方便使用 pipy 进行 mock：直接在控制台中打印数据。

另外，服务治理相关的配置有 mock 的 pipy config 服务提供。

### log & metrics

```shell
$ cat > middleware.js <<EOF
pipy()
.listen(8123)
    .link('mock')

.listen(9001)
    .link('mock')

.pipeline('mock')
    .decodeHTTPRequest()
    .replaceMessage(
        req => (
            console.log(req.body.toString()),
            new Message('OK')
        )
    )
    .encodeHTTPResponse()
EOF

$ docker run --rm --name middleware --entrypoint "pipy" -v ${PWD}:/script -p 8123:8123 -p 9001:9001 flomesh/pipy-pjs:0.4.0-118 /script/middleware.js
```

### pipy config

```shell
$ cat > mock-config.json <<EOF
{
  "ingress": {},
  "inbound": {
    "rateLimit": -1,
    "dataLimit": -1,
    "circuitBreak": false,
    "blacklist": []
  },
  "outbound": {
    "rateLimit": -1,
    "dataLimit": -1
  }
}
EOF

$ cat > mock.js <<EOF
pipy({
  _CONFIG_FILENAME: 'mock-config.json',

  _serveFile: (req, filename, type) => (
    new Message(
      {
        bodiless: req.head.method === 'HEAD',
        headers: {
          'etag': os.stat(filename)?.mtime | 0,
          'content-type': type,
        },
      },
      req.head.method === 'HEAD' ? null : os.readFile(filename),
    )
  ),

  _router: new algo.URLRouter({
    '/config': req => _serveFile(req, _CONFIG_FILENAME, 'application/json'),
    '/*': () => new Message({ status: 404 }, 'Not found'),
  }),
})

// Config
.listen(9000)
  .decodeHTTPRequest()
  .replaceMessage(
    req => (
      _router.find(req.head.path)(req)
    )
  )
  .encodeHTTPResponse()
EOF

$ docker run --rm --name mock --entrypoint "pipy" -v ${PWD}:/script -p 9000:9000 flomesh/pipy-pjs:0.4.0-118 /script/mock.js
```

## 运行 Demo

Demo 运行在另一个独立的命名空间 `flomesh-spring` 中，执行命令 `kubectl apply -f base/namespace.yaml` 来创建该命名空间。如果你 `describe` 该命名空间你会发现其使用了 `flomesh.io/inject=true` 标签。

这个标签告知 operator 的 admission webHook 拦截标注的命名空间下 pod 的创建。

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

提供中间件的访问 endpoid，将 `base/clickhouse.yaml`、`base/metrics.yaml` 和 `base/config.yaml` 中的 ip 地址改为本机的 ip 地址（不是 127.0.0.1）。

修改之后，执行如下命令：

```shell
$ kubectl apply -f base/clickhouse.yaml
$ kubectl apply -f base/metrics.yaml
$ kubectl apply -f base/config.yaml

$ kubectl get endpoints samples-clickhouse samples-metrics samples-config -n flomesh-spring
NAME                 ENDPOINTS            AGE
samples-clickhouse   192.168.1.101:8123   3m
samples-metrics      192.168.1.101:9001   3s
samples-config       192.168.1.101:9000   3m
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

### 灰度

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

执行上面的命令之后，我们可以在浏览器中访问前端服务（`http://fe-v1.flomesh.cn:81/productpage?u=normal`、 `http://fe-v2.flomesh.cn:81/productpage?u=normal`），只有 v1 版本的前端中才能看到刚才添加的记录。

![page v1](./docs/images/demo/page-v1.png)

![page v2](./docs/images/demo/page-v2.png)

### 熔断

这里熔断我们通过修改 `mock-config.json` 中的 `inbound.circuitBreak` 为 `true`，来将服务强制开启熔断：

```json
{
  "ingress": {},
  "inbound": {
    "rateLimit": -1,
    "dataLimit": -1,
    "circuitBreak": true, //here
    "blacklist": []
  },
  "outbound": {
    "rateLimit": -1,
    "dataLimit": -1
    
  }
}
```

```shell
$ curl http://$ingressAddr/actuator/health -H 'Host: api-v1.flomesh.cn'
HTTP/1.1 503 Service Unavailable
Connection: keep-alive
Content-Length: 27

Service Circuit Break Open
```

### 限流

修改 pipy config 的配置，将 `inbound.rateLimit` 设置为 1。

```json
{
  "ingress": {},
  "inbound": {
    "rateLimit": 1, //here
    "dataLimit": -1,
    "circuitBreak": false,
    "blacklist": []
  },
  "outbound": {
    "rateLimit": -1,
    "dataLimit": -1
  }
}
```

我们使用 `wrk` 模拟发送请求，20 个连接、20 个请求、持续 30s：

```shell
$ wrk -t20 -c20 -d30s --latency http://$ingressAddr/actuator/health -H 'Host: api-v1.flomesh.cn'
Running 30s test @ http://127.0.0.1:81/actuator/health
  20 threads and 20 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   951.51ms  206.23ms   1.04s    93.55%
    Req/Sec     0.61      1.71    10.00     93.55%
  Latency Distribution
     50%    1.00s
     75%    1.01s
     90%    1.02s
     99%    1.03s
  620 requests in 30.10s, 141.07KB read
Requests/sec:     20.60
Transfer/sec:      4.69KB
```

从结果来看 20.60 req/s，即每个连接 1 req/s。

### 黑白名单

将 pipy config 的 `mock-config.json` 做如下修改：ip 地址使用的是 ingress controller 的 pod ip。

```shell
$ kgpo -n ingress-pipy ingress-pipy-controller-76cd866d78-4cqqn -o jsonpath='{.status.podIP}'
10.42.0.78
```

```json
{
  "ingress": {},
  "inbound": {
    "rateLimit": -1,
    "dataLimit": -1,
    "circuitBreak": false,
    "blacklist": ["10.42.0.78"] //here
  },
  "outbound": {
    "rateLimit": -1,
    "dataLimit": -1
    
  }
}
```

还是访问网关的接口

```shell
curl http://$ingressAddr/actuator/health -H 'Host: api-v1.flomesh.cn'
HTTP/1.1 503 Service Unavailable
content-type: text/plain
Connection: keep-alive
Content-Length: 20

Service Unavailable
```