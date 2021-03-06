package eu.transkribus.core.model.builder.mets;
//package org.dea.transcript.trp.core.model.builder.mets;
//
//import java.io.File;
//import java.io.IOException;
//import java.math.BigInteger;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Observable;
//
//import javax.xml.datatype.XMLGregorianCalendar;
//
//import org.dea.fimgstoreclient.FimgStoreGetClient;
//import org.dea.fimgstoreclient.beans.FimgStoreFileMd;
//import org.dea.transcript.trp.core.io.LocalDocConst;
//import org.dea.transcript.trp.core.model.beans.ITrpFile;
//import org.dea.transcript.trp.core.model.beans.TrpDoc;
//import org.dea.transcript.trp.core.model.beans.TrpDocMetadata;
//import org.dea.transcript.trp.core.model.beans.TrpPage;
//import org.dea.transcript.trp.core.model.beans.TrpTranscriptMetadata;
//import org.dea.transcript.trp.core.model.beans.enums.EditStatus;
//import org.dea.transcript.trp.core.model.beans.mets.AmdSecType;
//import org.dea.transcript.trp.core.model.beans.mets.AreaType;
//import org.dea.transcript.trp.core.model.beans.mets.DivType;
//import org.dea.transcript.trp.core.model.beans.mets.DivType.Fptr;
//import org.dea.transcript.trp.core.model.beans.mets.FileGrpType;
//import org.dea.transcript.trp.core.model.beans.mets.FileType;
//import org.dea.transcript.trp.core.model.beans.mets.FileType.FLocat;
//import org.dea.transcript.trp.core.model.beans.mets.MdSecType;
//import org.dea.transcript.trp.core.model.beans.mets.MdSecType.MdWrap;
//import org.dea.transcript.trp.core.model.beans.mets.MdSecType.MdWrap.XmlData;
//import org.dea.transcript.trp.core.model.beans.mets.Mets;
//import org.dea.transcript.trp.core.model.beans.mets.MetsType.FileSec;
//import org.dea.transcript.trp.core.model.beans.mets.MetsType.FileSec.FileGrp;
//import org.dea.transcript.trp.core.model.beans.mets.MetsType.MetsHdr;
//import org.dea.transcript.trp.core.model.beans.mets.StructMapType;
//import org.dea.transcript.trp.core.util.JaxbUtils;
//import org.dea.util.file.ChecksumUtils;
//import org.dea.util.file.DeaFileUtils;
//import org.dea.util.file.MimeTypes;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class EnmapMetsBuilder extends Observable {
//	private static final Logger logger = LoggerFactory.getLogger(EnmapMetsBuilder.class);
//	public final static String METS_FILE_NAME = "mets.xml";
//	//TODO check the xsd file that is used to build 
//	public final static String ENMAP_METS_PROFILE = "ENMAP";
//	
////	public static final String TRP_DOC_MD_TYPE_CONST = "TRP_DOC_MD";
////	public static final String SOURCE_MD_ID_CONST = "SOURCE";
////	public static final String SOURCE_DOC_MD_ID_CONST = "MD_ORIG";
//	public static final String IMAGE_FILE_GRP_ID = "ImageGroup";
//	public static final String OCR_MASTER_FILE_GRP_ID = "OCRMasterFiles";
//	
//	public static final String IMG_GROUP_ID = "IMG";
//	public static final String PAGE_GROUP_ID = "PAGEXML";
//	public static final String ALTO_GROUP_ID = "ALTO";
//	public static final String TRP_STRUCTMAP_ID = "TRP_STRUCTMAP";
//	public static final String TRP_DOC_DIV_ID = "TRP_DOC_DIV";
//	
//	/**
//	 *NOT IMPLEMENTED
//	 */
//	public static Mets buildMets(TrpDoc doc, boolean exportPage, boolean exportAlto) throws IOException {
////		Mets mets = new Mets();
////		TrpDocMetadata md = doc.getMd();
////		File localFolder = md.getLocalFolder();
////		boolean isLocalDoc = localFolder != null;
////		
////		mets.setLABEL(md.getTitle());
////		mets.setOBJID(""+md.getDocId());
////		mets.setPROFILE(TRP_METS_PROFILE);
////		//FIXME remove TYPE
////		mets.setTYPE(TRP_METS_PROFILE);
////		
////		//metsHdr
////		MetsHdr hdr = buildMetsHdr(md);
////		mets.setMetsHdr(hdr);
////		//TODO dcmd_elec omitted meanwhile
////		//md_orig
////		AmdSecType amdSec = new AmdSecType();
////		amdSec.setID(SOURCE_MD_ID_CONST);
////		
////		MdSecType sourceMdSec = buildSourceMdSec(md);
////		
////		amdSec.getSourceMD().add(sourceMdSec);
////		mets.getAmdSec().add(amdSec);
////		
////		//structmap div, linking to the sourceMd section with dmd
////		DivType div = new DivType();
////		div.getADMID().add(sourceMdSec);
////		div.setID(TRP_DOC_DIV_ID);
////		
////		FileSec fileSec = new FileSec();
////		StructMapType structMap = new StructMapType();
////		structMap.setID(TRP_STRUCTMAP_ID);
////		structMap.setTYPE("MANUSCRIPT");
////		structMap.setDiv(div);
////		
////		List<TrpPage> pages = doc.getPages();
////		FimgStoreGetClient client = null;
////		
////		if(!isLocalDoc){
////			//TODO maybe we need this stuff in the docMetadata?
////			URL url = pages.get(0).getUrl();
////			client = new FimgStoreGetClient(url);
////		}
////		
////		FileGrp masterGrp = new FileGrp();
////		masterGrp.setID(MASTER_FILE_GRP_ID);
////		
////		FileGrpType imgGrp = new FileGrpType();
////		imgGrp.setID(IMG_GROUP_ID);
////		
////		FileGrpType pageGrp = new FileGrpType();
////		pageGrp.setID(PAGE_GROUP_ID);
////		
////		FileGrpType altoGrp = new FileGrpType();
////		altoGrp.setID(ALTO_GROUP_ID);
////		
////		for(TrpPage p : pages){
////			
////			//build a page div for the structmap
////			DivType pageDiv = new DivType();
////			pageDiv.setID("PAGE_" + p.getPageNr());
////			pageDiv.setTYPE("SINGLE_PAGE");
////			pageDiv.setORDER(BigInteger.valueOf(p.getPageNr()));
////			final String imgId = "IMG_" + p.getPageNr();
////			final String xmlId = PAGE_GROUP_ID+"_" + p.getPageNr();
////			final String altoId = ALTO_GROUP_ID+"_" + p.getPageNr();
////			
////			// do filesection stuff
////			/* only the most recent transcript is added here for now
////			 * 
////			 * TODO how to deal with imagestore files? use orig image? right now, it's just the view file...
////			 * TODO thumbnails not yet included
////			*/
////			FileType img = buildFileType(localFolder, imgId, p, client);
////			imgGrp.getFile().add(img);
////	
////			//linking images
////			Fptr imgPtr = buildFptr(img);			
////			pageDiv.getFptr().add(imgPtr);
////			//TODO error handling.. if no transcript??
////			if (exportPage){
////				// xmlfiletype: just add the most recent transcript
////				TrpTranscriptMetadata tMd = p.getCurrentTranscript();
////				FileType xml = buildFileType(md.getLocalFolder(), xmlId, tMd, client);
////				pageGrp.getFile().add(xml);
////				Fptr xmlPtr = buildFptr(xml);
////				pageDiv.getFptr().add(xmlPtr);
////			}
////			
////			//creat ALTO fileGrp
////			if (exportAlto){
////				FileType altoFt = new FileType();
////				altoFt.setCHECKSUMTYPE(ChecksumUtils.ChkSumAlg.MD5.toString());
////				//TODO calculate checksum
////				altoFt.setCHECKSUM("");
////				FLocat fLocat = new FLocat();
////				fLocat.setLOCTYPE("OTHER");
////				fLocat.setOTHERLOCTYPE("FILE");
////					
////				altoFt.setID(altoId);
////				altoFt.setSEQ(p.getPageNr());
////
////				String tmpImgName = img.getFLocat().get(0).getHref();
////				String relAltoPath = "/alto".concat(tmpImgName.substring(0, tmpImgName.lastIndexOf(".")).concat(".xml"));
////				fLocat.setHref(relAltoPath);
////				
////				//String absAltoPath = tMd.getUrl().getPath().replace("page", "alto");
////				String absAltoPath = p.getUrl().getPath().substring(0, p.getUrl().getPath().lastIndexOf("/"));
////				absAltoPath = absAltoPath.concat("/alto/").concat(p.getImgFileName().substring(0, p.getImgFileName().lastIndexOf(".")).concat(".xml"));
////				//logger.info("alto path starts with: " + absAltoPath);
////				if (absAltoPath.startsWith("\\") || absAltoPath.startsWith("/")){
////					//logger.info("alto path starts with \\ or /");
////					absAltoPath = absAltoPath.substring(1);
////				}
////				
////				String mime = MimeTypes.getMimeType("xml");
////				altoFt.setMIMETYPE(mime);
////				
////				File altoTmp = new File(absAltoPath);
////				if(altoTmp.exists()){
////					//logger.info("alto file exist at " + absAltoPath);
////					Date date = new Date(altoTmp.lastModified());
////					XMLGregorianCalendar cal = JaxbUtils.getXmlCalendar(date);
////					altoFt.setCREATED(cal);
////				}
////				else{
////					logger.info("alto file does not exist at " + absAltoPath);
////				}
////				
//////				System.out.println("relAltoPath " + relAltoPath);
//////				System.out.println("absAltoPath " + absAltoPath);
//////				System.in.read();
////
////				altoFt.getFLocat().add(fLocat);
////				
////				altoGrp.getFile().add(altoFt);
////				Fptr altoPtr = buildFptr(altoFt);
////				pageDiv.getFptr().add(altoPtr);
////				
////			}
////	
////			div.getDiv().add(pageDiv);
////			
////			
////			
////		}
////		fileSec.getFileGrp().add(masterGrp);
////		mets.setFileSec(fileSec);
////		
////		masterGrp.getFileGrp().add(imgGrp);
////		if (exportPage){
////			masterGrp.getFileGrp().add(pageGrp);
////		}
////		if (exportAlto){
////			masterGrp.getFileGrp().add(altoGrp);
////		}
////		mets.getStructMap().add(structMap);		
////		
////		return mets;
//		return null;
//	}
//	
////	private static MetsHdr buildMetsHdr(TrpDocMetadata md) {
////		MetsHdr hdr = new MetsHdr();
////		
////		XMLGregorianCalendar xmlCal = JaxbUtils.getXmlCalendar(new Date());
////		
////		hdr.setCREATEDATE(xmlCal);
////		hdr.setLASTMODDATE(xmlCal);
////		//TODO set some real value for record status
////		hdr.setRECORDSTATUS("SUBMITTED");
////		MetsHdr.Agent agent = new MetsHdr.Agent();
////		agent.setTYPE("ORGANIZATION");
////		agent.setROLE("CREATOR");
////		//FIXME what is the real agent here?
////		agent.setName("UIBK");
////		agent.getNote().add("This METS file was generated by Transkribus");
////		hdr.getAgent().add(agent);
////		return hdr;
////	}
////	private static MdSecType buildSourceMdSec(TrpDocMetadata md) {
////		MdWrap wrap = new MdWrap();
////		wrap.setMDTYPE("OTHER");
////		wrap.setID(TRP_DOC_MD_TYPE_CONST);
////		MdWrap.XmlData xmlData = new MdWrap.XmlData();
////		
////		//clone md and set local folder to null
//////		TrpDocMetadata mdClone = md.clone();
//////		mdClone.setLocalFolder(null);
//////		xmlData.getAny().add(mdClone);
////		//FIXME if the localFolder is null, this will be treated as remote doc, but the fileKeys are null!! => nullpointerexception
////		//md.setLocalFolder(null);
////		xmlData.getAny().add(md);
////		
////		wrap.setXmlData(xmlData);
////		MdSecType sec = new MdSecType();
////		sec.setMdWrap(wrap);
////		//link to the sourceMD with TrpDocMetadata
////		sec.setID(SOURCE_DOC_MD_ID_CONST);
////		return sec;
////	}
//	
//	/**
//	 * @param localFolder null if isLocalDoc
//	 * @param id
//	 * @param o
//	 * @param client
//	 * @return
//	 * @throws IOException
//	 */
//	private static FileType buildFileType(File localFolder, String id, ITrpFile o, FimgStoreGetClient client) throws IOException {
//		FileType fType = new FileType();
//		fType.setID(id);
//		String mime = null;
//		Date date = null;
//		FLocat fLocat = new FLocat();
//		String loc = null;
//		if(localFolder != null){
//			URL url = o.getUrl();
//			if(!url.getProtocol().contains("file")){
//				throw new IOException("Doc contains local folder reference but an URL refers to a non-local file! " + url.toString());
//			}
//			final String path = url.getPath();
//			File f = new File(path);
//			mime = MimeTypes.getMimeType(DeaFileUtils.getFileExtension(f.getName()));
//			date = new Date(f.lastModified());
//			fLocat.setLOCTYPE("OTHER");
//			fLocat.setOTHERLOCTYPE("FILE");
//			//remove protocol and localfolder, i.e. get relative path to this file
//			loc = path.substring(localFolder.getAbsolutePath().length() + 1);
//			
//			if(o.getMd5Sum() != null){
//				fType.setCHECKSUMTYPE(ChecksumUtils.ChkSumAlg.MD5.toString());
//				fType.setCHECKSUM(o.getMd5Sum());
//			}
//		} else {
//			try {
//				FimgStoreFileMd fMd = client.getFileMd(o.getKey());
//				date = fMd.getUploadDate();
//				mime = fMd.getMimetype();
//				fLocat.setLOCTYPE("URL");
//				//full URL in case of remote file
//				loc = o.getUrl().toString();
//			} catch (IOException e) {
//				logger.error(e.getMessage(), e);
//				throw new IOException("FileMetadata could not be retrieved from imagestore for key: " + o.getKey());
//			}
//		}
//		fType.setMIMETYPE(mime);
//		
//		XMLGregorianCalendar cal = JaxbUtils.getXmlCalendar(date);
//		fType.setCREATED(cal);
//		fType.setSEQ(o.getPageNr());
//		fLocat.setHref(loc);
//		fType.getFLocat().add(fLocat);
//		return fType;
//	}
//	
//	private static Fptr buildFptr(FileType img) {
//		Fptr ptr = new Fptr();
//		AreaType area = new AreaType();
//		area.setFILEID(img);
//		ptr.setArea(area);
//		return ptr;
//	}
//
//	public static List<FileType> getOcrMasterFiles(Mets mets) throws IOException {
//		List<FileGrp> fileGrps = mets.getFileSec().getFileGrp();
//		
//		FileGrp imageGrps = null;
//		for(FileGrp grp : fileGrps){
//			if(grp.getID().equals(EnmapMetsBuilder.IMAGE_FILE_GRP_ID)){
//				imageGrps = grp;
//				break;
//			}
//		}
//		if(imageGrps == null) throw new IOException("METS file has no MASTER fileGrp!");
//		
//		FileGrpType ocrGrp = null;
//		for(FileGrpType grp : imageGrps.getFileGrp()){
//			if(grp.getID().equals(EnmapMetsBuilder.OCR_MASTER_FILE_GRP_ID)){
//				ocrGrp = grp;
//				break;
//			}
//		}
//				
//		return ocrGrp.getFile();
//	}
//
//	public static TrpDocMetadata getTrpDocMd(Mets mets) {
//		TrpDocMetadata md = new TrpDocMetadata();
////		List<AmdSecType> secList = mets.getAmdSec();
////		List<MdSecType> mdSecList = null;
////		for (AmdSecType sec : secList) {
////			if (sec.getID().equals(EnmapMetsBuilder.SOURCE_MD_ID_CONST)) {
////				mdSecList = sec.getSourceMD();
////				break;
////			}
////		}
////		if (mdSecList == null)
////			logger.error("No SourceMd Section found!");
////		else {
////			XmlData xmlData = null;
////			for (MdSecType mdSec : mdSecList) {
////				if (mdSec.getID().equals(EnmapMetsBuilder.SOURCE_DOC_MD_ID_CONST)
////						&& mdSec.getMdWrap().getID().equals(EnmapMetsBuilder.TRP_DOC_MD_TYPE_CONST)) {
////					xmlData = mdSec.getMdWrap().getXmlData();
////					break;
////				}
////			}
////			if (xmlData != null && xmlData.getAny().size() > 0) {
////				Object o = xmlData.getAny().get(0);
////				if (o instanceof TrpDocMetadata) {
////					md = (TrpDocMetadata) o;
////					logger.info("Found metadata: " + md.toString());
////				} else {
////					logger.error("No doc MD found! ");
////				}
////			}
////		}
//		return md;
//	}
//
//	public static List<TrpPage> getTrpPages(Mets mets, File parentDir) throws IOException {
//		//check filesection. needs img group and xml group to distinguish them without going for mimetypes
//
//		List<FileType> imgGrp = getOcrMasterFiles(mets);
//
//		List<FileType> xmlGrp = null;
//
//		
//		if (imgGrp == null)
//			throw new IOException("METS file has no image file list!");
//		if (xmlGrp == null)
//			throw new IOException("METS file has no xml file list!");
//		
//		List<DivType> pageDivs = null;
//		for(StructMapType sMap : mets.getStructMap()){
//			if(sMap.getID().equals(EnmapMetsBuilder.TRP_STRUCTMAP_ID) 
//					&& sMap.getDiv().getID().equals(EnmapMetsBuilder.TRP_DOC_DIV_ID)){
//				pageDivs = sMap.getDiv().getDiv();
//				break;
//			}
//		}
//		if(pageDivs == null)
//			throw new IOException("No valid StructMap was found!");
//		
//		List<TrpPage> pages = new ArrayList<TrpPage>(pageDivs.size());
//		for(DivType div : pageDivs){
//			TrpPage page = buildPage(div, imgGrp, xmlGrp, parentDir);
//			pages.add(page);
//		}
//		return pages;
//	}
//	
//	private static TrpPage buildPage(DivType div, List<FileType> imgGrp, List<FileType> xmlGrp, File parentDir) throws IOException {
//		TrpPage page = new TrpPage();
//		int nr = div.getORDER().intValue();
//		page.setPageNr(nr);
//		
//		File imgFile = null;
//		File xmlFile = null;
//		
//		
//		//FIXME this will only work for local files
//		for(Fptr ptr : div.getFptr()){
//			FileType type = (FileType)ptr.getArea().getFILEID();				
//			if(imgGrp.contains(type)){
//				imgFile = getFile(type, parentDir);
//			} else if (xmlGrp.contains(type)){
//				xmlFile = getFile(type, parentDir);
//			}
//		}
//		
//		if(imgFile == null) {
//			logger.error("No master image mapped for page " + nr + " in the structmap!");
//		} else {
//			logger.info("Page " + page.getPageNr() + " image: " + imgFile.getAbsolutePath());
//		}
//		page.setUrl(new URL(LocalDocConst.URL_PROT_CONST + imgFile.getAbsolutePath()));
//		page.setKey(null);
//		page.setDocId(-1);
//		page.setImgFileName(imgFile.getName());
//
//		if(xmlFile == null) {
//			logger.error("No master xml mapped for page " + nr + " in the structmap!");
//		} else {
//			logger.info("Page " + page.getPageNr() + " xml: " + xmlFile.getAbsolutePath());
//		}
//		TrpTranscriptMetadata tmd = new TrpTranscriptMetadata();
//		tmd.setPage(page);
//		tmd.setPageNr(nr);
//		tmd.setUrl(new URL(LocalDocConst.URL_PROT_CONST + xmlFile.getAbsolutePath()));
//		tmd.setKey(null);
//		tmd.setStatus(EditStatus.NEW);
//		tmd.setPageNr(nr);
//		tmd.setDocId(-1);
//		tmd.setTimestamp(new Date().getTime());
//		tmd.setUserName("LocalDocReader");
//
//		page.getTranscripts().add(tmd);
//		return page;
//	}
//
//	private static File getFile(FileType type, File parentDir) throws IOException{
//		File file = null;
//		FLocat fLocat = type.getFLocat().get(0);
//		if(fLocat.getOTHERLOCTYPE() != null && fLocat.getOTHERLOCTYPE().equals("FILE")){
//			//localdoc
//			file = new File(parentDir.getAbsolutePath() + File.separator + fLocat.getHref());
//			if(!file.exists()){
//				throw new IOException("File does not exist: " + file.getAbsolutePath());
//			}
//			
//			if(!type.isSetCHECKSUMTYPE()){
//				logger.error("No checksum set!");
//			} else if(!type.getCHECKSUMTYPE().equals(ChecksumUtils.ChkSumAlg.MD5.toString())){
//				logger.error("Unknown checksum algorithm: " + type.getCHECKSUMTYPE());
//			} else {
//				final String metsChkSum = type.getCHECKSUM();
//				final String chkSum = ChecksumUtils.getMd5SumHex(file);
//				if(!metsChkSum.equals(chkSum)){
//					throw new IOException("Checksum error: METS=" + metsChkSum + " <-> FILE=" + chkSum + " | " + file.getAbsolutePath());
//				}
//				logger.debug("Checksum is correct: " + file.getAbsolutePath());
//			}
//			
//		} else { 
//			//TODO implement for URL type
//			throw new IOException("METS file does not belong to a local document!");
//		}
//		return file;
//	}
//}
