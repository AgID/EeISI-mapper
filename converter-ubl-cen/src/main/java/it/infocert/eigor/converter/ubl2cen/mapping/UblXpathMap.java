package it.infocert.eigor.converter.ubl2cen.mapping;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UblXpathMap {

    private Multimap<String, String> mapping = HashMultimap.create();

    private final List<String> italianPaths = new ArrayList<>(Arrays.asList(
            "//Invoice/ID",
            "//Invoice/IssueDate",
            "//Invoice/InvoiceTypeCode",
            "//Invoice/DocumentCurrencyCode",
            "//Invoice/AccountingSupplierParty/Party/PartyTaxScheme/CompanyID",
            "//Invoice/AccountingSupplierParty/Party/PostalAddress/Country/IdentificationCode"
    ));

    private final List<String> invoicePaths = new ArrayList<>(Arrays.asList(
            "/BT0001",
            "/BT0002",
            "/BT0003",
            "/BT0005",
            "/BT0031",
            "/BT0040"
    ));

    public Multimap<String, String> getMapping() {
        if (mapping.isEmpty()) {
            for (int i = 0; i < italianPaths.size(); i++) {
                mapping.put(invoicePaths.get(i), italianPaths.get(i));
            }
            return mapping;
        } else {
            return mapping;
        }
    }

}