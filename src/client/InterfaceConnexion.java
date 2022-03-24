package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import listeners.ConfirmConnexionListener;


public class InterfaceConnexion extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int xDim = 400;
	private int yDim = 300;
	private static final Color COULEUR_TEXTE = new Color(0, 0, 0);
	private InterfaceClient interfaceClient;
	
	private JPanel panel = new JPanel();
	private BoxLayout boxLayout = new BoxLayout(this.panel, BoxLayout.Y_AXIS);
	private JTextField idTextField = new JTextField();
	private JLabel idTextFieldLabel = new JLabel("Identifiant");
	private JLabel passwordTextFieldLabel = new JLabel("Mot de passe");
	private JPasswordField passwordTextField = new JPasswordField();
	
	private JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JButton confirmButton = new JButton("Se connecter");
	private JButton cancelButton = new JButton("Annuler");
	
	private Client client;

	public InterfaceConnexion(Client client) {
		this.client = client;
		this.interfaceClient = new InterfaceClient(this.client);
	}

	public void initialiser() {
		
		this.setTitle("Connexion au compte");
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setResizable(false);
		
		this.setPreferredSize(new Dimension(xDim, yDim));
		
		this.setSize(xDim, yDim);
		
		this.setLocationRelativeTo(null);
		
		this.panel.setLayout(this.boxLayout);
		
		this.setLabelStyle(idTextFieldLabel);
		
		this.setLabelStyle(passwordTextFieldLabel);
		
		this.initComponentStyle();
		
		this.panel.setBorder(new EmptyBorder(new Insets(15, 15, 15, 15)));
		
		this.idTextField.setMaximumSize(new Dimension(200, 0));
		
		this.passwordTextField.setMaximumSize(new Dimension(200, 0));
		
		this.idTextFieldLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		
		this.addComponentsInFrame();
		
		this.pack();
		
		this.addCancelButtonListener();
		
		this.addCloseButtonListener();
		
		this.addConfirmButtonListener();
		
		this.setVisible(true);
	} 
	
	private void addComponentsInFrame() {
		
		this.panel.add(Box.createRigidArea(new Dimension(0, 15)));
		this.panel.add(this.idTextFieldLabel);
		this.panel.add(Box.createRigidArea(new Dimension(0, 15)));
		this.panel.add(this.idTextField);
		
		this.panel.add(Box.createRigidArea(new Dimension(0, 15)));
		this.panel.add(this.passwordTextFieldLabel);
		this.panel.add(Box.createRigidArea(new Dimension(0, 15)));
		this.panel.add(this.passwordTextField);
		this.panel.add(Box.createRigidArea(new Dimension(0, 50)));
		
		this.buttonsPanel.add(this.confirmButton);
		this.buttonsPanel.add(this.cancelButton);
		this.panel.add(this.buttonsPanel);
		this.add(this.panel);
		
	}

	private void initComponentStyle() {
		
		this.panel.setBackground(InterfaceClient.COULEUR_INTERFACE);
		this.panel.setForeground(InterfaceClient.COULEUR_TEXTE);
		this.idTextFieldLabel.setForeground(InterfaceClient.COULEUR_TEXTE);
		this.idTextField.setBackground(InterfaceClient.COULEUR_COMPOSANTS);
		this.idTextField.setForeground(COULEUR_TEXTE);
		this.passwordTextFieldLabel.setForeground(InterfaceClient.COULEUR_TEXTE);
		this.passwordTextField.setBackground(InterfaceClient.COULEUR_COMPOSANTS);
		this.passwordTextField.setForeground(COULEUR_TEXTE);
		this.buttonsPanel.setBackground(InterfaceClient.COULEUR_INTERFACE);
		this.buttonsPanel.setForeground(InterfaceClient.COULEUR_TEXTE);
		this.confirmButton.setBackground(InterfaceClient.COULEUR_INTERFACE);
		this.confirmButton.setForeground(InterfaceClient.COULEUR_TEXTE);
		this.cancelButton.setBackground(InterfaceClient.COULEUR_INTERFACE);
		this.cancelButton.setForeground(InterfaceClient.COULEUR_TEXTE);
		this.idTextField.setCaretColor(COULEUR_TEXTE);
	}
	
	protected void setLabelStyle(JLabel label) {
		label.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		label.setFont(new Font(InterfaceClient.STYLE_TEXTE, Font.PLAIN, 15));
		label.setForeground(InterfaceClient.COULEUR_TEXTE);
	}
	
	private void addCancelButtonListener() {
		this.cancelButton.addActionListener(event -> {
            this.dispose();
		});
	}
	
    private void addCloseButtonListener() {
        
        JFrame frame = this;
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                frame.dispose();
            }
        });
    }
	
    private void addConfirmButtonListener() {
		ConfirmConnexionListener confirmConnexionListener = new ConfirmConnexionListener(this, this.interfaceClient);
		this.confirmButton.addActionListener(confirmConnexionListener);
		
	}

	public String getIdTextFieldText() {
		return idTextField.getText().trim();
	}

	public String getPasswordTextFieldText() {
		char[] pwdChar = this.passwordTextField.getPassword();
		return String.copyValueOf(pwdChar).trim();
	}
}
