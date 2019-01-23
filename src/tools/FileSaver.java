package tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileSaver {
	
	private final File file;
	private final String content;
	
	public FileSaver(String file) throws IOException {
		this.file = new File(file);
		this.content = new String(Files.readAllBytes(this.file.toPath()));
	}

	public File getFile() {
		return file;
	}

	public String getContent() {
		return content;
	}
	
}
