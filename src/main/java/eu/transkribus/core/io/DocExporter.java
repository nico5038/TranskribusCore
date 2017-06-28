package eu.transkribus.core.io;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.dea.fimgstoreclient.FimgStoreGetClient;
import org.dea.fimgstoreclient.beans.ImgType;
import org.dea.fimgstoreclient.utils.FimgStoreUriBuilder;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.DocumentException;

import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.model.beans.pagecontent.MetadataType;
import eu.transkribus.core.model.beans.pagecontent.TranskribusMetadataType;
import eu.transkribus.core.model.builder.CommonExportPars;
import eu.transkribus.core.model.builder.ExportUtils;
import eu.transkribus.core.model.builder.alto.AltoExporter;
import eu.transkribus.core.model.builder.docx.DocxBuilder;
import eu.transkribus.core.model.builder.mets.TrpMetsBuilder;
import eu.transkribus.core.model.builder.ms.TrpXlsxBuilder;
import eu.transkribus.core.model.builder.ms.TrpXlsxTableBuilder;
import eu.transkribus.core.model.builder.pdf.PdfExporter;
import eu.transkribus.core.model.builder.tei.ATeiBuilder;
import eu.transkribus.core.model.builder.tei.TeiExportPars;
import eu.transkribus.core.model.builder.tei.TrpTeiStringBuilder;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.JaxbUtils;

public class DocExporter extends Observable {
	private static final Logger logger = LoggerFactory.getLogger(DocExporter.class);

	public File writeRawDoc(TrpDoc doc, final String dir, boolean doOverwrite, Set<Integer> pageIndices, 
			boolean exportImg, boolean exportPage, boolean exportAlto, boolean splitIntoWordsInAlto, String fileNamePattern) throws IOException,
			IllegalArgumentException, URISyntaxException, JAXBException, TransformerException {
		CommonExportPars pars = new CommonExportPars();
		pars.setDoWriteMets(true);
		pars.setDoExportPageXml(exportPage);
		pars.setDoExportAltoXml(exportAlto);
		pars.setDoWriteImages(exportImg);
		pars.setUseOcrMasterDir(false);
		pars.setSplitIntoWordsInAltoXml(splitIntoWordsInAlto);
		pars.setPages(CoreUtils.getRangeListStrFromSet(pageIndices));
		pars.setDir(dir);
		pars.setDoOverwrite(doOverwrite);
		
		if(fileNamePattern != null) {
			pars.setFileNamePattern(fileNamePattern);
		}
		
		return exportDoc(doc, pars);
	}
	
	public void writePDF(final TrpDoc doc, final String path, Set<Integer> pageIndices, final boolean addTextPages, final boolean imagesOnly, final boolean highlightTags, final boolean wordBased, final boolean doBlackening, boolean createTitle) throws MalformedURLException, DocumentException, IOException, JAXBException, URISyntaxException, InterruptedException{
		PdfExporter pdfWriter = new PdfExporter();
		pdfWriter.export(doc, path, pageIndices, wordBased, addTextPages, imagesOnly, highlightTags, doBlackening, createTitle);
	}
	
	public void writeTEI(final TrpDoc doc, final String path, CommonExportPars commonPars, final TeiExportPars pars) throws Exception{
		ATeiBuilder builder = new TrpTeiStringBuilder(doc, commonPars, pars, null);
		builder.buildTei();
		builder.writeTeiXml(new File(path));
	}
	
	public void writeDocx(final TrpDoc doc, final String path, Set<Integer> pageIndices, final boolean highlightTags, final boolean wordBased, final boolean doBlackening, final boolean createTitle, boolean doDocxMarkUnclear, boolean doDocxExpandAbbrevs, boolean doDocxSubstituteAbbrevs, boolean doDocxPreserveLineBreaks) throws MalformedURLException, DocumentException, IOException, JAXBException, URISyntaxException, InterruptedException, Docx4JException{
		//last two params are for supplied handling, needs to be added to server esport as well. In the meanwhile args are false by default
		DocxBuilder.writeDocxForDoc(doc, wordBased, highlightTags, doBlackening, new File(path), pageIndices, null, createTitle, doDocxMarkUnclear, doDocxExpandAbbrevs, doDocxSubstituteAbbrevs, doDocxPreserveLineBreaks, false, false);
	}
	
