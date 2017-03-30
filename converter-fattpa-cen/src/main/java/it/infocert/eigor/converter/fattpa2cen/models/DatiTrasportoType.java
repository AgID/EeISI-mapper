//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.03.30 alle 11:53:02 AM CEST 
//


package it.infocert.eigor.converter.fattpa2cen.models;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java per DatiTrasportoType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="DatiTrasportoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DatiAnagraficiVettore" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiAnagraficiVettoreType" minOccurs="0"/&gt;
 *         &lt;element name="MezzoTrasporto" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}String80LatinType" minOccurs="0"/&gt;
 *         &lt;element name="CausaleTrasporto" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}String100LatinType" minOccurs="0"/&gt;
 *         &lt;element name="NumeroColli" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}NumeroColliType" minOccurs="0"/&gt;
 *         &lt;element name="Descrizione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}String100LatinType" minOccurs="0"/&gt;
 *         &lt;element name="UnitaMisuraPeso" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}String10Type" minOccurs="0"/&gt;
 *         &lt;element name="PesoLordo" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}PesoType" minOccurs="0"/&gt;
 *         &lt;element name="PesoNetto" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}PesoType" minOccurs="0"/&gt;
 *         &lt;element name="DataOraRitiro" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="DataInizioTrasporto" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="TipoResa" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}TipoResaType" minOccurs="0"/&gt;
 *         &lt;element name="IndirizzoResa" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}IndirizzoType" minOccurs="0"/&gt;
 *         &lt;element name="DataOraConsegna" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatiTrasportoType", propOrder = {
    "datiAnagraficiVettore",
    "mezzoTrasporto",
    "causaleTrasporto",
    "numeroColli",
    "descrizione",
    "unitaMisuraPeso",
    "pesoLordo",
    "pesoNetto",
    "dataOraRitiro",
    "dataInizioTrasporto",
    "tipoResa",
    "indirizzoResa",
    "dataOraConsegna"
})
public class DatiTrasportoType {

    @XmlElement(name = "DatiAnagraficiVettore")
    protected DatiAnagraficiVettoreType datiAnagraficiVettore;
    @XmlElement(name = "MezzoTrasporto")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String mezzoTrasporto;
    @XmlElement(name = "CausaleTrasporto")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String causaleTrasporto;
    @XmlElement(name = "NumeroColli")
    @XmlSchemaType(name = "integer")
    protected Integer numeroColli;
    @XmlElement(name = "Descrizione")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String descrizione;
    @XmlElement(name = "UnitaMisuraPeso")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String unitaMisuraPeso;
    @XmlElement(name = "PesoLordo")
    protected BigDecimal pesoLordo;
    @XmlElement(name = "PesoNetto")
    protected BigDecimal pesoNetto;
    @XmlElement(name = "DataOraRitiro")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataOraRitiro;
    @XmlElement(name = "DataInizioTrasporto")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataInizioTrasporto;
    @XmlElement(name = "TipoResa")
    protected String tipoResa;
    @XmlElement(name = "IndirizzoResa")
    protected IndirizzoType indirizzoResa;
    @XmlElement(name = "DataOraConsegna")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataOraConsegna;

    /**
     * Recupera il valore della proprietà datiAnagraficiVettore.
     * 
     * @return
     *     possible object is
     *     {@link DatiAnagraficiVettoreType }
     *     
     */
    public DatiAnagraficiVettoreType getDatiAnagraficiVettore() {
        return datiAnagraficiVettore;
    }

    /**
     * Imposta il valore della proprietà datiAnagraficiVettore.
     * 
     * @param value
     *     allowed object is
     *     {@link DatiAnagraficiVettoreType }
     *     
     */
    public void setDatiAnagraficiVettore(DatiAnagraficiVettoreType value) {
        this.datiAnagraficiVettore = value;
    }

    /**
     * Recupera il valore della proprietà mezzoTrasporto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMezzoTrasporto() {
        return mezzoTrasporto;
    }

    /**
     * Imposta il valore della proprietà mezzoTrasporto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMezzoTrasporto(String value) {
        this.mezzoTrasporto = value;
    }

    /**
     * Recupera il valore della proprietà causaleTrasporto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCausaleTrasporto() {
        return causaleTrasporto;
    }

    /**
     * Imposta il valore della proprietà causaleTrasporto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCausaleTrasporto(String value) {
        this.causaleTrasporto = value;
    }

    /**
     * Recupera il valore della proprietà numeroColli.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumeroColli() {
        return numeroColli;
    }

    /**
     * Imposta il valore della proprietà numeroColli.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumeroColli(Integer value) {
        this.numeroColli = value;
    }

    /**
     * Recupera il valore della proprietà descrizione.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Imposta il valore della proprietà descrizione.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescrizione(String value) {
        this.descrizione = value;
    }

    /**
     * Recupera il valore della proprietà unitaMisuraPeso.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitaMisuraPeso() {
        return unitaMisuraPeso;
    }

    /**
     * Imposta il valore della proprietà unitaMisuraPeso.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitaMisuraPeso(String value) {
        this.unitaMisuraPeso = value;
    }

    /**
     * Recupera il valore della proprietà pesoLordo.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPesoLordo() {
        return pesoLordo;
    }

    /**
     * Imposta il valore della proprietà pesoLordo.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPesoLordo(BigDecimal value) {
        this.pesoLordo = value;
    }

    /**
     * Recupera il valore della proprietà pesoNetto.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPesoNetto() {
        return pesoNetto;
    }

    /**
     * Imposta il valore della proprietà pesoNetto.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPesoNetto(BigDecimal value) {
        this.pesoNetto = value;
    }

    /**
     * Recupera il valore della proprietà dataOraRitiro.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataOraRitiro() {
        return dataOraRitiro;
    }

    /**
     * Imposta il valore della proprietà dataOraRitiro.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataOraRitiro(XMLGregorianCalendar value) {
        this.dataOraRitiro = value;
    }

    /**
     * Recupera il valore della proprietà dataInizioTrasporto.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataInizioTrasporto() {
        return dataInizioTrasporto;
    }

    /**
     * Imposta il valore della proprietà dataInizioTrasporto.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataInizioTrasporto(XMLGregorianCalendar value) {
        this.dataInizioTrasporto = value;
    }

    /**
     * Recupera il valore della proprietà tipoResa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoResa() {
        return tipoResa;
    }

    /**
     * Imposta il valore della proprietà tipoResa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoResa(String value) {
        this.tipoResa = value;
    }

    /**
     * Recupera il valore della proprietà indirizzoResa.
     * 
     * @return
     *     possible object is
     *     {@link IndirizzoType }
     *     
     */
    public IndirizzoType getIndirizzoResa() {
        return indirizzoResa;
    }

    /**
     * Imposta il valore della proprietà indirizzoResa.
     * 
     * @param value
     *     allowed object is
     *     {@link IndirizzoType }
     *     
     */
    public void setIndirizzoResa(IndirizzoType value) {
        this.indirizzoResa = value;
    }

    /**
     * Recupera il valore della proprietà dataOraConsegna.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataOraConsegna() {
        return dataOraConsegna;
    }

    /**
     * Imposta il valore della proprietà dataOraConsegna.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataOraConsegna(XMLGregorianCalendar value) {
        this.dataOraConsegna = value;
    }

}
