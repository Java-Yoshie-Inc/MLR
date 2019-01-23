package tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileSaver {
	
	private final File file;
	private final byte[] content;
	
	public FileSaver(String file) throws IOException {
		this(new File(file));
	}
	
	public FileSaver(File file) throws IOException {
		this.file = file;
		this.content = Files.readAllBytes(this.file.toPath());
	}
	
	public File getFile() {
		return file;
	}

	public byte[] getContent() {
		return content;
	}
	
}
