package Client;

import java.io.IOException;

public class ClientTester
{
    public static void main(String[] Args) throws IOException
    {
        Client cl = new Client("a8cfaa58af43b19a22f137ff349c9c2d", "eu-west-1", 9243, "https", "admin", "admin01");


        WatcherClient watcherClient = new WatcherClient(cl);

        cl.close();
        watcherClient.close();
    }
}
