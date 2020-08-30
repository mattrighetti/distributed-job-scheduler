package ds.cluster;

public class App {
    public static void main(String[] args) {
        if (args.length > 1) {
            ClusterNode node = new ClusterNode();
            node.connect(args[0], Integer.parseInt(args[1]));
            node.sendJobQueueInfo();
            node.runExecutor();
            node.listenForClientConnections(9000, true);
        } else {
            System.err.println("No hostname:port was given, exiting.");
            System.exit(-1);
        }
    }
}
