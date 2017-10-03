import org.elasticsearch.client.Response;

import static javax.swing.text.html.HTML.Tag.HEAD;
import com.unboundid.util.json.JSONException;

import java.io.BufferedReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class RESTapi
{
    public static void main(String[] Args) throws IOException, JSONException
    {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("Eric", "password"));

        RestClientBuilder builder = RestClient.builder(new HttpHost("bc2e0fb1ddbf540185dc508598e610d7.eu-west-1.aws.found.io", 9243, "https"))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });

        RestClient restClient = builder.build();

        String endpointWatcher = "/_xpack/watcher/watch/";

        Map<String, String> params = Collections.emptyMap();

        String trigger = "";

        String jsonString = "{\n"
                + "  \"trigger\": {\n"
                + "    \"schedule\": {\n"
                + "      \"interval\": \"50s\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"input\": {\n"
                + "    \"search\": {\n"
                + "      \"request\": {\n"
                + "        \"search_type\": \"query_then_fetch\",\n"
                + "        \"indices\": [\n"
                + "          \"shakes*\"\n"
                + "        ],\n"
                + "        \"types\": [],\n"
                + "        \"body\": {\n"
                + "          \"size\": 10,\n"
                + "          \"query\": {\n"
                + "            \"bool\": {\n"
                + "              \"must\": {\n"
                + "                \"match\": {\n"
                + "                  \"play_name\": \"Henry IV\"\n"
                + "                }\n"
                + "              }\n"
                + "            }\n"
                + "          }\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  },\n"
                + "  \"condition\": {\n"
                + "    \"compare\": {\n"
                + "      \"ctx.payload.hits.total\": {\n"
                + "        \"gt\": 0\n"
                + "      }\n"
                + "    }\n"
                + "  },\n"
                + "  \"actions\": {\n"
                + "    \"log\": {\n"
                + "      \"logging\": {\n"
                + "        \"level\": \"info\",\n"
                + "        \"text\": \"WARNING\"\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}";

        //Response response = restClient.performRequest("GET", "/.watches/_search");
        //Response response = restClient.performRequest("PUT", endpointWatcher+"my-watch",params, entity);
        //Response response = restClient.performRequest("GET",endpointWatcher+"my-watch");


        //JSONObject myObject = new JSONObject(json);

        Response getRequest = restClient.performRequest("GET", endpointWatcher + "my-watch");
        // get the data from the watcher
        JSONObject myobject = new JSONObject(EntityUtils.toString(getRequest.getEntity()));
        // converts data from watcher to object
        setInterval(myobject, "2h");
        // changes the interval of the watcher
        updateWatcher(myobject, restClient, endpointWatcher+"my-watch", params);
        // updates the watcher
        restClient.close();

    }

    public static void updateWatcher(JSONObject JSON, RestClient restClient, String endpoint, Map<String, String> params) throws IOException {
        
        JSON = JSON.getJSONObject("watch");
        String trigger = JSON.getJSONObject("trigger").toString();
        String input = JSON.getJSONObject("input").toString();
        String condition = JSON.getJSONObject("condition").toString();
        String actions = JSON.getJSONObject("actions").toString();
        String jsonString = "{\n"
                + "  \"trigger\":" + trigger + ",\n"
                + "  \"input\":" + input + ",\n"
                + "  \"condition\":" + condition + ",\n"
                + "  \"actions\":" + actions + "}";
        System.out.println("The string sent was: " + jsonString);
        HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);
        Response response = restClient.performRequest("PUT", endpoint, params, entity);
        System.out.println("The response received was: " + EntityUtils.toString(response.getEntity()));

        System.out.println();
        //System.out.println(myObject.getField());

        restClient.close();
    }
    
    public static void setInterval(JSONObject JSON, String interval) {
        
        JSONObject schedule = JSON.getJSONObject("watch").getJSONObject("trigger").getJSONObject("schedule");
        schedule.put("interval", interval);
        
    }
}
