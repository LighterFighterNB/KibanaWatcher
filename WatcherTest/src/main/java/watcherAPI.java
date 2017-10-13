import org.apache.http.HttpEntity;
import org.apache.http.ParseException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class watcherAPI
{
    private JFrame frame;
    private JPanel watcherAPIPanel;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItem;
    private JButton activateButton;
    private JButton deactivateButton;
    private JList<Object> actWatchList;
    private JList<Object> actWatchActiveList;
    private JButton refreshButton;
    private JTabbedPane tabbedPanel;
    private JList<Object> ackAcknowledgements;
    private JButton ackButton;
    private JList<Object> ackWatchIDList;
    private JTextField watcherLocation;
    private JButton watcherSubmit;
    private JTextField watcherName;
    private JLabel watchNameLabel;
    private JLabel watchLocaLabel;
    private JButton editButton;
    private JTextField timeField;
    private JButton editSubmitButton;
    private JPanel editTab;
    private JTextField watcherNameTextField;
    private JPanel ActiveTab;

    private WatcherClient watcherClient;
    private JSON json;

    watcherAPI() throws IOException
    {
        json = new JSON();
        watcherClient = new WatcherClient("a8cfaa58af43b19a22f137ff349c9c2d", "eu-west-1", 9243, "https", "admin", "admin01");
        setListData();
        activateButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (!actWatchList.isSelectionEmpty())
                {
                    System.out.println(actWatchList.getSelectedIndices().length);
                    if (actWatchList.getSelectedIndices().length > 1)
                    {
                        int[] indexes = actWatchList.getSelectedIndices();
                        for (int i = 0; i < indexes.length; i++)
                        {
                            try
                            {
                                actWatchList.setSelectedIndex(indexes[i]);
                                watcherClient.createRequest("PUT", actWatchList.getSelectedValue() + "/_activate");
                            } catch (IOException e1)
                            {
                                e1.printStackTrace();
                            }
                        }
                        try
                        {
                            setActivateListData();
                        } catch (IOException e1)
                        {
                            e1.printStackTrace();
                        }
                    } else
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
            }
        });
        deactivateButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!actWatchList.isSelectionEmpty())
                {
                    System.out.println(actWatchList.getSelectedIndices().length);
                    if (actWatchList.getSelectedIndices().length > 1)
                    {
                        int[] indexes = actWatchList.getSelectedIndices();
                        for (int i = 0; i < indexes.length; i++)
                        {
                            try
                            {
                                actWatchList.setSelectedIndex(indexes[i]);
                                watcherClient.createRequest("PUT", actWatchList.getSelectedValue() + "/_deactivate");
                            } catch (IOException e1)
                            {
                                e1.printStackTrace();
                            }
                        }
                        try
                        {
                            setActivateListData();
                        } catch (IOException e1)
                        {
                            e1.printStackTrace();
                        }
                    } else
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
            }
        });
        ackButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!ackWatchIDList.isSelectionEmpty())
                {
                    System.out.println(ackWatchIDList.getSelectedIndices().length);
                    if (ackWatchIDList.getSelectedIndices().length > 1)
                    {
                        int[] indexes = ackWatchIDList.getSelectedIndices();
                        for (int i = 0; i < indexes.length; i++)
                        {
                            try
                            {
                                ackWatchIDList.setSelectedIndex(indexes[i]);
                                watcherClient.createRequest("PUT", ackWatchIDList.getSelectedValue() + "/_ack");
                            } catch (IOException e1)
                            {
                                e1.printStackTrace();
                            }
                        }
                        try
                        {
                            setActivateListData();
                        } catch (IOException e1)
                        {
                            e1.printStackTrace();
                        }
                    } else
                    {
                        try
                        {
                            System.out.println(ackWatchIDList.getSelectedValue());
                            watcherClient.createRequest("PUT", ackWatchIDList.getSelectedValue() + "/_ack");
                        } catch (IOException e1)
                        {
                            e1.printStackTrace();
                        }
                    }
                }
                try
                {
                    setListData();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
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
                    setListData();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        });
        watcherSubmit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!watcherLocation.getText().equals("") && !watcherName.getText().equals(""))
                {
                    try
                    {
                        HttpEntity watcher = json.parse(watcherLocation.getText());
                        watcherClient.createParmRequest("POST", watcherName.getText(), watcher);
                        watcherLocation.setText("");
                        watcherName.setText("");
                        setListData();
                    } catch (IOException e1)
                    {
                        e1.printStackTrace();
                    } catch (ParseException e1)
                    {
                        e1.printStackTrace();
                    } catch (org.json.simple.parser.ParseException e1)
                    {
                        e1.printStackTrace();
                    }

                }
            }
        });
        editButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!actWatchList.isSelectionEmpty())
                {
                    editTab.setEnabled(true);
                    tabbedPanel.setSelectedComponent(editTab);
                    String watcherID = actWatchList.getSelectedValue().toString();

                    watcherNameTextField.setText(watcherID);
                    timeField.setText(watcherClient.getWatchInterval(watcherID));
                }
            }
        });
        editSubmitButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    watcherClient.setInterval(watcherNameTextField.getText(), timeField.getText());
                    tabbedPanel.setSelectedComponent(ActiveTab);
                    setListData();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void setActivateListData() throws IOException
    {
        actWatchList.setListData(watcherClient.getWatchIDArray());
        actWatchActiveList.setListData(watcherClient.getWatchState());
        actWatchActiveList.setEnabled(false);
    }

    private void setAckPanel() throws IOException
    {
        ackWatchIDList.setListData(watcherClient.getWatchIDArray());
        ackAcknowledgements.setListData(watcherClient.getWatchAckno());
    }

    private void setListData() throws IOException
    {
        setActivateListData();
        setAckPanel();
    }


    void runDisplay() throws IOException
    {
        JFrame frame = new JFrame("watcherAPI");
        frame.setContentPane(new watcherAPI().watcherAPIPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                try
                {
                    watcherClient.closeClient();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        });
        frame.pack();
        frame.setVisible(true);
    }

}
