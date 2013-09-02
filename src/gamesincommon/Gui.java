package gamesincommon;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.GridLayout;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;

public class Gui {

  private JFrame GamesInCommonFrame;
  private JTextField addPlayerText;
  private GamesInCommon gamesInCommon;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Gui window = new Gui();
          window.GamesInCommonFrame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public Gui() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    GamesInCommonFrame = new JFrame();
    GamesInCommonFrame.setTitle("Games in Common");
    GamesInCommonFrame.setBounds(100, 100, 900, 350);
    GamesInCommonFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JPanel playerPanel = new JPanel();
    playerPanel.setBorder(new TitledBorder(null, "Players", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    
    JPanel optionsPanel = new JPanel();
    optionsPanel.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    
    JPanel consolePanel = new JPanel();
    consolePanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    
    JPanel panel = new JPanel();
    panel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    
    JPanel panel_1 = new JPanel();
    panel_1.setBorder(new TitledBorder(null, "Output", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    GroupLayout groupLayout = new GroupLayout(GamesInCommonFrame.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(consolePanel, GroupLayout.DEFAULT_SIZE, 858, Short.MAX_VALUE)
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                  .addComponent(optionsPanel, GroupLayout.PREFERRED_SIZE, 267, GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(panel, GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))
                .addComponent(playerPanel, GroupLayout.PREFERRED_SIZE, 430, GroupLayout.PREFERRED_SIZE))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 421, GroupLayout.PREFERRED_SIZE)))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGap(15)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(playerPanel, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE)
              .addGap(18)
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                .addComponent(optionsPanel, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
                .addComponent(panel, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)))
            .addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 249, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(consolePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addContainerGap())
    );
    panel_1.setLayout(new GridLayout(1, 0, 0, 0));
    
    JScrollPane scrollPane_1 = new JScrollPane();
    panel_1.add(scrollPane_1);
    
    JTextArea textArea = new JTextArea();
    scrollPane_1.setViewportView(textArea);
    panel.setLayout(new GridLayout(0, 1, 0, 0));
    
    JButton btnScan = new JButton("Scan");
    btnScan.setFont(new Font("Tahoma", Font.PLAIN, 18));
    panel.add(btnScan);
    
    JLabel consoleLabel = new JLabel("");
    consoleLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
    GroupLayout gl_consolePanel = new GroupLayout(consolePanel);
    gl_consolePanel.setHorizontalGroup(
      gl_consolePanel.createParallelGroup(Alignment.LEADING)
        .addComponent(consoleLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
    );
    gl_consolePanel.setVerticalGroup(
      gl_consolePanel.createParallelGroup(Alignment.TRAILING)
        .addGroup(Alignment.LEADING, gl_consolePanel.createSequentialGroup()
          .addComponent(consoleLabel)
          .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    consolePanel.setLayout(gl_consolePanel);
    
    JPanel checkBoxPanel = new JPanel();
    GroupLayout gl_optionsPanel = new GroupLayout(optionsPanel);
    gl_optionsPanel.setHorizontalGroup(
      gl_optionsPanel.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_optionsPanel.createSequentialGroup()
          .addContainerGap()
          .addComponent(checkBoxPanel, GroupLayout.PREFERRED_SIZE, 205, Short.MAX_VALUE)
          .addContainerGap())
    );
    gl_optionsPanel.setVerticalGroup(
      gl_optionsPanel.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_optionsPanel.createSequentialGroup()
          .addContainerGap()
          .addComponent(checkBoxPanel, GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
          .addContainerGap())
    );
    checkBoxPanel.setLayout(new GridLayout(1, 2, 0, 0));
    
    JCheckBox multiplayerCheckBox = new JCheckBox("Multiplayer");
    multiplayerCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 18));
    checkBoxPanel.add(multiplayerCheckBox);
    
    JCheckBox coopCheckBox = new JCheckBox("Co-op");
    coopCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 18));
    checkBoxPanel.add(coopCheckBox);
    optionsPanel.setLayout(gl_optionsPanel);
    
    JPanel playerButtonPanel = new JPanel();
    playerButtonPanel.setLayout(new GridLayout(1, 2, 0, 0));
    
    JButton addButton = new JButton("Add");
    addButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
    playerButtonPanel.add(addButton);
    
    JButton removeButton = new JButton("Remove");
    removeButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
    playerButtonPanel.add(removeButton);
    
    addPlayerText = new JTextField();
    addPlayerText.setText("Enter player name...");
    addPlayerText.setFont(new Font("Tahoma", Font.ITALIC, 20));
    addPlayerText.setColumns(10);
    
    JScrollPane scrollPane = new JScrollPane();
    GroupLayout gl_playerPanel = new GroupLayout(playerPanel);
    gl_playerPanel.setHorizontalGroup(
      gl_playerPanel.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_playerPanel.createSequentialGroup()
          .addContainerGap()
          .addGroup(gl_playerPanel.createParallelGroup(Alignment.LEADING, false)
            .addComponent(playerButtonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(addPlayerText, GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE))
          .addGap(12)
          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
          .addContainerGap())
    );
    gl_playerPanel.setVerticalGroup(
      gl_playerPanel.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_playerPanel.createSequentialGroup()
          .addContainerGap()
          .addGroup(gl_playerPanel.createParallelGroup(Alignment.LEADING)
            .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
            .addGroup(gl_playerPanel.createSequentialGroup()
              .addComponent(addPlayerText, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.UNRELATED)
              .addComponent(playerButtonPanel, GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)))
          .addContainerGap())
    );
    
    JTextArea playerListText = new JTextArea();
    playerListText.setEditable(false);
    scrollPane.setViewportView(playerListText);
    playerPanel.setLayout(gl_playerPanel);
    GamesInCommonFrame.getContentPane().setLayout(groupLayout);
  }
}
