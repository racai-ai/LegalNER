package ro.racai.conllup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ro.racai.base.Sentence;
import ro.racai.base.Token;

public class CONLLUPReader {

	private BufferedReader in;
	private HashMap<String,String> metaHash;
	private String lastLine=null;
	private HashMap<String,Boolean> sentenceMeta;
	private List<String> columns=new ArrayList<String>(20);
	private List<String> metaOrder=new ArrayList<String>(20);
	
	public List<String> getMetaOrder() {
		return metaOrder;
	}

	public List<String> getColumns() {
		return columns;
	}

	public CONLLUPReader(Path fpath,String[] sentenceMeta) throws IOException {
		init(fpath,sentenceMeta);
	}
	
	public CONLLUPReader(Path fpath) throws IOException {
		init(fpath,new String[]{"sent_id","sent-id","text"});
	}
	
	public void close() throws IOException {
		in.close();
	}
	
	private void init(Path fpath,String[] sentenceMeta) throws IOException {
		metaHash=new HashMap<String,String>(10);
		in=new BufferedReader(new InputStreamReader(Files.newInputStream(fpath, StandardOpenOption.READ),Charset.forName("UTF-8")));
		this.sentenceMeta=new HashMap<String,Boolean>(sentenceMeta.length);
		for(String s:sentenceMeta)this.sentenceMeta.put(s, Boolean.TRUE);
		readMetadata();
	}
	
	private void readMetadata() throws IOException {
		for(lastLine=in.readLine();lastLine!=null && lastLine.startsWith("#");lastLine=in.readLine()) {
			String[] data=lastLine.substring(1).trim().split("[=]", 2);
			if(data.length!=2)break;
			
			String key=data[0].trim();
			String value=data[1].trim();
			if(key.length()==0 || this.sentenceMeta.containsKey(key))break;
			
			this.metaHash.put(key, value);
			this.metaOrder.add(key);

			if(key.equalsIgnoreCase("global.columns")) {
				String[] cols=value.split("[ ]");
				for(int i=0;i<cols.length;i++) {
					String c=cols[i].trim();
					if(c.length()>0)columns.add(c);
				}
			}
			
		}
		
	}
	
	public HashMap<String,String> getMetadata() throws IOException{
		return metaHash;
	}
	
	public String getMetaValue(String key) {
		if(metaHash.containsKey(key))return metaHash.get(key);
		return null;
	}
	
	public Sentence readSentence() throws IOException {
		if(lastLine==null)return null;
		
		Sentence s=new Sentence();

		for(;lastLine!=null && lastLine.length()==0;lastLine=in.readLine());
		if(lastLine==null)return null;
		
		for(;lastLine!=null && lastLine.startsWith("#");lastLine=in.readLine()) {
			String[] data=lastLine.substring(1).trim().split("[=]", 2);
			if(data.length!=2)continue;
			
			String key=data[0].trim();
			String value=data[1].trim();
			
			s.addMetaOrder(key);
			
			s.setMetaValue(key, value);
		}
		
		for(;lastLine!=null && lastLine.length()>0 && !lastLine.startsWith("#");lastLine=in.readLine()) {
			String[] data=lastLine.split("[\t]");
			Token t=new Token();
			if(columns.size()>0) {
				for(int i=0;i<data.length && i<columns.size();i++) {
					t.setByKey(columns.get(i), data[i]);
				}
			}else {
				for(int i=0;i<data.length;i++) {
					t.setByKey(""+i, data[i]);
				}
			}
			s.addToken(t);
		}
		
		
		return s;
	}
}
