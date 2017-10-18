package Client;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class WatcherClient extends Client
{
    private JSONArray watches;
    private int totalWatches;

    public WatcherClient(String hostname, String region, int port, String scheme, String username, String password) throws IOException
    {
        super(hostname, region, port, scheme, username, password);
        totalWatches = 0;
        watches = null;

        refreshWatches();
    }

    public WatcherClient(Client client) throws IOException
    {
        super(client.getHostname(), client.getRegion(), client.getPort(), client.getScheme(), client.getCred());
        totalWatches = 0;
        watches = null;

        refreshWatches();
    }

    public void refreshWatches() throws IOException
    {
        totalWatches = super.preformRequest("GET", "/.watches/_search").getJSONObject("hits").getInt("total");

        String jsonString = " {"
                + " \"size\" : " + totalWatches + "}";
        HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);

        watches = super.preformParmRequest("GET", "/.watches/_search", entity).getJSONObject("hits").getJSONArray("hits");
    }

    public JSONObject getWatch(String name)
    {
        JSONObject watch = new JSONObject();

        for (int i = 0; i < watches.length(); i++)
        {
            JSONObject temp = watches.getJSONObject(i);
            if (temp.get("_id").toString().equals(name))
            {
                watch = temp;
            }
        }

        return watch;
    }
}
