package net.xinshi.picenedemo.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FileUploadController {
		
	@RequestMapping("/upload")
	@ResponseBody
	public String upload(HttpServletRequest request) {
		System.out.println("hello upload!");
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		
		System.out.println(isMultipart);
		
		if(!isMultipart) {
			return "error";
		}
		
		try {
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload();

			// Parse the request
			FileItemIterator iter = upload.getItemIterator(request);
			while (iter.hasNext()) {
			    FileItemStream item = iter.next();
			    String name = item.getFieldName();
			    InputStream stream = item.openStream();
			    if (item.isFormField()) {
			        System.out.println("Form field " + name + " with value "
			            + Streams.asString(stream) + " detected.");
			    } else {
			        System.out.println("File field " + name + " with file name "
			            + item.getName() + " detected.");
			        // Process the input stream
			        String saveDirectory = "/Users/benzhao/work/projects/picenedemo/src/main/webapp/uploads/";
			        File dir = new File(saveDirectory);
			        if(!dir.isDirectory()) {
			        	dir.mkdirs();
			        }
			        String save = saveDirectory + item.getName();
			        FileOutputStream out = new FileOutputStream(save);
			        byte[] buffer = new byte[1024];
			        while(stream.read(buffer) != -1) {
			        	out.write(buffer);
			        }
			        out.close();
			        stream.close();
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "ok";
	}
}
