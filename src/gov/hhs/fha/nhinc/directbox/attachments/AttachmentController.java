package gov.hhs.fha.nhinc.directbox.attachments;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Comparator;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AttachmentController {

	private static ConfigurableMimeFileTypeMap mimeTypeMap = null;
	
	/** Attachment storage directory, include trailing slash. */
	public static final String ATTACH_STORE_DIR = "/home/tomcat/attachments/mbox_store/";
//	public static final String ATTACH_STORE_DIR = "F://dev//attach_store//";
	
	@RequestMapping(method = RequestMethod.GET, value = "/get/{messageId}/{attachNum}")
	public ResponseEntity<byte[]> getAttachment(@PathVariable("messageId") String messageId,
			@PathVariable("attachNum") String attachNum) throws Exception {

		HttpHeaders headers = new HttpHeaders();
		int fileNum = 0;
		byte[] bites = new byte[0];

		try {
			File dir = new File(ATTACH_STORE_DIR + messageId);
			if (!dir.isDirectory()) {
				System.out.println("Directory not found!!!");
				throw new Exception("Directory not found.");
			}
			if (!dir.canRead()) {
				System.out.println("Directory not readable!!!");
				throw new Exception("Directory not readable.");
			}
			File[] fileList = dir.listFiles();
			Arrays.sort(fileList, new MyFileComparator());
			
			fileNum = Integer.valueOf(attachNum);
			headers.set("Content-Type", getContentType(fileList[fileNum]));
			headers.set("Content-Disposition", "attachment; filename=\"" + fileList[fileNum].getName() + "\"");
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(fileList[fileNum]));
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			for (int c = bis.read(); c != -1; c = bis.read()) {
				buffer.write(c);
			}
			bites = buffer.toByteArray();
		} catch (Exception e) {
			System.out.println("Error finding attachment for messageId: " + messageId + ", number: " + attachNum);
			e.printStackTrace();
			throw e;
		}

		return new ResponseEntity<byte[]>(bites, headers, HttpStatus.OK);
	}

	private String getContentType(File file) {

		if (mimeTypeMap == null) {
			Resource resource = new ClassPathResource("mime.types", getClass());
			mimeTypeMap = new ConfigurableMimeFileTypeMap();
			mimeTypeMap.setMappingLocation(resource);
			mimeTypeMap.afterPropertiesSet();
		}

		return mimeTypeMap.getContentType(file);
	}

	class MyFileComparator implements Comparator<File> {

		@Override
		public int compare(File f1, File f2) {
			return f1.getPath().compareToIgnoreCase(f2.getPath());
		}
		
	}
}