	public void writeTagExcel(final TrpDoc doc, final String path, Set<Integer> pageIndices, boolean wordBased) throws Exception{
		TrpXlsxBuilder.writeXlsxForDoc(doc, wordBased, new File(path), pageIndices, null);
	}
	
	public void writeTableExcel(final TrpDoc doc, final String path, Set<Integer> pageIndices) throws Exception{
		TrpXlsxTableBuilder.writeXlsxForTables(doc, new File(path), pageIndices, null);
	}

	public File exportDoc(TrpDoc doc, CommonExportPars pars) throws IOException, IllegalArgumentException,
			URISyntaxException, JAXBException, TransformerException {
		FimgStoreGetClient getter = null;
		FimgStoreUriBuilder uriBuilder = null;
		ImgType imgType = pars.getRemoteImgQuality() == null ? ImgType.orig : pars.getRemoteImgQuality();
		if (doc.isRemoteDoc()) {
			//FIXME fimagestore path should be read from docMd!
			getter = new FimgStoreGetClient("dbis-thure.uibk.ac.at", "f");
			final String scheme = pars.isUseHttps() ? "https" : "http";
			final int port = pars.isUseHttps() ? 443 : 80;
			uriBuilder = new FimgStoreUriBuilder(scheme, getter.getHost(), port,
					getter.getServerContext());
		}
		
		//create copy of object, as we alter it here while exporting
		TrpDoc doc2;
		doc2 = new TrpDoc(doc);
		
		File outputDir = new File(pars.getDir());
		if (!pars.isDoOverwrite() && outputDir.exists()) {
			throw new IOException("File path already exists.");
		}
		outputDir.mkdir();
		
		//decide where to put the images
		final File imgOutputDir;
		if (pars.isUseOcrMasterDir()) {
			imgOutputDir = new File(outputDir.getAbsolutePath() + File.separatorChar
					+ LocalDocConst.OCR_MASTER_DIR);
			imgOutputDir.mkdir();
		} else {
			imgOutputDir = outputDir;
		}
		
		File pageOutputDir = null, altoOutputDir = null;
		String pageDirName = pars.getPageDirName();
		if (pars.isDoExportPageXml() && !StringUtils.isEmpty(pageDirName)) {
			pageOutputDir = new File(outputDir.getAbsolutePath() + File.separatorChar + pageDirName);
			if (pageOutputDir.mkdir()){
				logger.debug("pageOutputDir created successfully ");
			}
			else{
				logger.debug("pageOutputDir could not be created!");
			}
		} else {
			//if pageDirName is not set, export the PAGE XMLs to imgOutputDir
			pageOutputDir = imgOutputDir;
		}
		
		AltoExporter altoEx = new AltoExporter();
		if (pars.isDoExportAltoXml()){
			altoOutputDir = altoEx.createAltoOuputDir(doc2, outputDir.getAbsolutePath());
		}

		if (doc2.getMd() != null) {
			File fileOut = new File(outputDir.getAbsolutePath() + File.separatorChar
					+ "metadata.xml");
			try {
				JaxbUtils.marshalToFile(doc2.getMd(), fileOut);
			} catch (JAXBException e) {
				throw new IOException("Could not marshal metadata to file.", e);
			}
		}

		List<TrpPage> pages = doc2.getPages();
		
		Set<Integer> pageIndices = pars.getPageIndices(doc.getNPages());
		
		for (int i=0; i<pages.size(); ++i) {
//			for (TrpPage p : pages) {
			if (pageIndices!=null && !pageIndices.contains(i))
				continue;
		
			TrpPage p = pages.get(i);
			File imgFile = null, xmlFile = null, altoFile = null;
			
			URL imgUrl = p.getUrl(); 
			
			final String baseFileName = buildFileName(pars.getFileNamePattern(), p);
			final String imgExt = "." + FilenameUtils.getExtension(p.getImgFileName());
			final String xmlExt = ".xml";
			
			if (doc2.isRemoteDoc()) {				
				if (pars.isDoWriteImages()) {
					final URI imgUri = uriBuilder.getImgUri(p.getKey(), imgType);
					imgFile = getter.saveFile(imgUri, imgOutputDir.getAbsolutePath(), baseFileName + imgExt);
					p.setUrl(imgFile.toURI().toURL());
					p.setKey(null);
				}
				if(pars.isDoExportPageXml()) {
					//old
					//TrpTranscriptMetadata t = p.getCurrentTranscript();
					/*
					 * new: to get the previously stored choosen version
					 */
					TrpTranscriptMetadata transcriptMd;
					JAXBPageTranscript transcript = ExportUtils.getPageTranscriptAtIndex(i);
					
					if(transcript == null) {
						transcriptMd = p.getCurrentTranscript();
						logger.warn("Have to unmarshall transcript in DocExporter for transcript "+transcriptMd+" - should have been built before using ExportUtils::storePageTranscripts4Export!");
						transcript = new JAXBPageTranscript(transcriptMd);
						transcript.build();
					} else {
						transcriptMd = transcript.getMd();
					}
					
					URL xmlUrl = transcriptMd.getUrl();
										
					if (pars.isExportTranscriptMetadata()) {
						MetadataType md = transcript.getPage().getPcGtsType().getMetadata();
						if (md == null) {
							throw new JAXBException("Transcript does not contain a metadata element: "+transcriptMd);
						}
						
						String imgUrlStr = CoreUtils.urlToString(imgUrl);
						String xmlUrlStr = CoreUtils.urlToString(xmlUrl);
						String status = transcriptMd.getStatus() == null ? null : transcriptMd.getStatus().toString();

						TranskribusMetadataType tmd = new TranskribusMetadataType();
						tmd.setDocId(doc.getId());
						tmd.setPageId(p.getPageId());
						tmd.setPageNr(p.getPageNr());
						tmd.setTsid(transcriptMd.getTsId());
						tmd.setStatus(status);
						tmd.setUserId(transcriptMd.getUserId());
						tmd.setImgUrl(imgUrlStr);
						tmd.setXmlUrl(xmlUrlStr);
						tmd.setImageId(p.getImageId());
						md.setTranskribusMetadata(tmd);
					}
					
					xmlFile = new File(FilenameUtils.normalizeNoEndSeparator(pageOutputDir.getAbsolutePath())+File.separator+baseFileName + xmlExt);
					logger.debug("PAGE XMl output file: "+xmlFile.getAbsolutePath());
					transcript.write(xmlFile);

					// old code: save file by just downloading to disk
//					xmlFile = getter.saveFile(transcriptMd.getUrl().toURI(), pageOutputDir.getAbsolutePath(), baseFileName + xmlExt);
					
					// make sure (for other exports) that the transcript that is exported is the only one set in the transcripts list of TrpPage
					p.getTranscripts().clear();
					TrpTranscriptMetadata tCopy = new TrpTranscriptMetadata(transcriptMd, p);
					tCopy.setUrl(xmlFile.toURI().toURL());
					p.getTranscripts().add(tCopy);
				}
			} else {
				if (pars.isDoWriteImages()) {
					imgFile = LocalDocWriter.copyImgFile(p, p.getUrl(), imgOutputDir.getAbsolutePath(), baseFileName + imgExt);
				}
				if(pars.isDoExportPageXml()) {
					xmlFile = LocalDocWriter.copyTranscriptFile(p, pageOutputDir.getAbsolutePath(), baseFileName + xmlExt);
				}
			}
			// export alto:
			if (pars.isDoExportAltoXml()) {
				altoFile = altoEx.exportAltoFile(p, baseFileName + xmlExt, altoOutputDir, pars.isSplitIntoWordsInAltoXml());
			}
			
			if (imgFile != null)
				logger.debug("Written image file " + imgFile.getAbsolutePath());
			
			if (xmlFile != null) {
				logger.debug("Written transcript xml file " + xmlFile.getAbsolutePath());
			} else {
				logger.warn("No transcript was exported for page ");
			}
			if (altoFile != null) {
				logger.debug("Written ALTO xml file " + altoFile.getAbsolutePath());
			} else {
				logger.warn("No alto was exported for page ");
			}
			
			notifyObservers(Integer.valueOf(p.getPageNr()));
			setChanged();
		}
		
		if (pars.isDoWriteMets()) {
			//load the exported doc from its new location
			//FIXME this does not work for export of PAGE XMLs only!
//			final TrpDoc localDoc = LocalDocReader.load(outputDir.getAbsolutePath(), false);
			
			//set local folder or else TrpMetsBuilder will treat this as remote doc!
			doc2.getMd().setLocalFolder(outputDir);
			//write mets with file pointers to local files
			Mets mets = TrpMetsBuilder.buildMets(doc2, pars.isDoExportPageXml(), pars.isDoExportAltoXml(), pars.isDoWriteImages(), pageIndices);
			File metsFile = new File(outputDir.getAbsolutePath() + File.separator
					+ TrpMetsBuilder.METS_FILE_NAME);
	
			try {
				JaxbUtils.marshalToFile(mets, metsFile, TrpDocMetadata.class);
			} catch (JAXBException e) {
				throw new IOException("Could not marshal METS to file!", e);
			}
		}
		
		return outputDir;
	}
	
	

