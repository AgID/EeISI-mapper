<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="syntax" id="CIUS-IT">
<param name="CIUS-VD-57_CONTEXT" value="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementPaymentMeans/ram:PayeePartyCreditorFinancialAccount"/>
<param name="CIUS-VD-57" value="string-length(ram:IBANID) &lt;= 34 and string-length(ram:IBANID) &gt;= 15"/>
</pattern>