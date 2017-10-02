import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.xpack.XPackClient;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.elasticsearch.xpack.watcher.client.WatcherClient;
import org.elasticsearch.xpack.watcher.support.xcontent.XContentSource;
import org.elasticsearch.xpack.watcher.transport.actions.delete.DeleteWatchResponse;
import org.elasticsearch.xpack.watcher.transport.actions.execute.ExecuteWatchRequest;
import org.elasticsearch.xpack.watcher.transport.actions.get.GetWatchResponse;
import org.elasticsearch.xpack.watcher.watch.Watch;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class Watcher
{
    public static void main(String[] Args) throws UnknownHostException
    {
        // Build the settings for our client.
        String clusterId = "bc2e0fb1ddbf540185dc508598e610d7"; // Your cluster ID here
        String region = "eu-west-1"; // Your region here
        boolean enableSsl = true;

        String hostname =clusterId + "." + region + ".aws.found.io";
    // Instantiate a TransportClient and add the cluster to the list of addresses to connect to.
    // Only port 9343 (SSL-encrypted) is currently supported.

        TransportClient client;
        client = new PreBuiltXPackTransportClient(Settings.builder()
                .put("cluster.name", clusterId)
                .put("node.name","test")
                .put("xpack.security.transport.ssl.enabled", "true")
                .put("xpack.security.user", "Eric:password")
                .build());
        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostname), 9243));

        System.out.println(client.connectedNodes());


        char[]pass = new char[8];
        pass[0]= 'p';
        pass[1]= 'a';
        pass[2]= 's';
        pass[3]= 's';
        pass[4]= 'w';
        pass[5]= 'o';
        pass[6]= 'r';
        pass[7]= 'd';
        XPackClient xpackClient = new XPackClient(client);
        WatcherClient watcherClient = xpackClient.watcher();

        GetWatchResponse getWatchResponse = watcherClient.prepareGetWatch("my-watch").get();
        boolean active = getWatchResponse.getStatus().state().isActive();

        /*ActivateWatchResponse activateResponse = watcherClient.prepareActivateWatch("my-watch", true).get();
boolean active = activateResponse.getStatus().state().isActive();*/

        System.out.println(active);
    }
}