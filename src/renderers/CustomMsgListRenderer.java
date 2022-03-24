package renderers;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import client.InterfaceClient;
import data.Message;
import enums.StatutEnum;

import java.awt.Component;
import java.awt.Font;
import java.awt.Color;

public class CustomMsgListRenderer implements ListCellRenderer<Message> {
	
	private int width;
	
	private JLabel label;
	
	private Color selectedColor = new Color(54, 60, 70);
	
	public CustomMsgListRenderer(int width) {
		this.width = width;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Message> list, Message value, int index, boolean isSelected, boolean cellHasFocus) {
		
		JPanel panel = new JPanel();
		
		panel.setBackground(InterfaceClient.COULEUR_INTERFACE);
		
		if (isSelected)
	        panel.setBackground(this.selectedColor);
		
		String message = String.format(value.toString(), this.width);
		
		this.label = new JLabel(message);
		
		this.label.setFont(new Font(InterfaceClient.STYLE_TEXTE, Font.PLAIN, 10));
		
		if(value.getStatus().equals(StatutEnum.EN_ATTENTE))
			this.label.setForeground(Color.gray);
		
		else if(value.getStatus().equals(StatutEnum.RECU_SERVEUR))
			this.label.setForeground(new Color(184, 27, 72));
		
		else if(value.getStatus().equals(StatutEnum.NON_LU_PAR_TOUS))
			this.label.setForeground(Color.orange);
		
		else
			this.label.setForeground(Color.green);
		
		panel.add(this.label);

		
		return panel;
	}

}