package ds.loadbalancer;

public class App {
    public static void main(String[] args) {
        ReverseProxy reverseProxy;
        if (args.length > 1) {
            reverseProxy = new ReverseProxy(Integer.parseInt(args[1]));
        } else {
            reverseProxy = new ReverseProxy(8080);
        }

        int dispatchPeriod = 10000;
        int max_num_jobs_dispatch = 30;
        int requestResultPeriod = 3000;

        if (System.getenv().containsKey("DISPATCH_PERIOD")) {
            dispatchPeriod = Integer.parseInt(System.getenv("DISPATCH_PERIOD"));
        }

        if (System.getenv().containsKey("MAX_NUM_JOBS_DISPATCH")) {
            max_num_jobs_dispatch = Integer.parseInt(System.getenv("MAX_NUM_JOBS_DISPATCH"));
        }

        if (System.getenv().containsKey("REQUEST_RESULT_PERIOD")) {
            requestResultPeriod = Integer.parseInt(System.getenv("REQUEST_RESULT_PERIOD"));
        }

        System.out.println("dispatchPeriod: " + dispatchPeriod);
        System.out.println("max_num_jobs_dispatch: " + max_num_jobs_dispatch);
        System.out.println("requestResultPeriod: " + requestResultPeriod);
        System.out.println("maxNumOfNodes: " + System.getenv("MAX_NUM_NODES"));

        reverseProxy.openSocket();
        reverseProxy.dispatch(dispatchPeriod, max_num_jobs_dispatch);
        reverseProxy.requestResultsRoutine(requestResultPeriod);
    }
}
