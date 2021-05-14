package ro.racai.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Sentence {

	private HashMap<String,String> metadata=new HashMap<String,String>(10);
	private List<String> metaOrder=new ArrayList<>(10);
	public List<String> getMetaOrder() {
		return metaOrder;
	}

	private List<Token>tokens=new ArrayList<Token>(100);
	
	public Sentence() {
		
	}
	
	public void addToken(Token t) {
		tokens.add(t);
	}
	
	public void addMetaOrder(String metaKey) {
		this.metaOrder.add(metaKey);
	}
	
	public List<Token> getTokens(){
		return tokens;
	}
	
	public int getNumTokens() {
		return tokens.size();
	}
	
	public Token getToken(int index) {
		return tokens.get(index);
	}
	
	public void setMetaValue(String key,String value) {
		metadata.put(key, value);
	}
	
	public HashMap<String,String> getMetadata(){
		return metadata;
	}
}
