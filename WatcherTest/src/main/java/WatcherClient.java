import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class WatcherClient
{
    private CredentialsProvider credentialsProvider;
    private RestClientBuilder restClientBuilder;
    private RestClient restClient;
    private Response response;
    private String endpointWatcher = "/_xpack/watcher/watch/";
    private Map<String, String> params = Collections.emptyMap();
    private JSONArray watches;

    public WatcherClient(String hostname, String region, int port, String scheme, String username, String password) throws IOException
    {
        response = null;
        credentialsProvider = new BasicCredentialsProvider();
        restClientBuilder = getRestClientBuilder(hostname, region, port, scheme, username, password);
        restClient = restClientBuilder.build();
        watches = getWatchObjects();
    }

    public CredentialsProvider getCredentails(String username, String password)
    {
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
        return credentialsProvider;
    }

    public RestClientBuilder getRestClientBuilder(String hostname, String region, int port, String scheme, final String username, final String password)
    {
        restClientBuilder = RestClient.builder(new HttpHost(hostname + "." + region + ".aws.found.io", port, scheme))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback()
                {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder)
                    {
                        return httpClientBuilder.setDefaultCredentialsProvider(getCredentails(username, password));
                    }
                });
        return restClientBuilder;
    }

    public RestClient getRestClient()
    {
        return restClient;
    }

    public String getEndpointWatcher()
    {
        return endpointWatcher;
    }

    public void closeClient() throws IOException
    {
        restClient.close();
    }

    public void createRequest(String method, String endpoint) throws IOException
    {
        response = restClient.performRequest(method, endpointWatcher + endpoint);
    }

    public void createParmRequest(String method, String endpoint, HttpEntity entity) throws IOException
    {
        response = restClient.performRequest(method, endpointWatcher + endpoint, params, entity);
    }


    public JSONArray getWatchObjects() throws IOException
    {
        String jsonString = " {"
                + " \"query\" : "
                + "{\"match_all\" : { }"
                + " }"
                + "} ";
        String endpoint = "/.watches/_search";
        HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);
        Response response = restClient.performRequest("GET", endpoint, params, entity);
        JSONObject myobject = new JSONObject(EntityUtils.toString(response.getEntity()));
        JSONArray watches = myobject.getJSONObject("hits").getJSONArray("hits");

        return watches;
    }

    public Object[] getWatchIDArray() throws IOException
    {
        ArrayList<String> idArray = new ArrayList<>();

        for (int i = 0; i < watches.length(); i++)
        {
            JSONObject watch = watches.getJSONObject(i);
            idArray.add(watch.get("_id").toString());
        }
        return idArray.toArray();
    }

    public Object[] getWatchState() throws IOException
    {
        ArrayList<String> state = new ArrayList<>();
        String field = "";
        refreshWatches();

        for (int i = 0; i < watches.length(); i++)
        {
            JSONObject watch = watches.getJSONObject(i);
            JSONObject status = watch.getJSONObject("_source").getJSONObject("_status").getJSONObject("state");

            if ((boolean) status.get("active"))
            {
                field = "active";
            } else
            {
                field = "deactivated";
            }
            state.add(field);
        }

        return state.toArray();
    }

    public Object[] getWatchAckno() throws IOException
    {
        ArrayList<String> ack = new ArrayList<>();
        refreshWatches();
        System.out.println(watches.length());

        for (int i = 0; i < watches.length(); i++)
        {
            JSONObject watch = watches.getJSONObject(i);
            JSONObject actions = watch.getJSONObject("_source").getJSONObject("_status").getJSONObject("actions");

            Map log = actions.toMap();
            Object[] set = log.keySet().toArray();

            JSONObject logs = actions.getJSONObject(set[0].toString()).getJSONObject("ack");

            String ackable = logs.get("state").toString();
            System.out.println(ackable);
            if (ackable.equals("awaits_successful_execution"))
            {
                ack.add("Waiting");
            } else if (ackable.equals("ackable"))
            {
                ack.add("Can be acknowledged");
            } else if (ackable.equals("acked"))
            {
                ack.add("Acknowledged");
            } else
            {
                ack.add("Not really working");
            }
        }
        return ack.toArray();
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

    public String getWatchInterval(String watchID)
    {
        return getWatch(watchID).getJSONObject("_source").getJSONObject("trigger").getJSONObject("schedule").get("interval").toString();
    }


    public void refreshWatches() throws IOException
    {
        watches = getWatchObjects();
    }

    public void setInterval(String watchID, String interval) throws IOException
    {
        JSONObject watch = getWatch(watchID);

        JSONObject schedule = watch.getJSONObject("_source").getJSONObject("trigger").getJSONObject("schedule");
        schedule.put("interval", interval);

        watch = watch.getJSONObject("_source");
        String trigger = watch.getJSONObject("trigger").toString();
        String input = watch.getJSONObject("input").toString();
        String condition = watch.getJSONObject("condition").toString();
        String actions = watch.getJSONObject("actions").toString();
        String jsonString = "{\n"
                + "  \"trigger\":" + trigger + ",\n"
                + "  \"input\":" + input + ",\n"
                + "  \"condition\":" + condition + ",\n"
                + "  \"actions\":" + actions + "}";

        HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);
        createParmRequest("PUT", watchID, entity);
    }
}
