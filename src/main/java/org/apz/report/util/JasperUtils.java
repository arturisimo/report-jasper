package org.apz.report.util;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

public class JasperUtils {
	
	private static final String SOURCE_EXTENSION = ".jrxml";
	private static final String COMPILED_EXTENSION = ".jasper";
	
	private static Logger logger = LogManager.getLogger(JasperUtils.class);
	
	/**
	 * Metodo que genera el un report con el jasper compilado (por cuestiones de performance)
	 * 
	 * @param nombreJasperCompilado ruta completa del jasper compilado (xxxx.jasper)
	 * @param parameters parametros que recibe el informe
	 * @param nombrePdfSalida nombre que va tener el pdf generado
	 * @throws JRException si habido un error
	 */	
	public static void staticReport(String nombreJasperCompilado, String typeFile, Map<String, Object> parameters, String nombrePdfSalida) throws Exception {
		
		try {
			
			//InputStream reportStream = JasperUtils.class.getResourceAsStream(nombreJasperCompilado);

			File sourcePath = new File("C:\\desarrollo\\wk-mediasearch\\ms-admin\\src\\main\\resources\\reports\\" + nombreJasperCompilado +".jasper");
			String salida = "C:\\tmp\\"+nombrePdfSalida+"."+typeFile;
			logger.info(sourcePath);
			
			JasperReport jr = (JasperReport) JRLoader.loadObject(new FileInputStream(sourcePath));

			JasperPrint jasperPrint = JasperFillManager.fillReport(jr, parameters);

			exportReport(jasperPrint, typeFile, salida);
			
			logger.info("OK");
			
		} catch (JRException e) {
			logger.error(e.getMessage());
			throw new Exception(e);
		}
			
	}
	
	/**
	 * Metodo que genera un report con una lista de objetos 
	 * 
	 * @param nombreJasperCompilado ruta completa del jasper compilado (xxxx.jasper)
	 * @param list los datos necesario par la tabla
	 * @param parameters parametros que recibe el informe
	 * @param nombrePdfSalida nombre que va tener el pdf generado
	 * @throws JRException si habido un error
	 */
	public static void listReport(String nombreJasperCompilado, List<?> list, String typeFile,
			                             Map<String, Object> parameters, String salida) throws Exception {
			
		try {
			
			JRDataSource jRDataSource = new JRBeanCollectionDataSource(list, false);
		
			//InputStream reportStream = JasperUtils.class.getResourceAsStream(nombreJasperCompilado);
			JasperReport jr = (JasperReport) JRLoader.loadObject(new FileInputStream(nombreJasperCompilado));
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(jr, parameters, jRDataSource);
			
			exportReport(jasperPrint, typeFile, salida);
			
		} catch (JRException e) {
			logger.error(e.getMessage());
			throw new Exception(e);
		}
	}
	
	public static void listReportJdbc(Connection conn, String jasper, String typeFile, 
			Map<String, Object> parameters, String salida) throws Exception {
		  
		try {
			
		  JasperPrint jasperPrint = JasperFillManager.fillReport(jasper, parameters, conn);
	      
		  exportReport(jasperPrint, typeFile, salida);
		  
		} catch (JRException e) {
			logger.error(e.getMessage());
			throw new Exception(e);
		}  
	      
	}
	
	
	private static void exportReport(JasperPrint jasperPrint, String typeFile, String salida) throws JRException {
		logger.info("generando : " + salida);
		switch (typeFile) {
			case "pdf":
				JasperExportManager.exportReportToPdfFile(jasperPrint, salida);
				break;
			case "xml":
				JasperExportManager.exportReportToXmlFile(jasperPrint, salida, false);
				break;
			case "xls":
				JRXlsxExporter exporter = new JRXlsxExporter();
			    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(salida));
			    SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
			    configuration.setOnePagePerSheet(true);
			    configuration.setDetectCellType(true);
			    exporter.setConfiguration(configuration);
			    exporter.exportReport();
				break;
			case "doc":
				JRDocxExporter docExporter = new JRDocxExporter();
				docExporter.setExporterInput(new SimpleExporterInput(jasperPrint));      
			    docExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(salida));
			    docExporter.exportReport();
			 	break;
			case "html":
				JasperExportManager.exportReportToHtmlFile(jasperPrint, salida);
				break;	
			default:
				logger.warn(typeFile + " no es un formato valido");
				break;
		}
		logger.info("generando : OK");
	}
	
	public static void compileReportToJasperFile(String sourcePath, String compiledPath) throws Exception {
		
		File srcDirectory = new File(sourcePath);
		File jasperDirectory = new File(compiledPath);
		
		if (srcDirectory.isDirectory() && jasperDirectory.isDirectory()) {
			
			try {
				
				
				List<File> reports = Arrays.asList(srcDirectory.listFiles()).stream()
											.filter(file -> file.getName().contains(SOURCE_EXTENSION))
											.collect(Collectors.toList());
				
				for (File srcFile : reports) {
					final String destFile = compiledPath +  File.separator  + srcFile.getName().replace(SOURCE_EXTENSION, COMPILED_EXTENSION);
					logger.info("generando : " + destFile);
					JasperCompileManager.compileReportToFile(srcFile.getPath(), destFile);
				}
			} catch (JRException e) {
				logger.error(e.getMessage());
				throw new Exception(e);
			}
		} else {
			throw new Exception("No esta bien los directorios");
		}
		
		
	}
}
