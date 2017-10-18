package Client;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class Credentials
{
    private CredentialsProvider credentialsProvider;

    public Credentials(String userName, String password)
    {
        credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(userName, password));
    }

    public CredentialsProvider getCredentials()
    {
        return credentialsProvider;
    }

    public boolean authorise(Client client) throws IOException
    {
        boolean auth = false;
        try
        {
            JSONObject response = client.preformRequest("GET", "/_xpack/security/_authenticate");
            String enabled = response.get("enabled").toString();
            JSONArray roles = response.getJSONArray("roles");

            if (enabled.equals("true"))
            {
                for (int i = 0; i < roles.length(); i++)
                {
                    System.out.println(roles.get(i));
                    if (roles.get(i).equals("watcher_user") || roles.get(i).equals("watcher_admin") || roles.get(i).equals("superuser"))
                    {
                        auth = true;
                    }
                }
            }
        } catch (Exception e)
        {
            System.out.println(e);
            System.out.println("User not authorized");
        }
        return auth;
    }


}
