package gamesincommon;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.miginfocom.swing.MigLayout;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.community.SteamId;
import java.awt.GridLayout;
import javax.swing.JCheckBox;

public class PlayerGui {

  private JFrame frame;
  
  private JPanel playerPanel;
  private CardLayout playerLayout;
  
  private JPanel userDisplayPanel;
  private DefaultListModel<SteamId> playerNameModel;
  private JList<SteamId> playerNameList;
  private JButton changeUserButton;
  
  private JPanel userEnterPanel;
  private JTextField userEnterTextField;
  private ActionListener selectUserAction;
  private JButton selectUserButton = new JButton("Select user");
  
  private JPanel playerFriendsPanel;
  private DefaultListModel<SteamId> playerFriendsModel;
  private JList<SteamId> playerFriendsList;
  
  private GamesInCommon gamesInCommon = new GamesInCommon();

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (UnsupportedLookAndFeelException e) {
        e.printStackTrace();
    } catch (IllegalAccessException e) {
        e.printStackTrace();
    } catch (InstantiationException e) {
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
    
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          PlayerGui window = new PlayerGui();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    
  }

  /**
   * Create the application.
   */
  public PlayerGui() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    
    frame = new JFrame();
    frame.setBounds(100, 100, 450, 600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[grow][grow]"));
    
    playerPanel = new JPanel();
    frame.getContentPane().add(playerPanel, "cell 0 0,grow");
    playerLayout = new CardLayout(0, 0);
    playerPanel.setLayout(playerLayout);
    
    userEnterPanel = new JPanel();
    playerPanel.add(userEnterPanel);
    userEnterPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));
    
    userEnterTextField = new JTextField();
    userEnterPanel.add(userEnterTextField, "flowx,cell 0 0,grow");
    
    selectUserAction = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addUser();
        
      }
    };
    
    userEnterTextField.addKeyListener(new KeyListener() {
      @Override
      public void keyPressed(KeyEvent e) {
      }
      @Override
      public void keyReleased(KeyEvent e) {
      }
      @Override
      public void keyTyped(KeyEvent e) {
        
        char c = e.getKeyChar();

        if (c == KeyEvent.VK_ENTER) {
          addUser();
        }
      }
    });
    
    selectUserButton = new JButton("Select user");
    selectUserButton.addActionListener(selectUserAction);
    userEnterPanel.add(selectUserButton, "cell 0 0");
    
    userDisplayPanel = new JPanel();
    playerPanel.add(userDisplayPanel);
    userDisplayPanel.setLayout(new MigLayout("","[grow]","[grow]"));
    
    playerNameModel = new DefaultListModel<SteamId>();
    playerNameList = new JList<SteamId>(playerNameModel);
    playerNameList.setCellRenderer(new PlayerListRenderer());
    userDisplayPanel.add(playerNameList, "flowx,cell 0 0,grow");
    
    changeUserButton = new JButton("Change user");
    changeUserButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        playerLayout.next(playerPanel);
      }
    });
    userDisplayPanel.add(changeUserButton, "cell 0 0");
    
    playerFriendsPanel = new JPanel();
    frame.getContentPane().add(playerFriendsPanel, "cell 0 1,grow");
    playerFriendsPanel.setLayout(new GridLayout(1, 0, 0, 0));
    
    playerFriendsModel = new DefaultListModel<SteamId>();
    playerFriendsList = new JList<SteamId>(playerFriendsModel);
    playerFriendsList.setCellRenderer(new PlayerCheckRenderer());
    playerFriendsPanel.add(playerFriendsList);
    
  }
  
  private void addUser() {
    String name = userEnterTextField.getText();
    
    if ((!name.isEmpty()) && (!name.equals("Enter player name..."))) {
      try {  
        SteamId id = gamesInCommon.checkSteamId(name);
        playerNameModel.clear();
        playerNameModel.addElement(id);
        userEnterTextField.setText("");
        playerLayout.next(playerPanel);
        displayFriends(id.getFriends());
      } 
      catch (SteamCondenserException ex) {
        ex.printStackTrace();
        userEnterTextField.setText(ex.getMessage());
        userEnterTextField.selectAll();
      }
      
    }
  }
  
  private void displayFriends(SteamId[] friends) {
    for (SteamId id : friends) {
      try {
        id.fetchData();
        System.out.println("Data fetched for id: " + id.getNickname());
        playerFriendsModel.addElement(id);
      } catch (SteamCondenserException e) {
        e.printStackTrace();
      }
    }
  }
  
  class PlayerCheckRenderer extends PlayerListRenderer {

    private static final long serialVersionUID = 1L;
    private JCheckBox checkBox = new JCheckBox();
    
    public PlayerCheckRenderer() {
      super();
      add(checkBox);
    }
    
  }
  
  class PlayerListRenderer extends DefaultListCellRenderer {
    
    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(
        JList<?> list, Object value, int index,
        boolean isSelected, boolean cellHasFocus) {
      
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      
      if (value instanceof SteamId) {
        SteamId id = (SteamId) value;
        setText(id.getNickname());
        try {
          ImageIcon icon = new ImageIcon(new URL(id.getAvatarIconUrl())); 
          setIcon(icon);
        } catch (MalformedURLException e) {
          e.printStackTrace();
        }
      }
       
      return this;
      
    }

  }
}
