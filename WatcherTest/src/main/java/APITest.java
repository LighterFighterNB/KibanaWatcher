import java.io.File;
import java.io.IOException;

public class APITest
{
    public static void main(String[] Args) throws IOException
    {
        ProcessBuilder pb = new ProcessBuilder(
                "curl",
                "-XGET",
                " https://bc2e0fb1ddbf540185dc508598e610d7.eu-west-1.aws.found.io:9243/_xpack/watcher/watch/my_watch");

        pb.directory(new File("/home/your_user_name/Pictures"));
        pb.redirectErrorStream(true);
        Process p = pb.start();

    }
}