	private String buildFileName(String fileNamePattern, TrpPage p) {
		if(fileNamePattern == null || fileNamePattern.equals("${filename}")) {
			return FilenameUtils.getBaseName(p.getImgFileName());
		} else {
			String fileName = buildFileName(fileNamePattern, p.getImgFileName(), p.getPageId(), 
					p.getDocId(), p.getKey(), p.getPageNr());
			return fileName;
		}
	}

	private static String buildFileName(String fileNamePattern, String imgFileName, int pageId, int docId,
			String key, int pageNr) {
		
		if(!isFileNamePatternValid(fileNamePattern)){
			throw new IllegalArgumentException("Filename pattern is invalid: " + fileNamePattern);
		}

		final String pageNrStr = StringUtils.leftPad(""+pageNr, 4, '0');
		final String docIdStr = StringUtils.leftPad(""+docId, 6, '0');
		
		String fileName = fileNamePattern
		.replaceAll("\\$\\{filename\\}", FilenameUtils.getBaseName(imgFileName))
		.replaceAll("\\$\\{pageId\\}", ""+pageId)
		.replaceAll("\\$\\{docId\\}", ""+docIdStr)
		.replaceAll("\\$\\{filekey\\}", key)
		.replaceAll("\\$\\{pageNr\\}", pageNrStr);
		
		return fileName;
	}

	public static boolean isFileNamePatternValid(final String fnp) {
		//filename must have a unique component with respect to document
		boolean isValid = fnp.contains("${filename}") || fnp.contains("${filekey}") 
				|| fnp.contains("${pageId}") || fnp.contains("${pageNr}");
		if(!isValid){
			return false;
		}
		//remove all valid placeholders
		String fnpRemainder = fnp
				.replaceAll("\\$\\{filename\\}", "")
				.replaceAll("\\$\\{pageId\\}", "")
				.replaceAll("\\$\\{docId\\}", "")
				.replaceAll("\\$\\{filekey\\}", "")
				.replaceAll("\\$\\{pageNr\\}", "");	
		//check for occurence of illegal chars
		final String[] illegalChars = {"\\", "/", ":", "*", "?", "\"", "<", ">", "|", "~", "{", "}"};
		for(String s : illegalChars){
			if(fnpRemainder.contains(s)){
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args){
		final String p = "${filename}_${${pageId}_${pageNr}";
		System.out.println(isFileNamePatternValid(p));
		System.out.println(buildFileName(p, "test.jpg", 123, 456, "AAAAA", 7));
	}
}
