package Server.Network.Tracker;

import java.util.List;

public interface TrackerServerUtils {
    void addIpToArrayList(String ipAddress);

    List<String> getIpAddresses();
}
