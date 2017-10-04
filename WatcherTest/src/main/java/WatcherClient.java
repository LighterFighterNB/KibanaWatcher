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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WatcherClient
{
    private CredentialsProvider credentialsProvider;
    private RestClientBuilder restClientBuilder;
    private RestClient restClient;
    private Response response;
    private String endpointWatcher = "/_xpack/watcher/watch/";
    private Map<String, String> params = Collections.emptyMap();

    public WatcherClient(String hostname, String region, int port, String scheme, String username, String password)
    {
        response = null;
        credentialsProvider = new BasicCredentialsProvider();
        restClientBuilder = getRestClientBuilder(hostname, region, port, scheme, username, password);
        restClient = restClientBuilder.build();
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

    public Map<String, String> getWatchIDs() throws IOException
    {
        HashMap<String, String> stringMap = new HashMap<>();
        String field = "";
        String id = "";
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
        for (int i = 0; i < watches.length(); i++)
        {
            JSONObject watch = watches.getJSONObject(i);
            JSONObject status = watch.getJSONObject("_source").getJSONObject("_status").getJSONObject("state");
            id = watch.get("_id").toString();
            if ((boolean) status.get("active"))
            {
                field = "active";
            } else
            {
                field = "deactivated";
            }
            stringMap.put(id, field);
        }
        return stringMap;
    }
}
