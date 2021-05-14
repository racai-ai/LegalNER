import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import ro.racai.base.Sentence;
import ro.racai.base.Token;
import ro.racai.brat.Annotation;
import ro.racai.brat.AnnotationReader;
import ro.racai.brat.AnnotationWriter;
import ro.racai.brat.MalformedAnnotationException;
import ro.racai.conllup.CONLLUPReader;
import ro.racai.conllup.CONLLUPWriter;
import ro.racai.utils.Counter;

public class CONLLUPFilterGeonames {

	public static Counter cInitial=new Counter();
	public static Counter cRemaining=new Counter();
	
	public static void processFile(Path fpathIn) throws IOException, MalformedAnnotationException {
		CONLLUPReader in=new CONLLUPReader(fpathIn);
		Sentence firstSentence=in.readSentence();
		
		Path fpathOut=Paths.get("tmp",fpathIn.getFileName().toString());
		CONLLUPWriter out=new CONLLUPWriter(fpathOut,in.getMetaOrder(),firstSentence.getMetaOrder(),in.getColumns());
		out.setMetadata(in.getMetadata());
		
		for(Sentence sent=firstSentence;sent!=null;sent=in.readSentence()) {
			for(Token tok:sent.getTokens()) {
				if(!tok.getByKey("RELATE:GEONAMES").equals("_")) {
					cInitial.inc();

					if(!tok.getByKey("RELATE:NE").equals("B-LOC") && !tok.getByKey("RELATE:NE").equals("I-LOC")) {
						tok.setByKey("RELATE:GEONAMES", "_");
					}else cRemaining.inc();
				}
			}
			out.writeSentence(sent);
		}
		
		in.close();
		out.close();
		
		fpathIn.toFile().delete();
		fpathOut.toFile().renameTo(fpathIn.toFile());

	}
	
	public static void processFileNoException(Path fpathIn) {
		try {
			processFile(fpathIn);
		}catch(Exception ex) {
			System.out.println(String.format("[%s] %s", fpathIn.toString(),ex.getMessage()));
			ex.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		if(args.length!=1) {
			System.out.println("Filter <path_to_conllup_files>");
			System.out.println("   files must have .conllup extension");
			System.exit(-1);
		}
		
		System.out.println("Processing files in ["+args[0]+"]");
		
		Files.createDirectory(Paths.get("tmp"));
		
		Stream<Path> paths = Files.walk(Paths.get(args[0]),1);
		paths.parallel()
			.filter(Files::isRegularFile)
			.filter(x->(x.toString().endsWith(".conllup")))
			.forEach(CONLLUPFilterGeonames::processFileNoException);
		paths.close();
		
		System.out.println("Initial GEONAMES annotations: "+cInitial.getC());
		System.out.println("Remaining GEONAMES annotations: "+cRemaining.getC());
	}	
	
}

