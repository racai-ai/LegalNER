package ro.racai.conllup;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.racai.base.Sentence;
import ro.racai.base.Token;

public class CONLLUPWriter {

	private PrintWriter out;
	private HashMap<String,String> metaHash;
	private List<String> columnsOrder;
	private List<String> metaOrder;
	private List<String> sentenceMetaOrder;
	private boolean metadataWritten=false;
	private boolean firstSentence=true;
	
	private HashMap<String,String> remapColumns;
	
	public HashMap<String, String> getRemapColumns() {
		return remapColumns;
	}

	public void setRemapColumns(HashMap<String, String> remap) {
		this.remapColumns = remap;
	}

	public CONLLUPWriter(Path fpath,List<String> metaOrder,List<String> sentenceMetaOrder,List<String> columnsOrder) throws IOException {
		init(fpath,metaOrder,sentenceMetaOrder,columnsOrder);
	}
	
	public void close() throws IOException {
		out.close();
	}
	
	private void init(Path fpath,List<String> metaOrder,List<String> sentenceMetaOrder,List<String> columnsOrder) throws IOException {
		remapColumns=null;
		metaHash=new HashMap<String,String>(10);
		out=new PrintWriter(
				new BufferedWriter(
						new OutputStreamWriter(
								Files.newOutputStream(fpath, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
								,Charset.forName("UTF-8")
						)));
		this.metaOrder=metaOrder;
		this.sentenceMetaOrder=sentenceMetaOrder;
		this.columnsOrder=columnsOrder;
		
		String columns="";
		for(String s:columnsOrder) {
			if(columns.length()>0)columns+=" ";
			columns+=s;
		}
		this.metaHash.put("global.columns", columns);
	}
	
	public void resetMetadataColumns() {
		String columns="";
		for(String s:columnsOrder) {
			if(columns.length()>0)columns+=" ";
			columns+=s;
		}
		this.metaHash.put("global.columns", columns);
	}
	
	
	public HashMap<String,String> getMetadata() throws IOException{
		return metaHash;
	}
	
	public String getMetaValue(String key) {
		if(metaHash.containsKey(key))return metaHash.get(key);
		return null;
	}
	
	public void setMetaValue(String key, String value) {
		metaHash.put(key, value);
	}
	
	public void setMetadata(HashMap<String,String> map) {
		for(Map.Entry<String, String> entry:map.entrySet()) {
			this.setMetaValue(entry.getKey(), entry.getValue());
		}
	}
	
	public void writeMetadata() {
		if(metadataWritten)return ;
		
		for(String key:metaOrder) {
			if(metaHash.containsKey(key)) {
				out.println("# "+key+" = "+metaHash.get(key));
			}
		}
		
		metadataWritten=true;
	}
	
	public void writeSentence(Sentence s) {
		if(!metadataWritten)writeMetadata();
		
		if(!firstSentence)out.println();
		
		for(String key:sentenceMetaOrder) {
			if(s.getMetadata().containsKey(key)) {
				out.println("# "+key+" = "+s.getMetadata().get(key));
			}
		}
		
		StringBuffer line=new StringBuffer(4000);
		for(Token t:s.getTokens()) {
			line.delete(0, line.length());
			for(String key:columnsOrder) {
				if(line.length()>0)line.append("\t");

				String keyGet=key;
				if(remapColumns!=null && remapColumns.containsKey(key)) {
					keyGet=remapColumns.get(key);
				}
				String value=t.getByKey(keyGet);
				
				if(value==null)value="_";
				line.append(value);
			}
			out.println(line.toString());
		}
		
		firstSentence=false;
	}
	
}
