package pe.gob.sblm.api.sftp.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import pe.gob.sblm.api.commons.constants.sgi.Constantes;
import pe.gob.sblm.api.commons.utility.DataFileUtil;
import pe.gob.sblm.api.commons.utility.FechaUtil;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;

public class SftpClient  {
	
	private static Logger logger = Logger.getLogger(SftpClient.class.getName());
	
	private String ip;
	private String user;
	private String password;
	private String port;
	
	Session session=null;
	Channel channel=null;
	ChannelSftp c=null;
	

	
	public SftpClient(String ip, String user, String password, String port) {
		super();
		this.ip = ip;
		this.user = user;
		this.password = password;
		this.port = port;
	}


	public void iniciarConexion(){
		
		logger.info("***   Creating FTP session.   ***");

		JSch jsch = new JSch();

		if (null == session) {
			try {
				session = jsch.getSession(user,ip,Integer.valueOf(port));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSchException e) {
				e.printStackTrace();
			}

			logger.info("***   FTP Session created.   ***");
			session.setPassword(password);

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			try {
				session.connect();
			} catch (JSchException e) {
				e.printStackTrace();
			}
		}

		logger.info("***   Session connected.   ***");
		logger.info("***   Opening FTP Channel.   ***");
		
		try {
			channel = session.openChannel("sftp");
		} catch (JSchException e) {
			e.printStackTrace();
		}
		try {
			channel.connect();
		} catch (JSchException e) {
			e.printStackTrace();
		}
		
		c = (ChannelSftp) channel;
	}
	
	
	public void cerrarConexion(){
		try {
			if (session != null){
				session.disconnect();
				logger.info("***   close session FTP server. ");
			}
			if (channel != null){
				channel.disconnect();
				logger.info("***   close channel FTP server. ");
			}
			if (c != null){
				c.disconnect();
				logger.info("***   close FTP server. ");
			}
		} catch (Exception exc) {
			logger.info("***   Unable to disconnect from FTP server. "
					+ exc.toString() + "   ***");
		}

		logger.info("***   SFTP Process Complete.   ***");
		
	}
	

	public void upload(InputStream stream, String nombre, String path,String extension)
			throws Exception {

		try {

			
			c.cd(path);

			try {
				logger.info("***   Storing file as remote filename: " + nombre + "   ***");

				c.put(stream, nombre.concat(".").concat(extension));
			} catch (Exception e) {
				logger.info("***   Storing remote file failed. " + e.toString() + "   ***");
				throw e;
			}

		} catch (Exception e) {
			logger.info("***   Unable to connect to FTP server. " + e.toString() + "   ***");
			throw e;
		} 
	}
	public byte[] download(String nombreArchivo, Date fcreacion)
			throws Exception {
		

		StringBuilder location = new StringBuilder();
		location.append(Constantes.APLICATIVO_SGI);
		location.append("/");
		location.append(FechaUtil.getYear(fcreacion));
		location.append("/");
		location.append(FechaUtil.getMonth(fcreacion));
		location.append("/");
		location.append(FechaUtil.getDay(fcreacion));

		c.cd(location.toString());
		
		logger.info("***   Changing to FTP remote dir: "+c.pwd() + "   ***");
		
		InputStream is = null;
		try {
			
			System.out.println(nombreArchivo);
			is = c.get(nombreArchivo);

			return DataFileUtil.getByteArrayOutputStream(is).toByteArray();
		} catch (Exception ex) {
			throw ex;
		}
	}
	public byte[] download(String nombreArchivo, Date fcreacion,String ruta)
			throws Exception {
		

		StringBuilder location = new StringBuilder();
		//location.append(Constantes.APLICATIVO_SGI);
		location.append(ruta!=null? (ruta.equals(Constantes.DIR_SGI_RECIBO_CAJA)?ruta:Constantes.APLICATIVO_SGI):Constantes.APLICATIVO_SGI);
		location.append("/");
		location.append(FechaUtil.getYear(fcreacion));
		location.append("/");
		location.append(FechaUtil.getMonth(fcreacion));
		location.append("/");
		location.append(FechaUtil.getDay(fcreacion));

		c.cd(location.toString());
		
		logger.info("***   Changing to FTP remote dir: "+c.pwd() + "   ***");
		
		InputStream is = null;
		try {
			
			System.out.println(nombreArchivo);
			is = c.get(nombreArchivo);

			return DataFileUtil.getByteArrayOutputStream(is).toByteArray();
		} catch (Exception ex) {
			throw ex;
		}
	}
	
