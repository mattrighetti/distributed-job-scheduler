package ds.loadbalancer;

public class App {
    public static void main(String[] args) {
        ReverseProxy reverseProxy;
        if (args.length > 1) {
            reverseProxy = new ReverseProxy(Integer.parseInt(args[1]));
        } else {
            reverseProxy = new ReverseProxy(8080);
        }

        reverseProxy.openSocket();
        reverseProxy.dispatch(10000, 20);
    }
}
