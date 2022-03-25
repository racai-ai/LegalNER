package ro.racai.CorpusMerge;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ro.racai.brat.Annotation;
import ro.racai.brat.AnnotationReader;
import ro.racai.brat.AnnotationWriter;
import ro.racai.brat.MalformedAnnotationException;

@SuppressWarnings("serial")
public class MergeWindow extends JFrame implements ActionListener {

	List<EntityDisplay> entities=new ArrayList<>(10);
	List<Annotation> currentAnnotations=new ArrayList<>(1000);
	private File corpusFolder;
	private HashMap<String,List<String>> mergeFiles;
	private List<String> mergeFilesList;
	private String lastFile;
	private JLabel labelCurrentFile;
	private AnnotationWriter annOut=null;
	private List<String> currentFileDirs;
	
	public void resetEntities() {
		for(EntityDisplay en:entities) {
			en.reset();
		}
	}
	
	public void createFileList() throws IOException {
		PrintWriter out=new PrintWriter(
				new BufferedWriter(
						new OutputStreamWriter(
								Files.newOutputStream(Paths.get(corpusFolder.getPath(),"merge_list.txt"), 
										StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
								,Charset.forName("UTF-8")
						)));	
		
		String[] directories = corpusFolder.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory() && !name.contentEquals("merged");
		  }
		});		
		
		for(String dir:directories) {
			File dirFile=new File(corpusFolder,dir);
			String[] files = dirFile.list(new FilenameFilter() {
				  @Override
				  public boolean accept(File current, String name) {
				    return !(new File(current, name).isDirectory()) && name.endsWith(".ann");
				  }
				});
			for(String f:files) {
				out.println(dir+"/"+f);
			}
			
		}
		
		out.close();
	}
	
	public void loadLastFile() throws IOException {
		lastFile="";
		File flist=new File(corpusFolder,"merge_last.txt");
		if(flist.exists() && flist.canRead()) {
			lastFile=Files.readAllLines(flist.toPath()).get(0);
		}
		
	}
	
	public void loadFileList() throws IOException {
		File flist=new File(corpusFolder,"merge_list.txt");
		if(!flist.exists() || !flist.canRead())createFileList();
		
		loadLastFile();
		
		mergeFiles=new HashMap<>(1000);
		mergeFilesList=new ArrayList<>(1000);
		HashMap<String,Boolean> processed=new HashMap<>(1000);
		boolean foundLast=false;
		
		BufferedReader in=new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(flist.getAbsolutePath()), StandardOpenOption.READ),Charset.forName("UTF-8")));
		for(String line=in.readLine();line!=null;line=in.readLine()) {
			line=line.trim();
			String[] ldata=line.split("[/]");
			if(ldata.length!=2)continue;
			
			if(!mergeFiles.containsKey(ldata[1])) {
				mergeFiles.put(ldata[1],new ArrayList<String>(5));
			}
			mergeFiles.get(ldata[1]).add(ldata[0]);
			mergeFilesList.add(ldata[1]);
			
			if(lastFile.length()>0 && !foundLast) {
				processed.put(ldata[1],Boolean.TRUE);
				if(lastFile.contentEquals(ldata[1]))foundLast=true;
			}
		}
		in.close();
		
		for(Map.Entry<String, Boolean> en:processed.entrySet()) {
			mergeFiles.remove(en.getKey());
		}
	}
	
	public void loadAnn(Path annPath) {
		try {
			AnnotationReader in=new AnnotationReader(annPath);
			
			while(true) {
				try{
					Annotation ann=in.getNextAnnotation();
					if(ann==null)break;
					currentAnnotations.add(ann);
				}catch(MalformedAnnotationException e) {
					e.printStackTrace();
				}
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getCurrentAnn() throws IOException {
		while(true) {
			if(currentAnnotations.size()==0) {
				getNextFile();
			}
			
			if(currentAnnotations.size()==0) {
				this.setVisible(false);
				JOptionPane.showMessageDialog(null, "All files are merged!", "InfoBox", JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			}
			
			List<Annotation> currentMerge=new ArrayList<>(20);
			
			Annotation ann=currentAnnotations.get(0);
			currentAnnotations.remove(0);
			currentMerge.add(ann);
			
			for(Annotation an:currentAnnotations) {
				if(an.getStart()>=ann.getStart() && an.getStart()<=ann.getEnd() ||
						an.getEnd()>=ann.getStart() && an.getEnd()<=ann.getEnd() ||
						ann.getStart()>=an.getStart() && ann.getStart()<=an.getEnd() ||
						ann.getEnd()>=an.getStart() && ann.getEnd()<=an.getEnd()
					)currentMerge.add(an);
			}
			
			for(Annotation an:currentMerge)currentAnnotations.remove(an);
			
			if(currentMerge.size()==currentFileDirs.size()) {
				boolean same=true;
				for(int i=1;i<currentMerge.size();i++)
					if(!currentMerge.get(i).getText().contentEquals(currentMerge.get(0).getText()) ||
					   !currentMerge.get(i).getType().contentEquals(currentMerge.get(0).getType())
					) {
						same=false;
						break;
					}
				if(same) {
					annOut.write(currentMerge.get(0));
					continue;
				}
			}
			
			if(currentMerge.size()>entities.size())
				JOptionPane.showMessageDialog(null, "Too many entities to merge", "InfoBox", JOptionPane.INFORMATION_MESSAGE);
			
			for(int i=0;i<currentMerge.size();i++) {
				Annotation an=currentMerge.get(i);
				if(an==null)continue;
				entities.get(i).setAnn(an);

				for(int j=i+1;j<currentMerge.size();j++) {
					if(currentMerge.get(j)!=null && 
							currentMerge.get(j).getText().contentEquals(an.getText()) &&
							currentMerge.get(j).getType().contentEquals(an.getType())
					) {
						entities.get(i).setSelected(true);
						currentMerge.set(j,null);
					}
				}
			}
			
			break;
		
		}		
	}
	
	public void saveLastFile() throws IOException {
		File flist=new File(corpusFolder,"merge_last.txt");
		PrintWriter out=new PrintWriter(
				new BufferedWriter(
						new OutputStreamWriter(
								Files.newOutputStream(flist.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
								,Charset.forName("UTF-8")
						)));		
		out.println(lastFile);
		out.close();
	}
	
	public void getNextFile() throws IOException {
		currentAnnotations.clear();
		
		if(lastFile.length()>0)saveLastFile();
		if(annOut!=null) {annOut.close();annOut=null;}

		try{
			File merged=new File(corpusFolder,"merged");
			merged.mkdir();
		}catch(Exception e) {e.printStackTrace();}

		for(String fname:mergeFilesList) {
			if(mergeFiles.containsKey(fname)) {
				
				lastFile=fname;
				
				if(mergeFiles.get(fname).size()==1) {
					try {
					Files.copy(
							Paths.get(corpusFolder.getAbsolutePath(),mergeFiles.get(fname).get(0),fname),
							Paths.get(corpusFolder.getAbsolutePath(),"merged",fname)
					);
					}catch(Exception ex) {;}
					continue;
				}
				
				currentFileDirs=mergeFiles.get(fname);
				for(String dir:currentFileDirs) {
					loadAnn(Paths.get(corpusFolder.getAbsolutePath(),dir,fname));
				}
				
				annOut=new AnnotationWriter(Paths.get(corpusFolder.getAbsolutePath(),"merged",fname));
				
				labelCurrentFile.setText(fname);
				mergeFiles.remove(fname);
				break;
			}
		}
	}
	
	public MergeWindow(File folder) throws IOException {
		super("Merge Corpus");
		this.corpusFolder=folder;
		loadFileList();
	}

	public void init() throws IOException {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000,500);
		
		JPanel mainPanel=new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		labelCurrentFile=new JLabel();
		Font flabel=new Font("Courier", Font.ITALIC,16);
		Color clabel=new Color(0,51,153);
		labelCurrentFile.setFont(flabel);
		labelCurrentFile.setForeground(clabel);
		mainPanel.add(labelCurrentFile);
		
		JPanel panel=null;
		for(int i=0;i<10;i++) {
			EntityDisplay ent=new EntityDisplay();
			panel=new JPanel();
			//BoxLayout layout=new BoxLayout(panel,BoxLayout.X_AXIS);
			FlowLayout layout=new FlowLayout(FlowLayout.LEFT);
			panel.setLayout(layout);
			//panel.setSize(500, 10);
			JButton button=ent.getButton();
			button.addActionListener(this);
			button.setActionCommand("toggle_"+i);
			panel.add(button);
			panel.add(ent.getLabel());
			mainPanel.add(panel);
			entities.add(ent);
		}
		
		panel=new JPanel();
			FlowLayout layout=new FlowLayout(FlowLayout.LEFT);
			panel.setLayout(layout);
			JLabel label=new JLabel("                   ");
			panel.add(label);
			JButton button=new JButton("SAVE");
			button.addActionListener(this);
			button.setActionCommand("save");
			panel.add(button);
		mainPanel.add(panel);
			
		this.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));
		this.getContentPane().add(mainPanel);//,BorderLayout.CENTER);
		
        this.setVisible(true);
        this.setLocationRelativeTo(null);
		
        getNextFile();
        getCurrentAnn();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd=e.getActionCommand();
		if(cmd.startsWith("toggle_")) {
			int n=Integer.parseInt(cmd.substring(7));
			entities.get(n).setSelected(!entities.get(n).isSelected());
		}else if(cmd.contentEquals("save")) {
			for(EntityDisplay en:entities) {
				if(en.isSelected())this.annOut.write(en.getAnn());
			}
			this.resetEntities();
			try {
				this.getCurrentAnn();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
}
