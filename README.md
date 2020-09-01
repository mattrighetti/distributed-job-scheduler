# Distributed Job Scheduler
### Requirements
> Implement an infrastructure to manage jobs submitted to a cluster of Executors. Each client may submit a job to any of the executors receiving a job id as a return value. Through such job id, clients may check (contacting the same executor they submitted the job to) if the job has been executed and may retrieve back the results produced by the job.
  Executors communicate and coordinate among themselves in order to share load such that at each time every Executor is running the same number of jobs (or a number as close as possible to that). Assume links are reliable but processes (i.e., Executors) may fail (and resume back, re-joining the system immediately after).
  Choose the strategy you find more appropriate to organize communication and coordination. Use stable storage to cope with failures of Executors.

# Implementation
### Main elements
- `ReverseProxy`: Grand central LoadBalancer and job dispatcher
- `ClusterNode`: Node on which jobs are executed

### Flow
1. Client connects to `ClusterNode` and submits as many jobs as he/she wants and for each job he/she submits a ticket is
returned so that he/she can later check the job's result
2. `ClusterNode` forwards every incoming job to the `ReverseProxy`
3. `ReverseProxy` constantly collects info on every `ClusterNode` jobQueue
4. `ReverseProxy` distributes jobs equally among each `ClusterNode` so that they have, almost always, the same amount of jobs in their queue
5. ...

# How to run
## Download images from DockerHub
1. `docker pull mattrighetti/cluster_node`
2. `docker pull mattrighetti/loadbalancer`

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

2. Run ReverseProxy with `docker run --rm --network-alias reverse-proxy --network rp_cluster_network -it mattrighetti/loadbalancer:latest 8080`

3. Run ClusterNode with `docker run --rm --network rp_cluster_network -it -p 9000:9000 mattrighetti/cluster_node:latest reverse-proxy 8080`

4. Connect to `ClusterNode` with `nc <cluster_node_ip> <cluster_node_port>`
