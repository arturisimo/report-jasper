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
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

public class JasperUtils {
	
	private static final String SOURCE_EXTENSION = ".jrxml";
	private static final String COMPILED_EXTENSION = ".jasper";
	
	private static Logger logger = LogManager.getLogger(JasperUtils.class);
	
	public enum TypeReport {
		PDF("pdf", "application/pdf"),
		XML("xml", "application/xml"),
		XLS("xls", "application/vnd.ms-excel"),
		RTF("rtf", "application/rtf"),
		DOC("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
		HTML("html", "text/html");
		
		private String ext;
		private String mimeType;
		
		TypeReport(String ext, String mimeType) {
			this.ext = ext;
			this.mimeType = mimeType;
		}

		public String getExt() {
			return ext;
		}
		public String getMimeType() {
			return mimeType;
		}
		public static TypeReport get(String ext) throws Exception {
			return Arrays.asList(TypeReport.values()).stream()
					.filter(tr->tr.getExt().equals(ext)).findFirst().orElseThrow(()-> new Exception("Formato no correcto"));
		}
	}
	/**
	 * Metodo que genera el un report con el jasper compilado (por cuestiones de performance)
	 * 
	 * @param nombreJasperCompilado ruta completa del jasper compilado (xxxx.jasper)
	 * @param parameters parametros que recibe el informe
	 * @param nombrePdfSalida nombre que va tener el pdf generado
	 * @throws JRException si habido un error
	 */	
	public static void staticReport(String jasper, TypeReport typeReport, Map<String, Object> parameters, String output) throws Exception {
		
		try {
			
			JasperReport jr = (JasperReport) JRLoader.loadObject(new FileInputStream(output));

			JasperPrint jasperPrint = JasperFillManager.fillReport(jr, parameters);

			exportReport(jasperPrint, typeReport, output);
			
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
	public static void listReport(String jasper, List<?> list, TypeReport typeReport,
			                             Map<String, Object> parameters, String output) throws Exception {
			
		try {
			
			JRDataSource jRDataSource = new JRBeanCollectionDataSource(list, false);
		
			//InputStream reportStream = JasperUtils.class.getResourceAsStream(nombreJasperCompilado);
			JasperReport jr = (JasperReport) JRLoader.loadObject(new FileInputStream(jasper));
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(jr, parameters, jRDataSource);
			
			exportReport(jasperPrint, typeReport, output);
			
		} catch (JRException e) {
			logger.error(e.getMessage());
			throw new Exception(e);
		}
	}
	
	public static void listReportJdbc(Connection conn, String jasper, TypeReport typeReport, 
			Map<String, Object> parameters, String output) throws Exception {
		  
		try {
			
		  JasperPrint jasperPrint = JasperFillManager.fillReport(jasper, parameters, conn);
	      
		  exportReport(jasperPrint, typeReport, output);
		  
		} catch (JRException e) {
			logger.error(e.getMessage());
			throw new Exception(e);
		}  
	      
	}
	
	
	private static void exportReport(JasperPrint jasperPrint, TypeReport typeReport, String output) throws JRException {
		logger.info("generando : " + output);
		switch (typeReport) {
			case PDF:
				JasperExportManager.exportReportToPdfFile(jasperPrint, output);
				break;
			case XML:
				JasperExportManager.exportReportToXmlFile(jasperPrint, output, false);
				break;
			case XLS:
				JRXlsxExporter exporter = new JRXlsxExporter();
			    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(output));
			    SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
			    configuration.setOnePagePerSheet(false);
			    configuration.setDetectCellType(true);
			    exporter.setConfiguration(configuration);
			    exporter.exportReport();
				break;
			case RTF:
				JRRtfExporter rtfExporter = new JRRtfExporter();
				rtfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				rtfExporter.setExporterOutput(new SimpleWriterExporterOutput(new File(output)));
				rtfExporter.exportReport();
				break;	
			case DOC:
				JRDocxExporter docExporter = new JRDocxExporter();
				docExporter.setExporterInput(new SimpleExporterInput(jasperPrint));      
			    docExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(output));
			    docExporter.exportReport();
			 	break;
			case HTML:
				JasperExportManager.exportReportToHtmlFile(jasperPrint, output);
				break;	
			default:
				logger.warn(typeReport.getExt() + " no es un formato valido");
				break;
		}
		logger.info("generando : "+ output);
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