import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;

public class Credentials
{
    private String userName;
    private String password;
    private String token;
    private String tokenDuration;
    private boolean authorized;
    private CredentialsProvider credentialsProvider;

    public Credentials(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
        token = "";
        authorized = false;
        credentialsProvider = new BasicCredentialsProvider();
    }

    public boolean authenticate()
    {
        return authorized;
    }

    public CredentialsProvider getCredentials()
    {
        return credentialsProvider;
    }

}
