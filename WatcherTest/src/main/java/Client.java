import org.apache.http.HttpHost;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

public class Client
{
    private Credentials cred;
    private RestClientBuilder restClientBuilder;
    private RestClient restClient;

    public Client(String hostname, String region, int port, String scheme, Credentials cred)
    {
        this.cred = cred;
        if(cred.authenticate())
        {
            restClientBuilder = getRestClientBuilder(hostname, region, port, scheme);
            restClient = restClientBuilder.build();
        }
    }

    public RestClientBuilder getRestClientBuilder(String hostname, String region, int port, String scheme)
    {
        RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(hostname + "." + region + ".aws.found.io", port, scheme))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback()
                {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder)
                    {
                        return httpClientBuilder.setDefaultCredentialsProvider(cred.getCredentials());
                    }
                });
        return restClientBuilder;
    }
}
