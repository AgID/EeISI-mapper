## Configuration

Eigor can be configured in several ways.


### UBL => CEN Converter

Converts from UBL invoices to the CEN semantic model.

#### Validation

* Schematron validation defined in: 
[schematron folder](https://gitlab.com/tgi-infocert-eigor/eigor/tree/develop/converter-ubl-cen/converterdata/converter-ubl-cen/schematron),
needs rebuild.


#### Mapping

* One to one field mapping defined in:
[one_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-ubl-cen/converterdata/converter-ubl-cen/mappings/one_to_one.properties),
needs restart.



### CEN => FattPa Converter

Converts from the CEN semantic model to the FattPa format.

#### Mapping

* No configuration available yet.

#### Validation

* [XSD](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cen-fattpa/src/main/resources/Schema_del_file_xml_FatturaPA_versione_1.2.xsd)
, needs rebuild.



### CENCSV => CEN Converter

Converts from .CSV files into CEN semantic model.
No need of configurations, this module is used only for testing.



### CEN Validation

Applies rules to be sure the CEN semantic model id correct and satisfy the CEN rules.

* [cardinality rules](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/eigor-rules/src/main/resources/cardinality.properties)
* [business rules](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/eigor-rules/src/main/resources/rules.properties)