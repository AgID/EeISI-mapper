//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.03.30 alle 11:53:02 AM CEST 
//


package it.infocert.eigor.converter.fattpa2cen.models;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per TipoCassaType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="TipoCassaType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;length value="4"/&gt;
 *     &lt;enumeration value="TC01"/&gt;
 *     &lt;enumeration value="TC02"/&gt;
 *     &lt;enumeration value="TC03"/&gt;
 *     &lt;enumeration value="TC04"/&gt;
 *     &lt;enumeration value="TC05"/&gt;
 *     &lt;enumeration value="TC06"/&gt;
 *     &lt;enumeration value="TC07"/&gt;
 *     &lt;enumeration value="TC08"/&gt;
 *     &lt;enumeration value="TC09"/&gt;
 *     &lt;enumeration value="TC10"/&gt;
 *     &lt;enumeration value="TC11"/&gt;
 *     &lt;enumeration value="TC12"/&gt;
 *     &lt;enumeration value="TC13"/&gt;
 *     &lt;enumeration value="TC14"/&gt;
 *     &lt;enumeration value="TC15"/&gt;
 *     &lt;enumeration value="TC16"/&gt;
 *     &lt;enumeration value="TC17"/&gt;
 *     &lt;enumeration value="TC18"/&gt;
 *     &lt;enumeration value="TC19"/&gt;
 *     &lt;enumeration value="TC20"/&gt;
 *     &lt;enumeration value="TC21"/&gt;
 *     &lt;enumeration value="TC22"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TipoCassaType")
@XmlEnum
public enum TipoCassaType {


    /**
     * 
     * 						Cassa nazionale previdenza e assistenza avvocati e procuratori legali
     * 					
     * 
     */
    @XmlEnumValue("TC01")
    TC_01("TC01"),

    /**
     * 
     * 						Cassa previdenza dottori commercialisti
     * 					
     * 
     */
    @XmlEnumValue("TC02")
    TC_02("TC02"),

    /**
     * 
     * 						Cassa previdenza e assistenza geometri
     * 					
     * 
     */
    @XmlEnumValue("TC03")
    TC_03("TC03"),

    /**
     * 
     * 						Cassa nazionale previdenza e assistenza ingegneri e architetti liberi professionisti
     * 					
     * 
     */
    @XmlEnumValue("TC04")
    TC_04("TC04"),

    /**
     * 
     * 						Cassa nazionale del notariato
     * 					
     * 
     */
    @XmlEnumValue("TC05")
    TC_05("TC05"),

    /**
     * 
     * 						Cassa nazionale previdenza e assistenza ragionieri e periti commerciali
     * 					
     * 
     */
    @XmlEnumValue("TC06")
    TC_06("TC06"),

    /**
     * 
     * 						Ente nazionale assistenza agenti e rappresentanti di commercio (ENASARCO)
     * 					
     * 
     */
    @XmlEnumValue("TC07")
    TC_07("TC07"),

    /**
     * 
     * 						Ente nazionale previdenza e assistenza consulenti del lavoro (ENPACL)
     * 					
     * 
     */
    @XmlEnumValue("TC08")
    TC_08("TC08"),

    /**
     * 
     * 						Ente nazionale previdenza e assistenza medici (ENPAM)
     * 					
     * 
     */
    @XmlEnumValue("TC09")
    TC_09("TC09"),

    /**
     * 
     * 						Ente nazionale previdenza e assistenza farmacisti (ENPAF)
     * 					
     * 
     */
    @XmlEnumValue("TC10")
    TC_10("TC10"),

    /**
     * 
     * 						Ente nazionale previdenza e assistenza veterinari (ENPAV)
     * 					
     * 
     */
    @XmlEnumValue("TC11")
    TC_11("TC11"),

    /**
     * 
     * 						Ente nazionale previdenza e assistenza impiegati dell'agricoltura (ENPAIA)
     * 					
     * 
     */
    @XmlEnumValue("TC12")
    TC_12("TC12"),

    /**
     * 
     * 						Fondo previdenza impiegati imprese di spedizione e agenzie marittime
     * 					
     * 
     */
    @XmlEnumValue("TC13")
    TC_13("TC13"),

    /**
     * 
     * 						Istituto nazionale previdenza giornalisti italiani (INPGI)
     * 					
     * 
     */
    @XmlEnumValue("TC14")
    TC_14("TC14"),

    /**
     * 
     * 						Opera nazionale assistenza orfani sanitari italiani (ONAOSI)
     * 					
     * 
     */
    @XmlEnumValue("TC15")
    TC_15("TC15"),

    /**
     * 
     * 						Cassa autonoma assistenza integrativa giornalisti italiani (CASAGIT)
     * 					
     * 
     */
    @XmlEnumValue("TC16")
    TC_16("TC16"),

    /**
     * 
     * 						Ente previdenza periti industriali e periti industriali laureati (EPPI)
     * 					
     * 
     */
    @XmlEnumValue("TC17")
    TC_17("TC17"),

    /**
     * 
     * 						Ente previdenza e assistenza pluricategoriale (EPAP)
     * 					
     * 
     */
    @XmlEnumValue("TC18")
    TC_18("TC18"),

    /**
     * 
     * 						Ente nazionale previdenza e assistenza biologi (ENPAB)
     * 					
     * 
     */
    @XmlEnumValue("TC19")
    TC_19("TC19"),

    /**
     * 
     * 						Ente nazionale previdenza e assistenza professione infermieristica (ENPAPI)
     * 					
     * 
     */
    @XmlEnumValue("TC20")
    TC_20("TC20"),

    /**
     * 
     * 						Ente nazionale previdenza e assistenza psicologi (ENPAP)
     * 					
     * 
     */
    @XmlEnumValue("TC21")
    TC_21("TC21"),

    /**
     * 
     * 						INPS
     * 					
     * 
     */
    @XmlEnumValue("TC22")
    TC_22("TC22");
    private final String value;

    TipoCassaType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TipoCassaType fromValue(String v) {
        for (TipoCassaType c: TipoCassaType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
