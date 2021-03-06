package eu.transkribus.core.model.beans.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * Generic list wrapper including pagination and sort information.<br/>
 * <br/>
 * <b>IMPORTANT</b>: when extending this you must use the {@link XmlSeeAlso} annotation to 
 * specify all the included classes that the JAXB context needs!<br/>
 * See {@link TrpKwsQueryList} for example.<br/>
 * <br/>
 * TODO: try to use {@link ContextResolver} instead of XmlSeeAlso annotation as it might be faster. 
 * FIXME & WTF: Jersey can't unmarshal this type from JSON correctly!?! The list will contain elements of type com.sun.org.apache.xerces.internal.dom.ElementNSImpl containing the attributes!
 * This might be caused by XmlAnyElement(lax=true) but without that it does not work at all... stick to XML for now with Java
 * @author philip
 *
 * @param <T>
 */
@XmlRootElement(name="ResultList")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class JaxbPaginatedList<T> {
	
	@XmlAttribute
	protected int total;
	
	@XmlAttribute
	protected int index;
	
	@XmlAttribute
	protected int nValues;
	
	@XmlAttribute
	protected String sortColumnField;
	
	@XmlAttribute
	protected String sortDirection;
	
	@XmlAnyElement(lax=true)
	protected List<T> list;

    public JaxbPaginatedList(){
    	//uncomment line to always have a possibly emtpy list field in the string represenation (and no nullpointers on access)
//    	list = new ArrayList<>(0);
    }

    public JaxbPaginatedList(List<T> list, int total, int index, int nValues, 
    		String sortColumnField, String sortDirection){
    	this();
    	if(list == null) {
    		list = new ArrayList<>(0);
    	}
    	this.setList(list);
    	this.total = total;
    	this.index = index;
    	this.nValues = nValues;
    	this.sortColumnField = sortColumnField;
    	this.sortDirection = sortDirection;
    }
    
    public List<T> getList() {
    	return list;
    }
    
    public void setList(List<T> list) {
    	this.list = list;
    }

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getnValues() {
		return nValues;
	}

	public void setnValues(int nValues) {
		this.nValues = nValues;
	}

	public String getSortColumnField() {
		return sortColumnField;
	}

	public void setSortColumnField(String sortColumnField) {
		this.sortColumnField = sortColumnField;
	}

	public String getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
	}
}