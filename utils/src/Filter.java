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

public class Filter {

	public static Counter cInitial=new Counter();
	public static Counter cRemaining=new Counter();
	public static String[] allowedAnn;
	
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
			if(ann.hasType(allowedAnn))newAnnotations.add(ann);
		}
		
		cRemaining.inc(newAnnotations.size());
		
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
		if(args.length!=2) {
			System.out.println("Filter <path_to_annotated_files> <ALLOWED_ANN>");
			System.out.println("   files must have .ann extension");
			System.out.println("   ALLOWED_ANN is a comma separated list of allowed annotations");
			System.exit(-1);
		}
		
		System.out.println("Processing files in ["+args[0]+"]");
		
		allowedAnn=args[1].split("[,]");
		
		Stream<Path> paths = Files.walk(Paths.get(args[0]),1);
		paths.parallel()
			.filter(Files::isRegularFile)
			.filter(x->(x.toString().endsWith(".ann")))
			.forEach(Filter::processFileNoException);
		paths.close();
		
		System.out.println("Initial annotations: "+cInitial.getC());
		System.out.println("Remaining annotations: "+cRemaining.getC());
	}	
	
}

