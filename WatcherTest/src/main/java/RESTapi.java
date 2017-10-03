
import com.unboundid.util.json.JSONException;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class RESTapi
{
    public static void main(String[] Args) throws IOException, JSONException
    {
        WatcherClient watcherClient = new WatcherClient("bc2e0fb1ddbf540185dc508598e610d7", "eu-west-1", 9243, "https", "Eric", "password");

        RestClient restClient = watcherClient.getRestClient();

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

        //Response getRequest = restClient.performRequest("GET", endpointWatcher + "my-watch");
        // get the data from the watcher
        //JSONObject myobject = new JSONObject(EntityUtils.toString(getRequest.getEntity()));
        // converts data from watcher to object
        //setInterval(myobject, "9h");
        // changes the interval of the watcher
        //updateWatcher(myobject, restClient, endpointWatcher + "my-watch", params);
        // updates the watcher
        //activateWatcher(restClient, endpointWatcher + "my-watch");
        getWatches(restClient, params);
        restClient.close();
        watcherClient.closeClient();

    }

    public static void updateWatcher(JSONObject JSON, RestClient restClient, String endpoint, Map<String, String> params) throws IOException
    {

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

    public static void setInterval(JSONObject JSON, String interval)
    {

        JSONObject schedule = JSON.getJSONObject("watch").getJSONObject("trigger").getJSONObject("schedule");
        schedule.put("interval", interval);

    }

    public static void activateWatcher(RestClient restClient, String endpoint) throws IOException
    {
        Response response = restClient.performRequest("PUT", endpoint + "/_activate");
    }

    public static void deactivateWatcher(RestClient restClient, String endpoint) throws IOException
    {
        Response response = restClient.performRequest("PUT", endpoint + "/_deactivate");
    }
    
    public static void getWatches(RestClient restClient, Map<String, String> params) throws IOException 
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
        for (int i = 0; i < watches.length(); i++) {
            JSONObject watch = watches.getJSONObject(i);
            System.out.println(watch.get("_id").toString());
        }
    }
}

