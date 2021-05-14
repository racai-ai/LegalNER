import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import ro.racai.brat.Annotation;
import ro.racai.brat.AnnotationReader;
import ro.racai.brat.AnnotationWriter;
import ro.racai.brat.MalformedAnnotationException;
import ro.racai.utils.Counter;

public class ANNRenameAnnotation {

	public static Counter cInitial=new Counter();
	public static Counter cChanged=new Counter();
	public static HashMap<String,String> annMap;
	
	public static void processFile(Path fpathIn) throws IOException, MalformedAnnotationException {
		AnnotationReader in = new AnnotationReader(fpathIn);
		List<Annotation> annotations=new ArrayList<>(100);
		for(Annotation ann=in.getNextAnnotation(); ann!=null; ann=in.getNextAnnotation()) {
			annotations.add(ann);
		}
		in.close();
		
		cInitial.inc(annotations.size());
		
		List<Annotation> newAnnotations=new ArrayList<>(annotations.size());
		for(Annotation ann:annotations) {
			if(annMap.containsKey(ann.getType())) {
				ann.setType(annMap.get(ann.getType()));
				cChanged.inc();
			}
			newAnnotations.add(ann);
		}
		
		AnnotationWriter out=new AnnotationWriter(fpathIn);
		out.writeAll(newAnnotations);
		out.close();
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
		if(args.length!=3) {
			System.out.println("Filter <path_to_annotated_files> <ANN_ORIG> <ANN_NEW>");
			System.out.println("   files must have .ann extension");
			System.out.println("   ANN_ORIG is a comma separated list of original annotations");
			System.out.println("   ANN_NEW is a comma separated list of new annotations");
			System.exit(-1);
		}
				
		String[] annOrig=args[1].split("[,]");
		String[] annNew=args[2].split("[,]");
		
		if(annOrig.length!=annNew.length) {
			System.out.println("Original annotations and NEW annotations must have the same size!");
			System.exit(-1);
		}
		
		annMap=new HashMap<>(annOrig.length);
		for(int i=0;i<annOrig.length;i++) {
			annMap.put(annOrig[i],annNew[i]);
		}
		
		System.out.println("Processing files in ["+args[0]+"]");
		
		Stream<Path> paths = Files.walk(Paths.get(args[0]),1);
		paths.parallel()
			.filter(Files::isRegularFile)
			.filter(x->(x.toString().endsWith(".ann")))
			.forEach(ANNRenameAnnotation::processFileNoException);
		paths.close();
		
		System.out.println("Total annotations: "+cInitial.getC());
		System.out.println("Changed annotations: "+cChanged.getC());
	}	
	
}

