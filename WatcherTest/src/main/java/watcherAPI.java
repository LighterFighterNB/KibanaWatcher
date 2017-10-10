import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class watcherAPI
{
    private JPanel watcherAPIPanel;
    private JButton activateButton;
    private JButton deactivateButton;
    private JList<Object> actWatchList;
    private JList<Object> actWatchActiveList;
    private JButton refreshButton;
    private JTabbedPane tabbedPane1;
    private JList<Object> ackAcknowledgements;
    private JButton ackButton;
    private JList<Object> ackWatchIDList;

    private WatcherClient watcherClient;

    watcherAPI() throws IOException
    {
        watcherClient = new WatcherClient("bc2e0fb1ddbf540185dc508598e610d7", "eu-west-1", 9243, "https", "Eric", "password");
        setListDate();
        activateButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!actWatchList.isSelectionEmpty())
                {
                    try
                    {
                        watcherClient.createRequest("PUT", actWatchList.getSelectedValue() + "/_activate");
                        setActivateListData();

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
                if (!actWatchList.isSelectionEmpty())
                {
                    try
                    {
                        watcherClient.createRequest("PUT", actWatchList.getSelectedValue() + "/_deactivate");
                        setActivateListData();
                    } catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
        refreshButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    setListDate();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        });
        ackButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!ackWatchIDList.isSelectionEmpty())
                {
                    try
                    {
                        System.out.println(ackWatchIDList.getSelectedValue());
                        watcherClient.createRequest("PUT", ackWatchIDList.getSelectedValue() + "/_ack");
                        setAckPanel();
                    } catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private void setActivateListData() throws IOException
    {
        actWatchList.setListData(watcherClient.getWatchIDArray());
        actWatchActiveList.setListData(watcherClient.getWatchState());
    }

    private void setAckPanel() throws IOException
    {
        ackWatchIDList.setListData(watcherClient.getWatchIDArray());
        ackAcknowledgements.setListData(watcherClient.getWatchAckno());
    }

    private void setListDate() throws IOException
    {
        setActivateListData();
        setAckPanel();
    }


    void runDisplay() throws IOException
    {
        JFrame frame = new JFrame("watcherAPI");
        frame.setContentPane(new watcherAPI().watcherAPIPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
