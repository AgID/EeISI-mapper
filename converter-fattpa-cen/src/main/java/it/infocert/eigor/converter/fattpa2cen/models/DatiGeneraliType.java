//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.03.30 alle 11:53:02 AM CEST 
//


package it.infocert.eigor.converter.fattpa2cen.models;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Blocco relativo ai Dati Generali della Fattura Elettronica
 * 			
 * 
 * <p>Classe Java per DatiGeneraliType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="DatiGeneraliType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DatiGeneraliDocumento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiGeneraliDocumentoType"/&gt;
 *         &lt;element name="DatiOrdineAcquisto" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiDocumentiCorrelatiType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DatiContratto" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiDocumentiCorrelatiType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DatiConvenzione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiDocumentiCorrelatiType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DatiRicezione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiDocumentiCorrelatiType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DatiFattureCollegate" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiDocumentiCorrelatiType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DatiSAL" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiSALType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DatiDDT" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiDDTType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DatiTrasporto" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiTrasportoType" minOccurs="0"/&gt;
 *         &lt;element name="FatturaPrincipale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}FatturaPrincipaleType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatiGeneraliType", propOrder = {
    "datiGeneraliDocumento",
    "datiOrdineAcquisto",
    "datiContratto",
    "datiConvenzione",
    "datiRicezione",
    "datiFattureCollegate",
    "datiSAL",
    "datiDDT",
    "datiTrasporto",
    "fatturaPrincipale"
})
public class DatiGeneraliType {

    @XmlElement(name = "DatiGeneraliDocumento", required = true)
    protected DatiGeneraliDocumentoType datiGeneraliDocumento;
    @XmlElement(name = "DatiOrdineAcquisto")
    protected List<DatiDocumentiCorrelatiType> datiOrdineAcquisto;
    @XmlElement(name = "DatiContratto")
    protected List<DatiDocumentiCorrelatiType> datiContratto;
    @XmlElement(name = "DatiConvenzione")
    protected List<DatiDocumentiCorrelatiType> datiConvenzione;
    @XmlElement(name = "DatiRicezione")
    protected List<DatiDocumentiCorrelatiType> datiRicezione;
    @XmlElement(name = "DatiFattureCollegate")
    protected List<DatiDocumentiCorrelatiType> datiFattureCollegate;
    @XmlElement(name = "DatiSAL")
    protected List<DatiSALType> datiSAL;
    @XmlElement(name = "DatiDDT")
    protected List<DatiDDTType> datiDDT;
    @XmlElement(name = "DatiTrasporto")
    protected DatiTrasportoType datiTrasporto;
    @XmlElement(name = "FatturaPrincipale")
    protected FatturaPrincipaleType fatturaPrincipale;

    /**
     * Recupera il valore della proprietà datiGeneraliDocumento.
     * 
     * @return
     *     possible object is
     *     {@link DatiGeneraliDocumentoType }
     *     
     */
    public DatiGeneraliDocumentoType getDatiGeneraliDocumento() {
        return datiGeneraliDocumento;
    }

    /**
     * Imposta il valore della proprietà datiGeneraliDocumento.
     * 
     * @param value
     *     allowed object is
     *     {@link DatiGeneraliDocumentoType }
     *     
     */
    public void setDatiGeneraliDocumento(DatiGeneraliDocumentoType value) {
        this.datiGeneraliDocumento = value;
    }

    /**
     * Gets the value of the datiOrdineAcquisto property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datiOrdineAcquisto property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatiOrdineAcquisto().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatiDocumentiCorrelatiType }
     * 
     * 
     */
    public List<DatiDocumentiCorrelatiType> getDatiOrdineAcquisto() {
        if (datiOrdineAcquisto == null) {
            datiOrdineAcquisto = new ArrayList<DatiDocumentiCorrelatiType>();
        }
        return this.datiOrdineAcquisto;
    }

    /**
     * Gets the value of the datiContratto property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datiContratto property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatiContratto().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatiDocumentiCorrelatiType }
     * 
     * 
     */
    public List<DatiDocumentiCorrelatiType> getDatiContratto() {
        if (datiContratto == null) {
            datiContratto = new ArrayList<DatiDocumentiCorrelatiType>();
        }
        return this.datiContratto;
    }

    /**
     * Gets the value of the datiConvenzione property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datiConvenzione property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatiConvenzione().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatiDocumentiCorrelatiType }
     * 
     * 
     */
    public List<DatiDocumentiCorrelatiType> getDatiConvenzione() {
        if (datiConvenzione == null) {
            datiConvenzione = new ArrayList<DatiDocumentiCorrelatiType>();
        }
        return this.datiConvenzione;
    }

    /**
     * Gets the value of the datiRicezione property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datiRicezione property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatiRicezione().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatiDocumentiCorrelatiType }
     * 
     * 
     */
    public List<DatiDocumentiCorrelatiType> getDatiRicezione() {
        if (datiRicezione == null) {
            datiRicezione = new ArrayList<DatiDocumentiCorrelatiType>();
        }
        return this.datiRicezione;
    }

    /**
     * Gets the value of the datiFattureCollegate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datiFattureCollegate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatiFattureCollegate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatiDocumentiCorrelatiType }
     * 
     * 
     */
    public List<DatiDocumentiCorrelatiType> getDatiFattureCollegate() {
        if (datiFattureCollegate == null) {
            datiFattureCollegate = new ArrayList<DatiDocumentiCorrelatiType>();
        }
        return this.datiFattureCollegate;
    }

    /**
     * Gets the value of the datiSAL property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datiSAL property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatiSAL().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatiSALType }
     * 
     * 
     */
    public List<DatiSALType> getDatiSAL() {
        if (datiSAL == null) {
            datiSAL = new ArrayList<DatiSALType>();
        }
        return this.datiSAL;
    }

    /**
     * Gets the value of the datiDDT property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datiDDT property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatiDDT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatiDDTType }
     * 
     * 
     */
    public List<DatiDDTType> getDatiDDT() {
        if (datiDDT == null) {
            datiDDT = new ArrayList<DatiDDTType>();
        }
        return this.datiDDT;
    }

    /**
     * Recupera il valore della proprietà datiTrasporto.
     * 
     * @return
     *     possible object is
     *     {@link DatiTrasportoType }
     *     
     */
    public DatiTrasportoType getDatiTrasporto() {
        return datiTrasporto;
    }

    /**
     * Imposta il valore della proprietà datiTrasporto.
     * 
     * @param value
     *     allowed object is
     *     {@link DatiTrasportoType }
     *     
     */
    public void setDatiTrasporto(DatiTrasportoType value) {
        this.datiTrasporto = value;
    }

    /**
     * Recupera il valore della proprietà fatturaPrincipale.
     * 
     * @return
     *     possible object is
     *     {@link FatturaPrincipaleType }
     *     
     */
    public FatturaPrincipaleType getFatturaPrincipale() {
        return fatturaPrincipale;
    }

    /**
     * Imposta il valore della proprietà fatturaPrincipale.
     * 
     * @param value
     *     allowed object is
     *     {@link FatturaPrincipaleType }
     *     
     */
    public void setFatturaPrincipale(FatturaPrincipaleType value) {
        this.fatturaPrincipale = value;
    }

}
