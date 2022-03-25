package ro.racai.CorpusMerge;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;

import ro.racai.brat.Annotation;

public class EntityDisplay {

	private JLabel label;
	private boolean selected;
	private Annotation ann;
	private JButton button;
	private Font fNotSelected;
	private Font fSelected;
	private Color cSelected;
	private Color cNotSelected;
	
	public JButton getButton() {
		return button;
	}


	public void setButton(JButton button) {
		this.button = button;
	}


	public EntityDisplay() {
		label=new JLabel();
		button=new JButton("SELECT");
		selected=false;
		ann=null;
		fNotSelected=new Font("Courier", Font.PLAIN,16);
		fSelected=new Font("Courier", Font.BOLD,16);
		this.cNotSelected=Color.BLACK;
		this.cSelected=new Color(51,102,0);
		this.reset();
	}
	

	public void reset() {
		label.setText("");
		this.setSelected(false);
		ann=null;
		button.setVisible(false);
	}
	
	public JLabel getLabel() {
		return label;
	}
	public void setLabel(JLabel label) {
		this.label = label;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
		if(selected) {
			label.setFont(fSelected);
			label.setForeground(cSelected);
		}
		else {
			label.setFont(fNotSelected);
			label.setForeground(cNotSelected);
		}
	}
	public Annotation getAnn() {
		return ann;
	}
	public void setAnn(Annotation ann) {
		this.ann = ann;
		this.label.setText(ann.getType()+" > "+ann.getText());
		button.setVisible(true);
		this.setSelected(false);
	}
	
}
