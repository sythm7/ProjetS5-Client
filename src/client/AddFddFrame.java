package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import listeners.ConfirmAddFddListener;
import renderers.CustomGroupsListRenderer;

public class AddFddFrame extends JFrame {
	
	/**
	 * Faut return
	 */
	private static final long serialVersionUID = 1L;
	private InterfaceClient clientFrame;
	
	private int xDim = 400;
	private int yDim = 500;
	private static final Color COULEUR_TEXTE = new Color(0, 0, 0);
	
	private JPanel panel = new JPanel();
	private BoxLayout boxLayout = new BoxLayout(this.panel, BoxLayout.Y_AXIS);
	private JTextField contentEntryField = new JTextField();
	private JLabel contentEntryFieldLabel = new JLabel("Contenu du premier message");
	private JTextField fddNameField = new JTextField();
	private JLabel fddNameFieldLabel = new JLabel("Nom du fil de discussion");
	private JLabel groupsListLabel = new JLabel("Groupe concerné");
	
	private DefaultListModel<String> groupsModel = new DefaultListModel<>();
	
	private JList<String> groupsList = new JList<>(this.groupsModel);
	
	private JScrollPane groupsScrollPane = new JScrollPane(this.groupsList);
	
	private JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JButton confirmButton = new JButton("Ajouter");
	private JButton cancelButton = new JButton("Annuler");

	public AddFddFrame(InterfaceClient clientFrame) {
		this.clientFrame = clientFrame;
	}

	public void initialiser() {
		
		this.setTitle("Ajouter un fil de discussion");
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.setResizable(false);
		
		this.setPreferredSize(new Dimension(xDim, yDim));
		
		this.setSize(xDim, yDim);
		
		this.setLocationRelativeTo(this.clientFrame);
		
		this.panel.setLayout(this.boxLayout);
		
		this.setLabelStyle(contentEntryFieldLabel);
		
		this.setLabelStyle(fddNameFieldLabel);
		
		this.setLabelStyle(groupsListLabel);
		
		this.initComponentStyle();
		
		this.initGroupsList();
		
		this.panel.setBorder(new EmptyBorder(new Insets(15, 15, 15, 15)));
		
		
		this.contentEntryField.setMaximumSize(new Dimension(300, 0));
		this.fddNameField.setMaximumSize(new Dimension(200, 0));
		this.groupsList.setVisibleRowCount(5);
		
		this.contentEntryFieldLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		
		
		this.addComponentsInFrame();
		
		this.addCancelButtonListener();
		
		this.addCloseButtonListener();
		
		this.addConfirmButtonListener();
		
		this.setAlwaysOnTop(true);
	}

	private void initGroupsList() {
		
		Client client = this.clientFrame.getClient();
	
		String getGroupsListRequest = new String("getGroupsList");
		
		client.getConnectionThread().sendObject(getGroupsListRequest);
		
		synchronized(client.getConnectionThread()) {
			try {
				client.getConnectionThread().wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		ArrayList<String> allGroupsList = client.getAllGroupsList();
		
		for(String groupe : allGroupsList) {
			this.groupsModel.addElement(groupe);
		}
		
	}
	
	private void addComponentsInFrame() {
		
		this.panel.add(Box.createRigidArea(new Dimension(0, 15)));
		this.panel.add(this.groupsListLabel);
		this.panel.add(Box.createRigidArea(new Dimension(0, 15)));
		this.panel.add(this.groupsScrollPane);
		this.panel.add(Box.createRigidArea(new Dimension(0, 15)));
		this.panel.add(this.fddNameFieldLabel);
		this.panel.add(Box.createRigidArea(new Dimension(0, 15)));
		this.panel.add(this.fddNameField);
		this.panel.add(Box.createRigidArea(new Dimension(0, 15)));
		this.panel.add(this.contentEntryFieldLabel);
		this.panel.add(Box.createRigidArea(new Dimension(0, 15)));
		this.panel.add(this.contentEntryField);
		this.panel.add(Box.createRigidArea(new Dimension(0, 50)));
		this.buttonsPanel.add(this.confirmButton);
		this.buttonsPanel.add(this.cancelButton);
		this.panel.add(this.buttonsPanel);
		this.add(this.panel);
		
	}

	private void initComponentStyle() {
		
		this.panel.setBackground(InterfaceClient.COULEUR_INTERFACE);
		this.panel.setForeground(InterfaceClient.COULEUR_TEXTE);
		this.contentEntryFieldLabel.setForeground(InterfaceClient.COULEUR_TEXTE);
		this.contentEntryField.setBackground(InterfaceClient.COULEUR_COMPOSANTS);
		this.contentEntryField.setForeground(COULEUR_TEXTE);
		this.fddNameFieldLabel.setForeground(InterfaceClient.COULEUR_TEXTE);
		this.fddNameField.setBackground(InterfaceClient.COULEUR_COMPOSANTS);
		this.fddNameField.setForeground(COULEUR_TEXTE);
		this.groupsListLabel.setForeground(InterfaceClient.COULEUR_TEXTE);
		this.groupsList.setBackground(InterfaceClient.COULEUR_INTERFACE);
		this.groupsList.setForeground(COULEUR_TEXTE);
		this.buttonsPanel.setBackground(InterfaceClient.COULEUR_INTERFACE);
		this.buttonsPanel.setForeground(InterfaceClient.COULEUR_TEXTE);
		this.confirmButton.setBackground(InterfaceClient.COULEUR_INTERFACE);
		this.confirmButton.setForeground(InterfaceClient.COULEUR_TEXTE);
		this.cancelButton.setBackground(InterfaceClient.COULEUR_INTERFACE);
		this.cancelButton.setForeground(InterfaceClient.COULEUR_TEXTE);
		this.contentEntryField.setCaretColor(COULEUR_TEXTE);
		this.fddNameField.setCaretColor(COULEUR_TEXTE);
		
		this.groupsList.setCellRenderer(new CustomGroupsListRenderer<>());
	}
	
	protected void setLabelStyle(JLabel label) {
		label.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		label.setFont(new Font(InterfaceClient.STYLE_TEXTE, Font.PLAIN, 15));
		label.setForeground(InterfaceClient.COULEUR_TEXTE);
	}
	
	private void addCancelButtonListener() {
		this.cancelButton.addActionListener(event -> {
            clientFrame.getButtonAddFdd().setEnabled(true);
            this.dispose();
		});
	}
	
    private void addCloseButtonListener() {
        
        JFrame frame = this;
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                clientFrame.getButtonAddFdd().setEnabled(true);
                frame.dispose();
            }
        });
    }
	
    private void addConfirmButtonListener() {
		ConfirmAddFddListener confirmAddFdd = new ConfirmAddFddListener(this, this.clientFrame);
		this.confirmButton.addActionListener(confirmAddFdd);
	}

	public String getContentEntryFieldText() {
		return contentEntryField.getText();
	}

	public String getFddNameFieldText() {
		return fddNameField.getText();
	}

	public String getGroupsListSelectedValue() {
		return groupsList.getSelectedValue();
	}

}
