package client;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.awt.*;
import java.time.LocalDateTime;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import data.Message;
import enums.StatutEnum;
import networking.NetworkMessage;
import renderers.CustomMsgListRenderer;
import renderers.FddTreeCellRenderer;
import data.FilDeDiscussion;

public class InterfaceClient extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private final int width = 1024;
	
	private final int height = 768;
	
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
	
	private DefaultTreeModel treeModel = new DefaultTreeModel(root);
	
	private JTree fddTree = new JTree(this.treeModel);
	
	private LinkedHashMap<String, LinkedHashSet<FilDeDiscussion>> groupTreeMap = null;
	
	protected Message messageSaisi;
	
	private FilDeDiscussion currentSelectedFdd;
	
	private String currentSelectedGroupe;
	
	private CustomMsgListRenderer msgListRenderer;
	
	private FddTreeCellRenderer renderer = new FddTreeCellRenderer();
	
	private final Dimension DIM_LEFT_COMPONENTS = new Dimension(300, 0);
	
	public static final Color COULEUR_INTERFACE = new Color(33, 37, 43);
	
	public static final Color COULEUR_TEXTE = new Color(221, 228, 241);
	
	public static final Color COULEUR_COMPOSANTS = new Color(192, 192, 192);
	
	public static String STYLE_TEXTE = "Arial";
	
	private JPanel mainContainer = new JPanel(new BorderLayout());
	
	private JPanel leftContainer = new JPanel();
	
	private BoxLayout boxLayout = new BoxLayout(this.leftContainer, BoxLayout.Y_AXIS); // Va avec le leftContainer
	
	private JButton buttonAddFdd = new JButton("Nouveau fil de discussion");
	
	private GridLayout gridLayout = new GridLayout();
	
	private JPanel sndLeftContainer = new JPanel(gridLayout);
	
	private JScrollPane ticketListScrollPane = new JScrollPane(this.fddTree);
	
	private JPanel centerContainer = new JPanel(new BorderLayout());
	
	private DefaultListModel<Message> listModel = new DefaultListModel<>();
	
	private JList<Message> msgList = new JList<>(listModel);
	
	private JScrollPane msgListScrollPane = new JScrollPane(this.msgList);
	
	private JTextField textEntry = new JTextField();
	
	private AddFddFrame addFddFrame = null;

	// Remettre les commentaires en UTF-8
	private Client client;

	private LinkedList<DefaultMutableTreeNode> expandedNodesList = new LinkedList<>();
	
	public InterfaceClient(Client client) {
		this.groupTreeMap = client.getGroupTreeMap();
		this.client = client;
	}
	
	// Creation et lancement de l'interface, crée les composants, leurs tailles etc..
	public void initialiser() {
		
		this.setTitle("ProjetS5 - Client");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setPreferredSize(new Dimension(1024, 768));
		
		this.setLocationRelativeTo(null);
		
		Point position = this.getLocation();
		
		this.setLocation((int)position.getX() - width / 2, (int)position.getY() - height / 2);

		this.msgListRenderer = new CustomMsgListRenderer(0);

		this.setMinimumSize(new Dimension(800, 600));

		this.initComponentStyle();
		this.addComponentsInFrame();
		
		this.sndLeftContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		this.sndLeftContainer.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
		
		this.leftContainer.setLayout(this.boxLayout);
		this.leftContainer.setPreferredSize(DIM_LEFT_COMPONENTS);
		this.ticketListScrollPane.setHorizontalScrollBar(null);
		
		this.addButtonFddListener();
		
		this.msgListScrollPane.setHorizontalScrollBar(null);

		this.textEntry.setMargin(new Insets(0, 5, 0, 5));
		this.centerContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
		this.textEntry.setPreferredSize(new Dimension(0, 35));

		this.pack();
		
		this.msgList.setFont(new Font(STYLE_TEXTE, Font.PLAIN, 10));
		this.msgList.setCellRenderer(this.msgListRenderer);
		
		this.fddTree.setCellRenderer(renderer);

		this.addFrameListener();
		this.addFddTreeListener();
		this.keyboardEnterKeyInputListener();
		this.addCloseButtonListener();
		
		this.centerContainer.setVisible(false);
	}
	
	public void setScrollBarToBottom() {
		int msgListSize = this.msgList.getModel().getSize();
		this.msgList.ensureIndexIsVisible(msgListSize - 1);
	}
	
	//Ajoute les composants dans la frame
	private void addComponentsInFrame() {
		this.sndLeftContainer.add(this.buttonAddFdd);
		this.leftContainer.add(this.ticketListScrollPane);
		this.leftContainer.add(this.sndLeftContainer);
		this.mainContainer.add(this.leftContainer, BorderLayout.LINE_START);
		this.mainContainer.add(this.centerContainer, BorderLayout.CENTER);
		this.centerContainer.add(this.msgListScrollPane, BorderLayout.CENTER);
		this.centerContainer.add(this.textEntry, BorderLayout.PAGE_END);
		this.add(this.mainContainer);
	}
	
	//Initialise le style des composants de la fenêtre
	private void initComponentStyle() {
		
		this.mainContainer.setBackground(COULEUR_INTERFACE);
		this.centerContainer.setBackground(COULEUR_INTERFACE);
		this.buttonAddFdd.setBackground(COULEUR_INTERFACE);
		this.buttonAddFdd.setForeground(COULEUR_TEXTE);
		this.textEntry.setBackground(new Color(67, 71, 77));
		this.textEntry.setCaretColor(COULEUR_TEXTE);
		this.textEntry.setForeground(COULEUR_TEXTE);
		this.msgList.setBackground(COULEUR_INTERFACE);
		this.msgList.setForeground(COULEUR_TEXTE);
		this.msgList.setSelectionBackground(new Color(54, 60, 70));
		this.msgList.setSelectionForeground(COULEUR_TEXTE);
		this.fddTree.setBackground(COULEUR_INTERFACE);
		
		this.renderer.setBackgroundNonSelectionColor(COULEUR_INTERFACE);
		this.renderer.setBackgroundSelectionColor(new Color(56, 63, 73));
		
		
		this.fddTree.setRootVisible(false);
	}

	/*
	 * Crée la structure de l'arbre : On fait un Entry car pour chaque groupe, on fait correspondre un nombre fini de fdd listés dans
	 * un ordre défini par l'utilisateur (ici selon la date du dernier message). Un groupe pourra donc avoir 1 à n fdd triés par la date
	 * de leur dernier message (d'où le TreeSet<FilDeDiscussion>). La fonction s'occupe de récupérer la liste <K, V> correspondant
	 * au nom du groupe avec ses fdd, et, pour chaque groupe, ajoute une branche dans l'arbre (le root est caché car ce n'est pas un groupe)
	 * (voir première ligne de la fonction, elle n'apparaît pas au lancement). Pendant qu'un groupe est ajouté dans l'arborescence,
	 * une boucle ajoute un à un les éléments de la liste des fdd contenus dans le TreeSet qui lui correspondent.
	 * 
	 * Finalement, on ajoute l'arbre créé dans le scrollPane ticketListScrollPane car c'est le composant qui nous permet de scroller
	 * si jamais le nombre de groupes auquel l'utilisateur s'est inscrit dépasse la limite du composant
	*/
	
	public void updateTree() {
		
		DefaultMutableTreeNode currentBranch = null;
		DefaultMutableTreeNode currentLeaf;
		
		DefaultMutableTreeNode currentSelectedFddNode = null;
		
		this.root.removeAllChildren();
		
		this.expandedNodesList.clear();
		
		for(String groupe : groupTreeMap.keySet()) {
			currentBranch = new DefaultMutableTreeNode(groupe);
			this.expandedNodesList.add(currentBranch);
			root.add(currentBranch);
			for(FilDeDiscussion fdd : groupTreeMap.get(groupe)) {
				currentLeaf = new DefaultMutableTreeNode(fdd);
				currentBranch.add(currentLeaf);
				if(this.currentSelectedFdd != null && fdd.idF == this.currentSelectedFdd.idF) {
					this.currentSelectedFdd = fdd;
					currentSelectedFddNode = currentLeaf;
				}
			}
		}
			
		this.treeModel.reload();
		
		for(DefaultMutableTreeNode node : this.expandedNodesList) {
			System.out.println(new TreePath(node.getPath()));
			this.fddTree.expandPath(new TreePath(node.getPath()));
		}
		
		if(currentSelectedFddNode != null)
			this.fddTree.setSelectionPath(new TreePath(currentSelectedFddNode.getPath()));
		else {
			this.currentSelectedFdd = null;
			this.centerContainer.setVisible(false);
		}
		
		this.msgList.revalidate();
		this.msgList.repaint();
		
	}
	
	/*Listener du bouton "Ajouter un fil de discussion" qui permet d'écouter un potentiel clic utilisateur sur le bouton et d'effectuer
	* une action en conséquence.
	*/
	private void addButtonFddListener() {
		this.buttonAddFdd.addActionListener(event -> {
			this.addFddFrame = new AddFddFrame(this);
			this.addFddFrame.initialiser();
			this.addFddFrame.setVisible(true);
			this.buttonAddFdd.setEnabled(false);
		});
	}
	
	/*Listener de l'arbre "fddTree" qui permet d'écouter un potentiel clic utilisateur sur l'arbre et d'effectuer
	* une action en conséquence. Cette fonction à été réalisée de manière à ce que ce ne soit que les fdd qui puissent être cliquables
	* (donc les seuls noeuds qui n'ont pas de fils (child) puisqu'un groupe doit ABSOLUMENT avoir un fdd (qui lui a forcément un message)
	* lors de sa création. Autrement dit, un groupe aura toujours au moins 1 fdd et ce fdd aura toujours au moins un message, on joue donc
	* sur cette règle pour effectuer une action.
	* 
	* NB : 
	* - DefaultMutableTreeNode = un noeud
	* - e.getNewLeadSelectionPath().getLastPathComponent() getNewLeadSelectionPath() retourne le chemin du node sélectionné lorsque l'event
	* se trigger, puis getLastPathComponent() retourne le dernier élément de ce chemin sous forme d'Object;
	* 
	* - listModel est le modèle de la liste msgList, on crée la msgList par la suite à l'aide du contenu de listModel contenant les 
	* messages. listModel est utile car c'est un composant JSwing qui implémente une sérialisation, donc qui permet d'utiliser les
	* méthodes d'un objet sérialisable tout en étant un composant.
	*/
	 
	private void addFddTreeListener() {
		
		fddTree.addTreeSelectionListener(e -> {
			
			TreePath selectionPath = e.getNewLeadSelectionPath();
			
			if(selectionPath == null)
				return;
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
			
			if(this.treeModel.getChildCount(node) == 0) {

				if(node.getParent().getParent() == null)
					return;
				
				currentSelectedFdd = (FilDeDiscussion) node.getUserObject();

				String[] tabFdd = new String[2];
				
				if(! currentSelectedFdd.getStatusFdd().equals(StatutEnum.LU_PAR_TOUS)) {
					tabFdd[0] = "idF_Lu";
					tabFdd[1] = String.valueOf(currentSelectedFdd.idF);
					this.client.send(tabFdd);
				}
				
				this.listModel.clear();
				
				for(Message message : currentSelectedFdd.getListeMessages()) {
					listModel.addElement(message);
				}
				
				this.setScrollBarToBottom();
				
				if(! this.centerContainer.isVisible()) {
					this.centerContainer.setVisible(true);
					
					this.msgList.setCellRenderer(new CustomMsgListRenderer((int)((centerContainer.getWidth() - 20) * 0.72)));
					this.msgList.revalidate();
				}
			}
			if(this.treeModel.getChildCount(node) > 0) {
				this.currentSelectedGroupe = (String) node.getUserObject();
				
				if(this.centerContainer.isVisible()) {
					this.centerContainer.setVisible(false);
				}
			}
        });
	}

	/*Listener de la zone de texte en bas de l'interface, qui permet d'ï¿½couter un potentiel appui sur la touche entrï¿½e en provenance de
	 * l'utilisateur et d'effectuer une action en consï¿½quence.
	 * 
	 * NB : 
	 * - texte.trim() permet d'ï¿½viter toute saisie non conforme (que des espaces, un appui sur entrï¿½e sans saisie ou encore de 
	 * supprimer tous les espaces inutiles avant la saisie du premier caractï¿½re != nul && != espace.
	 * 
	 * - currentSelectedFdd parle de lui-mï¿½me, mais c'est peut ï¿½tre ce qu'il faut utiliser pour faire fonctionner le refresh de l'arbre.
	 * (a voir)
	 */
	private void keyboardEnterKeyInputListener() {
		
		this.textEntry.addActionListener(e -> {
			String texte = textEntry.getText();
			texte = texte.trim();
			if(!texte.isEmpty()) {
				
				if(this.currentSelectedGroupe == null) {
					this.currentSelectedGroupe = this.currentSelectedFdd.getNomGroupe();
				}
				
				this.groupTreeMap.get(this.currentSelectedGroupe).remove(this.currentSelectedFdd);
				
				this.messageSaisi = new Message(texte, LocalDateTime.now(), Client.userName, Client.lastAndFirstName);
				
				currentSelectedFdd.addMsg(messageSaisi);
				
				listModel.addElement(messageSaisi);
				
				this.groupTreeMap.get(this.currentSelectedGroupe).add(this.currentSelectedFdd);
				
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.fddTree.getSelectionPath().getLastPathComponent();
				
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
				
				selectedNode.removeFromParent();
				
				parentNode.insert(selectedNode, 0);
				
				TreePath path = new TreePath(parentNode.getChildAt(0));
				
				this.fddTree.setSelectionPath(path);
				
				this.treeModel.reload(parentNode);
				
				NetworkMessage messageToSend = new NetworkMessage(this.messageSaisi.getContenu(), this.messageSaisi.getDate(), this.messageSaisi.getAuteur());
				messageToSend.setNomComplet(messageSaisi.getNomComplet());
				messageToSend.setIdF(this.currentSelectedFdd.idF);
				
				this.client.send(messageToSend);
				this.setScrollBarToBottom();
			}
			textEntry.setText("");
		});
	}
	
	public LinkedHashMap<String, LinkedHashSet<FilDeDiscussion>> getGroupTreeMap(){
		return this.groupTreeMap;
	}
	
	// Ajoute un listener pour chaque tentative de redimension de la fenï¿½tre (voir classe ResizeFrameListener ci-dessous pour plus d'infos)
	private void addFrameListener() {
		this.addComponentListener(new ResizeFrameListener());
	}
	
	/*
	 * Inner-class permettant de crï¿½er un listener dï¿½s lors que l'utilisateur redimensionne la fenï¿½tre, un timer est crï¿½ï¿½ car la fenï¿½tre
	 * se met ï¿½ lag sï¿½vï¿½rement car le programme recalcule la taille de la fenï¿½tre toutes les millisecondes (voire plus)
	 */
	private class ResizeFrameListener extends ComponentAdapter {
		
		private boolean isTimerDone = false;
		
		// Timer pour ralentir le render toutes les 100 ms
		private Timer timer = new Timer(100, e -> {
			isTimerDone = true;
		});
		
		public ResizeFrameListener() {
			timer.start();
		}
		
		@Override
        public void componentResized(ComponentEvent e) {
			
			// Ralentit le render en autorisant un render toutes les 100ms (Car render gourmant en ressources)
			if(this.isTimerDone) {
				
				msgList.setCellRenderer(new CustomMsgListRenderer((int)((centerContainer.getWidth() - 20) * 0.72)));
	            this.isTimerDone = false;
			}
        }
	}
	
	public JButton getButtonAddFdd() {
		return this.buttonAddFdd;
	}
	
	private void addCloseButtonListener() {
		
		JFrame frame = this;
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        frame.dispose();
		        
		        client.disconnectFromServer();
		        System.exit(0);
		    }
		});
	}

	public JTree getFddTree() {
		return this.fddTree;
	}
	
	public DefaultTreeModel getTreeModel() {
		return this.treeModel;
	}
	
	public Client getClient() {
		return this.client;
	}
	
	public AddFddFrame getAddFddFrame() {
		return addFddFrame;
	}
}
