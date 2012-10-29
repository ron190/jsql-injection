package mvc.view.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

public class TabHeader extends JPanel implements ActionListener, MouseListener {
	private static final long serialVersionUID = 8127944685828300647L;
	
	private JTabbedPane valuesTabbedPane;
	
	public TabHeader(JTabbedPane newJTabbedPane){
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        
        this.valuesTabbedPane = newJTabbedPane;
                
        this.setOpaque(false);
        
        this.addMouseListener(new MouseAdapter(){
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		Component component = e.getComponent();
        		if (component instanceof TabHeader) {
        			if(e.getButton() == MouseEvent.BUTTON2){
        				int closeTabNumber = valuesTabbedPane.indexOfTabComponent(TabHeader.this);
        				valuesTabbedPane.removeTabAt(closeTabNumber);
        			}
        		}
        	}
        	@Override
        	public void mousePressed(MouseEvent e) {
        		Component component = e.getComponent();
        		if (component instanceof TabHeader) {
        			if(e.getButton() == MouseEvent.BUTTON1){
        				int selectTabNumber = valuesTabbedPane.indexOfTabComponent(TabHeader.this);
        				valuesTabbedPane.setSelectedIndex(selectTabNumber);
        			}
        		}
        	}
        });
        
		JLabel tabTitleLabel = new JLabel(){
			private static final long serialVersionUID = -3224791474462317469L;

			public String getText() {
                int i = valuesTabbedPane.indexOfTabComponent(TabHeader.this);
                if (i != -1) {
                    return valuesTabbedPane.getTitleAt(i);
                }
                return null;
            }
        };
		
		tabTitleLabel.setFont( new Font(tabTitleLabel.getFont().getName(),Font.PLAIN,tabTitleLabel.getFont().getSize()) );
		
		this.add(tabTitleLabel);
		
		Icon closeIcon = new ImageIcon(this.getClass().getResource("/images/gtk_close.png"));
		Dimension closeButtonSize = new Dimension(closeIcon.getIconWidth(), closeIcon.getIconHeight());
	    
		JButton tabCloseButton = new JButton(closeIcon);
	    tabCloseButton.setPreferredSize(closeButtonSize);
		tabCloseButton.setContentAreaFilled(false);
		tabCloseButton.setFocusable(false);
		tabCloseButton.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Tree.textBackground")));
		tabCloseButton.setBorderPainted(false);
		tabCloseButton.setRolloverEnabled(true);
	    
		tabCloseButton.addActionListener(this);
		tabCloseButton.addMouseListener(this);
		
		this.add(tabCloseButton);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int closeTabNumber = valuesTabbedPane.indexOfTabComponent(TabHeader.this);
		valuesTabbedPane.removeTabAt(closeTabNumber);
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
        Component component = e.getComponent();
        if (component instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) component;
            button.setBorderPainted(true);
        }
    }

	@Override
    public void mouseExited(MouseEvent e) {
        Component component = e.getComponent();
        if (component instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) component;
            button.setBorderPainted(false);
        }
    }

	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
}