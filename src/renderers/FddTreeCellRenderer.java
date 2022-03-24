package renderers;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import client.InterfaceClient;

import java.awt.*;


public class FddTreeCellRenderer extends DefaultTreeCellRenderer {

	private Color selectedColor = new Color(54, 60, 70);
	
	private static final long serialVersionUID = -3452124074088778903L;
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		
		JPanel panel = new JPanel();
		
		JLabel label = new JLabel();
		
		String message;
		
		
//		DefaultMutableTreeNode valueNode = (DefaultMutableTreeNode) value;
//		if(leaf && valueNode.getParent().getParent() == null)
//			message = "<html><b STYLE=\"color: rgb(41, 128, 185); font-size: 2em;\">" + value.toString() + "</b></html>";
		if(leaf) 
			message = "<html><h1 STYLE=\"font-size: 1.2em; width: 200px; margin: 3px 0px;\">" + value.toString() + "</h1></html>";
		else
			message = "<html><b STYLE=\"color: rgb(41, 128, 185); font-size: 2em;\">" + value.toString() + "</b></html>";
		
		
		if(isSelected)
			panel.setBackground(selectedColor);
		else
			panel.setBackground(InterfaceClient.COULEUR_INTERFACE);
		
		label.setForeground(InterfaceClient.COULEUR_TEXTE);
		label.setFont(new Font(InterfaceClient.STYLE_TEXTE, Font.PLAIN, 10));
		
		label.setText(message);
		
		panel.add(label);
		
		return panel;
	}
	
}