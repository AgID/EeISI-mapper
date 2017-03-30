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
 * 				Blocco relativo ai dati di Beni Servizi della Fattura	Elettronica
 * 			
 * 
 * <p>Classe Java per DatiBeniServiziType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="DatiBeniServiziType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DettaglioLinee" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DettaglioLineeType" maxOccurs="unbounded"/&gt;
 *         &lt;element name="DatiRiepilogo" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiRiepilogoType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatiBeniServiziType", propOrder = {
    "dettaglioLinee",
    "datiRiepilogo"
})
public class DatiBeniServiziType {

    @XmlElement(name = "DettaglioLinee", required = true)
    protected List<DettaglioLineeType> dettaglioLinee;
    @XmlElement(name = "DatiRiepilogo", required = true)
    protected List<DatiRiepilogoType> datiRiepilogo;

    /**
     * Gets the value of the dettaglioLinee property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dettaglioLinee property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDettaglioLinee().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DettaglioLineeType }
     * 
     * 
     */
    public List<DettaglioLineeType> getDettaglioLinee() {
        if (dettaglioLinee == null) {
            dettaglioLinee = new ArrayList<DettaglioLineeType>();
        }
        return this.dettaglioLinee;
    }

    /**
     * Gets the value of the datiRiepilogo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datiRiepilogo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatiRiepilogo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatiRiepilogoType }
     * 
     * 
     */
    public List<DatiRiepilogoType> getDatiRiepilogo() {
        if (datiRiepilogo == null) {
            datiRiepilogo = new ArrayList<DatiRiepilogoType>();
        }
        return this.datiRiepilogo;
    }

}
