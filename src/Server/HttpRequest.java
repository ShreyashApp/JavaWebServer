package Server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class HttpRequest {
	static final File WEB_ROOT = new File(".");
	static final String DEFAULT_FILE = "index.html";
	static final String FILE_NOT_FOUND = "404.html";
	static final String METHOD_NOT_SUPPORTED = "not_supported.html";
	
	private BufferedReader in;
	
	public HttpRequest(InputStreamReader inr) {
		in = new BufferedReader(inr);
	}
	
	public File RequestedFile() {
		File file = null;
		
		String input = null;
		try {
			input = in.readLine();
			
			if(input == null) {
				throw new Exception("Empty input");
			}
			
			StringTokenizer parse = new StringTokenizer(input);
			String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
			System.out.println(method);
			// we get file requested
			String fileRequested = parse.nextToken().toLowerCase();
			System.out.println(fileRequested);
			switch(method) {
				case "GET":{
					file = GetFile(fileRequested);
					if(!file.exists()) {
						file = new File(WEB_ROOT, FILE_NOT_FOUND);
					}
					break;
				}
				default:{
					file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		
		return file;
	}
	
	private File GetFile(String fileName) {
		File file = null;
		if (fileName.endsWith("/")) {
			fileName += DEFAULT_FILE;
		}
		file = new File(WEB_ROOT, fileName);
		return file;
	}
}
