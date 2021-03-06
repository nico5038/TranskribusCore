package eu.transkribus.core.model.beans;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.adapters.SqlTimestampAdapter;
import eu.transkribus.core.model.beans.enums.DocType;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.HtrCITlabUtils;
import eu.transkribus.core.util.HtrPyLaiaUtils;

@Entity
@Table(name = "HTR")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpHtr {
	private static final Logger logger = LoggerFactory.getLogger(TrpHtr.class);
	
	@Id
	@Column(name="HTR_ID")
	private int htrId;
	
	@Column
	private String name;
	
	@Column
	private String description;
	
	@Column
	private String provider;
	
	/**
	 * path is better created from HTR ID and DbServiceName
	 */
	@XmlTransient
	@Column
	private String path;
	
	@Column
	@XmlJavaTypeAdapter(SqlTimestampAdapter.class)
	private Timestamp created;
	
	@Column(name="TRAIN_GT_DOCID")
	private Integer gtDocId;
	
	@Column(name="TEST_GT_DOCID")
	private Integer testGtDocId;
	
	@Column(name="LANGUAGE")
	private String language;
	
	@Column(name="BASE_HTR_ID")
	private Integer baseHtrId;
	
	@Column(name="TRAIN_JOB_ID")
	private String trainJobId;
	
	@Column(name="TRAIN_CER_SERIES")
	private String cerString;
	
	@Column(name="TEST_CER_SERIES")
	private String cerTestString;
	
	/**
	 * Contains the CITlab specific char-to-channel mapping
	 */
	@Deprecated
	private String charList;
	
	@Column(name="CHARSET")
	private String charSetString;
	
	
	@Column(name="HAS_BEST_NET")
	private boolean bestNetStored = true;
	
	@Column(name="HAS_LANGUAGE_MODEL")
	private boolean languageModelExists = false;
	
	@Column(name="NR_OF_LINES")
	private int nrOfLines;
	
	@Column(name="NR_OF_WORDS")
	private int nrOfWords;
	
	@Column(name="PARAMS")
	private String params;
	
	@Transient
	@Column
	private String userName;
	
	@Transient
	@Column
	private int userId;
	
	@Transient
	@Column(name="NR_OF_TRAIN_GT_PAGES")
	private Integer nrOfTrainGtPages;
	
	@Transient
	@Column(name="NR_OF_VALIDATION_GT_PAGES")
	private Integer nrOfValidationGtPages;
	
	@Column(name="RELEASE_LEVEL")
	private int releaseLevelValue;

	@Transient
	@Column(name="COLLECTION_ID_LINK")
	private Integer collectionIdLink;
	
	@Column(name="DEL_TIME")
	@XmlJavaTypeAdapter(SqlTimestampAdapter.class)
	private Timestamp delTime;
	
	//those fields just cache the split result from cerString and cerTestString
	private double[] cerLog = null;
	private double[] cerTestLog = null;
	
	/**
	 * Specifies the document type this model is trained on, i.e. print or handwritten. The field is required to determine the credit cost factor applying to recognition.
	 */
	@Column(name="DOC_TYPE")
	private int docType;
	
	public TrpHtr() {
		//docType = handwritten if nothing else is specified
		this.docType = DocType.HANDWRITTEN.getValue();
	}
	
	public TrpHtr(TrpHtr otherHtr) {
		super();
		this.htrId = otherHtr.htrId;
		this.name = otherHtr.name;
		this.description = otherHtr.description;
		this.provider = otherHtr.provider;
		this.path = otherHtr.path;
		this.created = otherHtr.created;
		this.gtDocId = otherHtr.gtDocId;
		this.testGtDocId = otherHtr.testGtDocId;
		this.language = otherHtr.language;
		this.baseHtrId = otherHtr.baseHtrId;
		this.trainJobId = otherHtr.trainJobId;
		this.cerString = otherHtr.cerString;
		this.cerTestString = otherHtr.cerTestString;
		this.charList = otherHtr.charList;
		this.charSetString = otherHtr.charSetString;
		this.bestNetStored = otherHtr.bestNetStored;
		this.languageModelExists = otherHtr.languageModelExists;
		this.nrOfLines = otherHtr.nrOfLines;
		this.nrOfWords = otherHtr.nrOfWords;
		this.params = otherHtr.params;
		this.userName = otherHtr.userName;
		this.userId = otherHtr.userId;
		this.nrOfTrainGtPages = otherHtr.nrOfTrainGtPages;
		this.nrOfValidationGtPages = otherHtr.nrOfValidationGtPages;
		this.releaseLevelValue = otherHtr.releaseLevelValue;
		this.collectionIdLink = otherHtr.collectionIdLink;
		this.delTime = otherHtr.delTime;
		this.cerLog = otherHtr.cerLog;
		this.cerTestLog = otherHtr.cerTestLog;
		this.docType = otherHtr.docType;
	}

	public int getHtrId() {
		return htrId;
	}

	public void setHtrId(int htrId) {
		this.htrId = htrId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public Integer getGtDocId() {
		return gtDocId;
	}

	public void setGtDocId(Integer gtDocId) {
		this.gtDocId = gtDocId;
	}
	
	public Integer getTestGtDocId() {
		return testGtDocId;
	}

	public void setTestGtDocId(Integer testGtDocId) {
		this.testGtDocId = testGtDocId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Integer getBaseHtrId() {
		return baseHtrId;
	}

	public void setBaseHtrId(Integer baseHtrId) {
		this.baseHtrId = baseHtrId;
	}
	
	public String getTrainJobId() {
		return trainJobId;
	}

	public void setTrainJobId(String trainJobId) {
		this.trainJobId = trainJobId;
	}
	
	public String getCerString() {
		return cerString;
	}

	public void setCerString(String cerLogString) {
		this.cerString = cerLogString;
	}
	
	public String getCerTestString() {
		return cerTestString;
	}

	public void setCerTestString(String cerTestLogString) {
		this.cerTestString = cerTestLogString;
	}
	
	/**
	 * CITlab specific charmap string. needed by GUI versions &lt; 1.5<br>
	 * Channel mapping may not match the actual one! So do not use this for HTR.
	 */
	@Deprecated
	public String getCharList() {
		return charList;
	}
	
	/**
	 * CITlab specific charmap string. needed by GUI versions &lt; 1.5
	 */
	@Deprecated
	public void setCharList(String charList) {
		if(charList != null && !charList.isEmpty()) {
			this.charList = charList;
		}
	}
	
	/**
	 * Fake the CITlab syntax on the basis of what is stored in DB. needed by GUI versions &lt; 1.5<br>
	 * Channel mapping may not match the actual one! So do not use this for HTR.
	 * 
	 * @return
	 */
	@Deprecated
	private String createCharList() {
		int i = 1;
		String charListStr = "";
		for(String s : getCharSetList()) {
			charListStr += s + "=" + i + "\n";
		}
		return charListStr.trim();
	}
	
	public String getCharSetString() {
		return charSetString;
	}

	public void setCharSetString(String charSet) {
		this.charList = createCharList();
		this.charSetString = charSet;
	}
	
	public List<String> getCharSetList() {
		if(StringUtils.isEmpty(charSetString)) {
			return new ArrayList<>();
		}
		return Arrays.asList(charSetString.split("\n"));
	}

	public boolean isBestNetStored() {
		return bestNetStored;
	}

	public void setBestNetStored(boolean bestNetStored) {
		this.bestNetStored = bestNetStored;
	}

	public boolean isLanguageModelExists() {
		return languageModelExists;
	}

	public void setLanguageModelExists(boolean dictionaryExists) {
		this.languageModelExists = dictionaryExists;
	}

	public String getParams() {
		return params;
	}
	
	public Properties getParamsProps() {
		if(params == null || params.isEmpty()) {
			return new Properties();
		}
		try {
			return CoreUtils.readPropertiesFromString(params);
		} catch (IOException ioe) {
			logger.error("Could nor read Properties from String:\n" + params);
			return new Properties();
		}
	}
	
	public void setParams(String params) {
		this.params = params;
	}

	public int getNrOfLines() {
		return nrOfLines;
	}

	public void setNrOfLines(int nrOfLines) {
		this.nrOfLines = nrOfLines;
	}

	public int getNrOfWords() {
		return nrOfWords;
	}

	public void setNrOfWords(int nrOfWords) {
		this.nrOfWords = nrOfWords;
	}
	
	public double[] getCerLog() {
		if(cerLog == null) {
			cerLog = HtrCITlabUtils.parseCitlabCerString(cerString);
		}
		return cerLog;
	}
	
	public double[] getCerTestLog() {
		if(cerTestLog == null) {
			cerTestLog = HtrCITlabUtils.parseCitlabCerString(cerTestString);
		}
		return cerTestLog;
	}
	
	public double getFinalTrainCerVal() {
		if(!hasCerLog()) {
			return -1;
		}
		return getCerLog()[getCerLog().length-1];
	}
	
	public boolean hasCerLog() {
		return cerString != null && !cerString.isEmpty();
	}

	public boolean hasCerTestLog() {
		return cerTestString != null && !cerTestString.isEmpty();
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getReleaseLevelValue() {
		return releaseLevelValue;
	}

	public void setReleaseLevelValue(int releaseLevelValue) {
		this.releaseLevelValue = releaseLevelValue;
	}
	
	public ReleaseLevel getReleaseLevel() {
		return ReleaseLevel.fromValue(releaseLevelValue);
	}

	public void setReleaseLevel(ReleaseLevel level) {
		this.releaseLevelValue = level.getValue();
	}

	public Integer getCollectionIdLink() {
		return collectionIdLink;
	}

	public void setCollectionIdLink(Integer collectionIdLink) {
		this.collectionIdLink = collectionIdLink;
	}

	public Integer getNrOfTrainGtPages() {
		return nrOfTrainGtPages;
	}
	
	public void setNrOfTrainGtPages(Integer nrOfTrainGtPages) {
		this.nrOfTrainGtPages = nrOfTrainGtPages;
	}
	
	public Integer getNrOfValidationGtPages() {
		return nrOfValidationGtPages;
	}
	
	public void setNrOfValidationGtPages(Integer nrOfValidationGtPages) {
		this.nrOfValidationGtPages = nrOfValidationGtPages;
	}

	public Timestamp getDelTime() {
		return delTime;
	}
	
	public void setDelTime(Timestamp delTime) {
		this.delTime = delTime;
	}
	
	public boolean isDeleted() {
		return this.delTime != null;
	}
	
	public int getDocType() {
		return docType;
	}
	
	public void setDocType(int docType) {
		this.docType = docType;
	}
		
	public boolean hasTrainGt() {
		return getNrOfTrainGtPages() != null && getNrOfTrainGtPages() > 0;
	}
	
	public boolean hasValidationGt() {
		return getNrOfValidationGtPages() != null && getNrOfValidationGtPages() > 0;
	}
	
	public boolean isPyLaiaModel() {
		return StringUtils.equals(getProvider(), HtrPyLaiaUtils.PROVIDER_PYLAIA);
	}
	
	public String toShortString() {
		return "TrpHtr [htrId=" + htrId + ", name=" + name + ", provider=" + provider
				+ ", path=" + path + ", created=" + created + ", language=" + language + ", baseHtrId=" + baseHtrId 
				+ ", trainJobId=" + trainJobId + ", bestNetStored=" + bestNetStored + ", languageModelExists=" + languageModelExists
				+ ", nrOfLines=" + nrOfLines + ", nrOfWords=" + nrOfWords + ", userName="
				+ userName + ", userId=" + userId + ", nrOfTrainGtPages=" + nrOfTrainGtPages
				+ ", nrOfValidationGtPages=" + nrOfValidationGtPages + ", releaseLevelValue=" + releaseLevelValue
				+ ", collectionIdLink=" + collectionIdLink + "]";
	}

	@Override
	public String toString() {
		return "TrpHtr [htrId=" + htrId + ", name=" + name + ", description=" + description + ", provider=" + provider
				+ ", path=" + path + ", created=" + created + ", gtDocId=" + gtDocId + ", testGtDocId=" + testGtDocId
				+ ", language=" + language + ", baseHtrId=" + baseHtrId + ", trainJobId=" + trainJobId + ", cerString="
				+ cerString + ", cerTestString=" + cerTestString + ", charList=" + charList + ", charSetString="
				+ charSetString + ", bestNetStored=" + bestNetStored + ", languageModelExists=" + languageModelExists
				+ ", nrOfLines=" + nrOfLines + ", nrOfWords=" + nrOfWords + ", params=" + params + ", userName="
				+ userName + ", userId=" + userId + ", nrOfTrainGtPages=" + nrOfTrainGtPages
				+ ", nrOfValidationGtPages=" + nrOfValidationGtPages + ", releaseLevelValue=" + releaseLevelValue
				+ ", collectionIdLink=" + collectionIdLink + ", delTime=" + delTime + ", cerLog="
				+ Arrays.toString(cerLog) + ", cerTestLog=" + Arrays.toString(cerTestLog) + ", docType=" + docType
				+ "]";
	}
}
