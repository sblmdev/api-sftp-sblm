import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import pe.gob.sblm.api.sftp.client.SftpClient;


public class Main {

	public static void main(String[] args) {
		
		SftpClient sftpClient= new SftpClient("127.0.0.1","desarrollo", "d3s4rr0ll0$", "22");
		sftpClient.iniciarConexion();
		InputStream stream = null;
		try {
			stream = new FileInputStream("C:/1.pdf");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			sftpClient.createFolferIfNotExist("Test", "2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		try {
//			sftpClient.upload(stream, "12344", "Test");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		InputStream in = null;
		
//		try {
//			
//			
//		in=sftpClient.download("12344", null);
//		
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		 try {
//			 File file=new File("D:/ara.pdf");
//		        OutputStream out = new FileOutputStream(file);
//		        byte[] buf = new byte[1024];
//		        int len;
//		        while((len=in.read(buf))>0){
//		            out.write(buf,0,len);
//		        }
//		        out.close();
//		        in.close();
//		    } catch (Exception e) {
//		        e.printStackTrace();
//		    }

		sftpClient.cerrarConexion();
		}
	
	
	
}
