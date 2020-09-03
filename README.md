# Distributed Job Scheduler
### Requirements
> Implement an infrastructure to manage jobs submitted to a cluster of Executors. Each client may submit a job to any of the executors receiving a job id as a return value. Through such job id, clients may check (contacting the same executor they submitted the job to) if the job has been executed and may retrieve back the results produced by the job.
  Executors communicate and coordinate among themselves in order to share load such that at each time every Executor is running the same number of jobs (or a number as close as possible to that). Assume links are reliable but processes (i.e., Executors) may fail (and resume back, re-joining the system immediately after).
  Choose the strategy you find more appropriate to organize communication and coordination. Use stable storage to cope with failures of Executors.

# Implementation
### Main elements
- `ReverseProxy`: Grand central loadbalancer and job dispatcher
- `ClusterNode`: Node on which jobs are executed

### Flow
1. Client connects to `ClusterNode` and submits as many jobs as he/she wants and for each job he/she submits a ticket is
returned so that he/she can later check the job's result
2. `ClusterNode` forwards every incoming job from clients to `ReverseProxy`
3. `ReverseProxy` constantly collects info on every `ClusterNode` jobQueue
4. `ReverseProxy` distributes jobs equally among each `ClusterNode` so that they have, almost always, the same amount of jobs in their queue
5. `ReverseProxy` and `ClusterNode` constantly exchange messages to request/respond for/with results until every job result is fulfilled

### Messages description
|Name|Payload type|
|:---------------|:-------------|
|`INFO`          |`Integer`     |
|`REQUEST_OF_RES`|`List<String>`|
|`RESULT`|`List<Tuple2<String, String>>`|
|`RES_REQ`|`Tuple2<List<Tuple2<String, String>>, List<String>>`|

|Name|Description|
|:---------------|:-|
|`INFO`          |Message containing an integer value used by `ClusterNodes` to notify how many jobs they have in their local job queue|
|`REQUEST_OF_RES`|Message containing a list of **jobIds** that both `ReverseProxy` and `ClusterNode` use to request those job results|
|`RESULT`|Message containing a list of tuples that both `ReverseProxy` and `ClusterNode` use to send jobs result.<br>Each tuple contains a **jobId** and its result|
|`RES_REQ`|Message containing a list of **jobIds** to request and a list of tuples of job results.<br>This message is used to optimize message communication and to avoid network cluttering|

# How to run
## Download images from DockerHub
1. `docker pull ghcr.io/mattrighetti/cluster_node`
2. `docker pull ghcr.io/mattrighetti/loadbalancer`

You can find the Docker images on my Github Container Registry 
[cluster_node](https://github.com/users/MattRighetti/packages/container/cluster_node/29697) and 
[loadbalancer](https://github.com/users/MattRighetti/packages/container/loadbalancer/29696)

## Loadbalancer ENV variables
| Name   |   Description   | Default value |
|:----------|:-------------|:-:|
| `DISPATCH_PERIOD` | How frequently should the loadbalancer dispatch jobs (in ms) | 5000 |
| `MAX_NUM_JOBS_DISPATCH` | How many jobs are dispatched every `DISPATCH_PERIOD` | 30 |
| `REQUEST_RESULT_PERIOD` | How frequently should the loadbalancer request/send results to nodes (in ms) | 3000 |
| `MAX_NUM_NODES` | Maximum number of nodes that can connect to the loadbalancer | 10 |

You can override this values at any time by passing `-e ENV_NAME=value` to the docker `run` command

## Run container
1. Make sure to create a `docker network` with `docker network create rp_cluster_network`

2. To use persistent storage you have to create a volume for each instance you want to launch
```
docker volume create rp_v
docker volume create c1_v
```

3. Run ReverseProxy with 
```
docker run --rm \
--network-alias reverse-proxy \
--network rp_cluster_network \
--mount source=rp_v,target=/usr/src/app \
-e DISPATCH_PERIOD=3000 \
-e MAX_NUM_NODES=5 \
-e REQUEST_RESULT_PERIOD=3000 \
-e MAX_NUM_JOBS_DISPATCH=50 \
-it ghcr.io/mattrighetti/loadbalancer:latest 8080
```

4. Run ClusterNode with 
```
docker run --rm \
--network rp_cluster_network \
--mount source=c1_v,target=/usr/src/app \
-p 9000:9000 \
-it ghcr.io/mattrighetti/cluster_node:latest \
reverse-proxy 8080
```

5. Connect to `ClusterNode` with `nc <cluster_node_ip> <cluster_node_port>`
