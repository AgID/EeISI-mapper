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


/**
 * <p>Classe Java per DatiBolloType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="DatiBolloType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BolloVirtuale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}BolloVirtualeType"/&gt;
 *         &lt;element name="ImportoBollo" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}Amount2DecimalType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatiBolloType", propOrder = {
    "bolloVirtuale",
    "importoBollo"
})
public class DatiBolloType {

    @XmlElement(name = "BolloVirtuale", required = true)
    @XmlSchemaType(name = "string")
    protected BolloVirtualeType bolloVirtuale;
    @XmlElement(name = "ImportoBollo", required = true)
    protected BigDecimal importoBollo;

    /**
     * Recupera il valore della proprietà bolloVirtuale.
     * 
     * @return
     *     possible object is
     *     {@link BolloVirtualeType }
     *     
     */
    public BolloVirtualeType getBolloVirtuale() {
        return bolloVirtuale;
    }

    /**
     * Imposta il valore della proprietà bolloVirtuale.
     * 
     * @param value
     *     allowed object is
     *     {@link BolloVirtualeType }
     *     
     */
    public void setBolloVirtuale(BolloVirtualeType value) {
        this.bolloVirtuale = value;
    }

    /**
     * Recupera il valore della proprietà importoBollo.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getImportoBollo() {
        return importoBollo;
    }

    /**
     * Imposta il valore della proprietà importoBollo.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setImportoBollo(BigDecimal value) {
        this.importoBollo = value;
    }

}