	public byte[] download1(String num_certi, Date fexpedicion)throws Exception {
		
		byte[] resultado = null;

//		StringBuilder location = new StringBuilder();
//		location.append(PropiedadesSFTP.getString("sftpRemoteDirectory"));
//		location.append("/");
//		location.append(FechaUtil.getYear(fexpedicion));
//		location.append("/");
//		location.append(FechaUtil.getMonth(fexpedicion));
//		location.append("/");
//		location.append(FechaUtil.getDay(fexpedicion));

//		c.cd(location.toString());
		
		logger.info("***   Changing to FTP remote dir: "+c.pwd() + "   ***");
		
		InputStream is = null;
		try {
			is = c.get(num_certi.concat(".pdf"));
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
		    int nRead;
		    while ((nRead = is.read(data, 0, data.length)) != -1) {
		        buffer.write(data, 0, nRead);
		    }
		    buffer.flush();
		    resultado = buffer.toByteArray();
			
		} catch (Exception e) {
			logger.info("Error: download file to FTP server. " + e.toString() + "   ***");
			throw e;
		} finally{
			if (session != null)
				session.disconnect();

			if (channel != null)
				channel.disconnect();

			if (c != null)
				c.disconnect();

			logger.info("Disconnect completado");
		}
		return resultado;
	}

	public String createDirectory(Date fcreacion,String path) throws Exception {
		String pathBase = path;
		String newFolder = "";

		newFolder = FechaUtil.getYear(fcreacion);
		try {
			createFolferIfNotExist(pathBase, newFolder);
			pathBase = pathBase + "/" + newFolder;
			newFolder = FechaUtil.getMonth(fcreacion);
			createFolferIfNotExist(pathBase, newFolder);

			pathBase = pathBase + "/" + newFolder;
			newFolder = FechaUtil.getDay(fcreacion);
			createFolferIfNotExist(pathBase, newFolder);
			pathBase = pathBase + "/" + newFolder;

		} catch (Exception e) {
			
			throw new Exception(e.getMessage(), e);
		}
		
		System.out.println(pathBase);

		return pathBase.toString();

	}

	public void createFolferIfNotExist(String path, String newNameFolder)
			throws Exception {
		
		String fullPath;

		if ("/".equals(path)) {
			fullPath = path + newNameFolder;

		} else {
			fullPath = path + "/" + newNameFolder;
		}

		SftpATTRS attrs = null;
		try {
			attrs = c.stat(fullPath);
		} catch (Exception e) {
			logger.info(fullPath + " not found");
		}

		if (attrs != null) {
			logger.info("Directory exists IsDir=" + attrs.isDir());
		} else {
			logger.info("Creating dir " + fullPath);
			c.mkdir(fullPath);
		}
	}
	public byte[] visualizar(String nombreArchivo, Date fcreacion)
			throws Exception {
		

		StringBuilder location = new StringBuilder();
		location.append(Constantes.APLICATIVO_SGI);
		location.append("/");
		location.append(FechaUtil.getYear(fcreacion));
		location.append("/");
		location.append(FechaUtil.getMonth(fcreacion));
		location.append("/");
		location.append(FechaUtil.getDay(fcreacion));

		c.cd(location.toString());
		
		logger.info("***   Changing to FTP remote dir: "+c.pwd() + "   ***");
		
		InputStream is = null;
		try {
			
			System.out.println(nombreArchivo);
			is = c.get(nombreArchivo);

			return DataFileUtil.getByteArrayOutputStream(is).toByteArray();
		} catch (Exception ex) {
			throw ex;
		}
	}
	public byte[] visualizar(String nombreArchivo, Date fcreacion,String ruta)
			throws Exception {
		

		StringBuilder location = new StringBuilder();
		location.append(ruta!=null? (ruta.equals(Constantes.DIR_SGI_RECIBO_CAJA)?ruta:Constantes.APLICATIVO_SGI):Constantes.APLICATIVO_SGI);
		location.append("/");
		location.append(FechaUtil.getYear(fcreacion));
		location.append("/");
		location.append(FechaUtil.getMonth(fcreacion));
		location.append("/");
		location.append(FechaUtil.getDay(fcreacion));

		c.cd(location.toString());
		
		logger.info("***   Changing to FTP remote dir: "+c.pwd() + "   ***");
		
		InputStream is = null;
		try {
			
			System.out.println(nombreArchivo);
			is = c.get(nombreArchivo);

			return DataFileUtil.getByteArrayOutputStream(is).toByteArray();
		} catch (Exception ex) {
			throw ex;
		}
	}
}
