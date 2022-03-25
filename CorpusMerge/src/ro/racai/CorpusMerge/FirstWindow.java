package ro.racai.CorpusMerge;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class FirstWindow extends JFrame implements ActionListener {

	public FirstWindow(String name) {
		super(name);
	}
	
	public void init() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600,300);
		
		JPanel mainPanel=new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		JPanel panel=new JPanel();
		FlowLayout layout=new FlowLayout();
		//layout.setAlignment(FlowLayout.TRAILING);
		panel.setLayout(layout);
			JButton button = new JButton("Open corpus");
			button.addActionListener(this);
			button.setActionCommand("corpus_open");
			panel.add(button);
		mainPanel.add(panel);
		
		panel=new JPanel();
		panel.setLayout(new GridBagLayout());
			JLabel label=new JLabel();
			label.setText("<html>The corpus folder should contain multiple sub-folders with the .ann files of sub-corpora to merge.<br/> The result will be placed in a \"merged\" folder.</html>");
			panel.add(label);
		mainPanel.add(panel);
			
		this.getContentPane().setLayout(new FlowLayout());
		this.getContentPane().add(mainPanel);//,BorderLayout.CENTER);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd=e.getActionCommand();
		if(cmd.contentEquals("corpus_open")) {
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new java.io.File(".")); // start at application current directory
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showSaveDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
			    File folder = fc.getSelectedFile();
			    //OptionPane.showMessageDialog(null, folder.getAbsolutePath(), "InfoBox", JOptionPane.INFORMATION_MESSAGE);
			    this.setVisible(false);
			    MergeWindow mw;
				try {
					mw = new MergeWindow(folder);
				    mw.init();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					throw new RuntimeException(e1);
				}
			}			
		}
	}
	
}
