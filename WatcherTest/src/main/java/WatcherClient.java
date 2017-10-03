import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class WatcherClient
{
    private CredentialsProvider credentialsProvider;
    private RestClientBuilder restClientBuilder;
    private RestClient restClient;
    private String endpointWatcher = "/_xpack/watcher/watch/";
    private Map<String, String> params = Collections.emptyMap();

    public WatcherClient(String hostname, String region, int port, String scheme, String username, String password)
    {
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

    public void closeClient() throws IOException
    {
        restClient.close();
    }
}
