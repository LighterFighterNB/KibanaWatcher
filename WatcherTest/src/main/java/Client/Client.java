package Client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Client
{
    private Credentials cred;
    private RestClient restClient;
    private Response response;
    private HashMap<String, Object> buildData;
    private Map<String, String> params = Collections.emptyMap();

    public Client(String hostname, String region, int port, String scheme, String username, String password) throws IOException
    {
        buildData = new HashMap<>();

        buildData.put("hostname", hostname);
        buildData.put("region", region);
        buildData.put("port", port);
        buildData.put("scheme", scheme);

        cred = new Credentials(username, password);
        RestClientBuilder restClientBuilder = getRestClientBuilder(hostname, region, port, scheme);
        restClient = restClientBuilder.build();
        response = null;

        System.out.println(cred.authorise(this));
    }

    protected Client(String hostname, String region, int port, String scheme, Credentials cred) throws IOException
    {
        buildData = new HashMap<>();

        buildData.put("hostname", hostname);
        buildData.put("region", region);
        buildData.put("port", port);
        buildData.put("scheme", scheme);

        this.cred = cred;
        RestClientBuilder restClientBuilder = getRestClientBuilder(hostname, region, port, scheme);
        restClient = restClientBuilder.build();
        response = null;

        System.out.println(this.cred.authorise(this));
    }

    public void close() throws IOException
    {
        restClient.close();
    }

    private RestClientBuilder getRestClientBuilder(String hostname, String region, int port, String scheme)
    {
        return RestClient.builder(new HttpHost(hostname + "." + region + ".aws.found.io", port, scheme))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(cred.getCredentials()));
    }

    public HashMap<String, Object> getBuildData()
    {
        HashMap<String, Object> buildDataExCred = new HashMap<>();
        buildDataExCred.put("hostname", buildData.get("hostname"));
        buildDataExCred.put("region", buildData.get("region"));
        buildDataExCred.put("port", buildData.get("port"));
        buildDataExCred.put("scheme", buildData.get("scheme"));
        return buildDataExCred;
    }

    public JSONObject preformRequest(String method, String endpoint) throws IOException
    {
        response = restClient.performRequest(method, endpoint);

        return new JSONObject(EntityUtils.toString(response.getEntity()));
    }

    public JSONObject preformParmRequest(String method, String endpoint, HttpEntity entity) throws IOException
    {
        response = restClient.performRequest(method, endpoint, params, entity);

        return new JSONObject(EntityUtils.toString(response.getEntity()));
    }

    protected String getHostname()
    {
        return buildData.get("hostname").toString();
    }

    protected String getRegion()
    {
        return buildData.get("region").toString();
    }

    protected int getPort()
    {
        return (int)buildData.get("port");
    }

    protected String getScheme()
    {
        return buildData.get("scheme").toString();
    }

    protected Credentials getCred()
    {
        return cred;
    }

}
