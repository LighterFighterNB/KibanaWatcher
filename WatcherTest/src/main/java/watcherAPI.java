import org.elasticsearch.client.Response;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class watcherAPI
{
    private JPanel watcherAPIPanel;
    private JButton activateButton;
    private JButton deactivateButton;
    private JList watchIDList;
    private JList watchActiveList;

    private WatcherClient watcherClient;
    private Response response;

    public watcherAPI() throws IOException
    {
        response = null;
        watcherClient = new WatcherClient("bc2e0fb1ddbf540185dc508598e610d7", "eu-west-1", 9243, "https", "Eric", "password");
        setListData();
        activateButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(!watchIDList.isSelectionEmpty())
                {
                    try
                    {
                        watcherClient.createRequest("PUT",watchIDList.getSelectedValue()+"/_activate");
                        setListData();

                    } catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
        deactivateButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(!watchIDList.isSelectionEmpty())
                {
                    try
                    {
                        watcherClient.createRequest("PUT",watchIDList.getSelectedValue()+"/_deactivate");
                        setListData();
                    } catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    public void setListData() throws IOException
    {
        watchIDList.setListData(watcherClient.getWatchIDs().keySet().toArray());
        watchActiveList.setListData(watcherClient.getWatchIDs().values().toArray());
    }

    public void runDisplay() throws IOException
    {
        JFrame frame = new JFrame("watcherAPI");
        frame.setContentPane(new watcherAPI().watcherAPIPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
