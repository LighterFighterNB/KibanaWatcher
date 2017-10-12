import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class JSON
{
    private JSONParser parser;
    private JSONObject jsonObject;
    private Object jsonFile;
    private HttpEntity entity;

    public JSON()
    {
        parser = new JSONParser();
        jsonObject = new JSONObject();
        jsonFile = new Object();
        entity = null;
    }

    public HttpEntity parse(String location) throws IOException, ParseException
    {
        jsonFile = parser.parse(new FileReader(location));

        jsonObject = (JSONObject) jsonFile;
        entity = new NStringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON);
        return entity;
    }
}
