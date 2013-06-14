package share.photo.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 上传文件的文件bean
 * 
 * @author 黄世凡
 * 
 */
public class FormFileBean {
	// 上传的数据
	private byte[] data;
	// 文件名
	private String fileName;
	// 参数名
	private String parameterName;
	// 文件流
	private InputStream inputStream;
	// 文件
	private File file;
	private String contentType = "application/octet-stream";

	public FormFileBean(String fileName, String parameterName, byte[] data,
			String contentType) {
		this.fileName = fileName;
		this.parameterName = parameterName;
		this.data = data;
		this.contentType = contentType;
	}

	public FormFileBean(String fileName, String parameterName, File file,
			String contentType) {
		this.fileName = fileName;
		this.parameterName = parameterName;
		this.file = file;
		this.contentType = contentType;
		try {
			this.inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
