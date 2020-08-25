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

1. Start `ReverseProxy <port>`:
2. Start `ClusterNode <reverse_proxy_ip> <reverse_proxy_port>`
3. Connect to `ClusterNode` with `nc <cluster_node_ip> <cluster_node_port>`
