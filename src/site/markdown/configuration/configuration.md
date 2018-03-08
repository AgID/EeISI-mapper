## Configuration

Eigor can be configured in several ways.

Mapping and validation files for each converter, including link to latest develop version, below.


### UBL => CEN Converter

Converts from UBL invoices to the CEN semantic model.

#### Mapping

* One to one field mapping defined in: 
converterdata/converter-ubl-cen/mappings/[one_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-ubl-cen/src/main/resources/converterdata/converter-ubl-cen/mappings/one_to_one.properties)

* One to many field mapping defined in: 
converterdata/converter-ubl-cen/mappings/[one_to_many.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-ubl-cen/src/main/resources/converterdata/converter-ubl-cen/mappings/one_to_many.properties)

* Many to one field mapping defined in: 
converterdata/converter-ubl-cen/mappings/[many_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-ubl-cen/src/main/resources/converterdata/converter-ubl-cen/mappings/many_to_one.properties)

* Uses custom converters defined in: 
converterdata/converter-ubl-cen/mappings/[custom.conf](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-ubl-cen/src/main/resources/converterdata/converter-ubl-cen/mappings/custom.conf)

#### Validation

* XSD validation defined in: 
converterdata/converter-commons/ubl/xsd/[UBL-Invoice-2.1.xsd](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-commons/src/main/resources/converterdata/converter-commons/ubl/xsd/UBL-Invoice-2.1.xsd)

* Schematron validation defined in: 
converter-commons/ubl/[schematron](https://gitlab.com/tgi-infocert-eigor/eigor/tree/develop/converter-commons/src/main/resources/converterdata/converter-commons/ubl/schematron) ([schematron updating](schematron.html))

* CIUS Schematron validation defined in: 
converterdata/converter-commons/ubl/cius/[schematron](https://gitlab.com/tgi-infocert-eigor/eigor/tree/develop/converter-commons/src/main/resources/converterdata/converter-commons/ubl/cius/schematron) ([schematron updating](schematron.html))


### CEN => UBL Converter

Converts from the CEN semantic model to UBL invoice.

#### Mapping

* One to one field mapping defined in: 
converterdata/converter-cen-ubl/mappings/[one_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cen-ubl/src/main/resources/converterdata/converter-cen-ubl/mappings/one_to_one.properties)

* One to many field mapping defined in: 
converterdata/converter-cen-ubl/mappings/[one_to_many.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cen-ubl/src/main/resources/converterdata/converter-cen-ubl/mappings/one_to_many.properties)

* Many to one field mapping defined in: 
converterdata/converter-cen-ubl/mappings/[many_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cen-ubl/src/main/resources/converterdata/converter-cen-ubl/mappings/many_to_one.properties)

* Uses custom converters defined in: 
converterdata/converter-cen-ubl/mappings/[custom.conf](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cen-ubl/src/main/resources/converterdata/converter-cen-ubl/mappings/custom.conf)

#### Validation

* XSD validation defined in: 
converterdata/converter-commons/ubl/xsd/[UBL-Invoice-2.1.xsd](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-commons/src/main/resources/converterdata/converter-commons/ubl/xsd/UBL-Invoice-2.1.xsd)

* Schematron validation defined in: 
converter-commons/ubl/[schematron](https://gitlab.com/tgi-infocert-eigor/eigor/tree/develop/converter-commons/src/main/resources/converterdata/converter-commons/ubl/schematron) ([schematron updating](schematron.html))


### UBL CN => CEN Converter

Converts from UBL credit notes to the CEN semantic model.

#### Mapping

* One to one field mapping defined in: 
converterdata/converter-ublcn-cen/mappings/[one_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-ublcn-cen/src/main/resources/converterdata/converter-ublcn-cen/mappings/one_to_one.properties)

* One to many field mapping defined in: 
converterdata/converter-ublcn-cen/mappings/[one_to_many.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-ublcn-cen/src/main/resources/converterdata/converter-ublcn-cen/mappings/one_to_many.properties)

* Many to one field mapping defined in: 
converterdata/converter-ublcn-cen/mappings/[many_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-ublcn-cen/src/main/resources/converterdata/converter-ublcn-cen/mappings/many_to_one.properties)

* Uses custom converters defined in: 
converterdata/converter-ublcn-cen/mappings/[custom.conf](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-ublcn-cen/src/main/resources/converterdata/converter-ublcn-cen/mappings/custom.conf)

#### Validation

* XSD validation defined in: 
converterdata/converter-commons/ublcn/xsd/[UBL-CreditNote-2.1.xsd](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-commons/src/main/resources/converterdata/converter-commons/ublcn/xsd/UBL-CreditNote-2.1.xsd)

* Schematron validation defined in: 
converter-commons/ubl/[schematron](https://gitlab.com/tgi-infocert-eigor/eigor/tree/develop/converter-commons/src/main/resources/converterdata/converter-commons/ubl/schematron) ([schematron updating](schematron.html))

* CIUS Schematron validation defined in: 
converterdata/converter-commons/ubl/cius/[schematron](https://gitlab.com/tgi-infocert-eigor/eigor/tree/develop/converter-commons/src/main/resources/converterdata/converter-commons/ubl/cius/schematron) ([schematron updating](schematron.html))


### CEN => UBL CN Converter

Converts from the CEN semantic model to UBL credit note.

#### Mapping

* One to one field mapping defined in: 
converterdata/converter-ublcn-cen/mappings/[one_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-ublcn-cen/src/main/resources/converterdata/converter-ublcn-cen/mappings/one_to_one.properties)

* One to many field mapping defined in: 
converterdata/converter-ublcn-cen/mappings/[one_to_many.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-ublcn-cen/src/main/resources/converterdata/converter-ublcn-cen/mappings/one_to_many.properties)

* Many to one field mapping defined in: 
converterdata/converter-ublcn-cen/mappings/[many_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-ublcn-cen/src/main/resources/converterdata/converter-ublcn-cen/mappings/many_to_one.properties)

* Uses custom converters defined in: 
converterdata/converter-ublcn-cen/mappings/[custom.conf](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-ublcn-cen/src/main/resources/converterdata/converter-ublcn-cen/mappings/custom.conf)

#### Validation

* XSD validation defined in: 
converterdata/converter-commons/ublcn/xsd/[UBL-CreditNote-2.1.xsd](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-commons/src/main/resources/converterdata/converter-commons/ublcn/xsd/UBL-CreditNote-2.1.xsd)

* Schematron validation defined in: 
converter-commons/ubl/[schematron](https://gitlab.com/tgi-infocert-eigor/eigor/tree/develop/converter-commons/src/main/resources/converterdata/converter-commons/ubl/schematron) ([schematron updating](schematron.html))


### CII => CEN Converter

Converts from Cross Industry Invoice to the CEN semantic model.

#### Mapping

* One to one field mapping defined in: 
converterdata/converter-cii-cen/mappings/[one_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cii-cen/src/main/resources/converterdata/converter-cii-cen/mappings/one_to_one.properties)

* One to many field mapping defined in: 
converterdata/converter-cii-cen/mappings/[one_to_many.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cii-cen/src/main/resources/converterdata/converter-cii-cen/mappings/one_to_many.properties)

* Many to one field mapping defined in: 
converterdata/converter-cii-cen/mappings/[many_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cii-cen/src/main/resources/converterdata/converter-cii-cen/mappings/many_to_one.properties)

* Uses custom converters defined in: 
converterdata/converter-cii-cen/mappings/[custom.conf](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cii-cen/src/main/resources/converterdata/converter-cii-cen/mappings/custom.conf)

#### Validation

* XSD validation defined in: 
converterdata/converter-commons/cii/xsd/uncoupled/data/standard/[CrossIndustryInvoice_100pD16B.xsd](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-commons/src/main/resources/converterdata/converter-commons/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd)

* Schematron validation defined in: 
converter-commons/cii/[schematron](https://gitlab.com/tgi-infocert-eigor/eigor/tree/develop/converter-commons/src/main/resources/converterdata/converter-commons/cii/schematron) ([schematron updating](schematron.html))

* CIUS Schematron validation defined in: 
converter-commons/cii/cius/[schematron](https://gitlab.com/tgi-infocert-eigor/eigor/tree/develop/converter-commons/src/main/resources/converterdata/converter-commons/cii/cius/schematron) ([schematron updating](schematron.html))


### CEN => CII Converter

Converts from the CEN semantic model to Cross Industry Invoice.

#### Mapping

* One to one field mapping defined in: 
converterdata/converter-cen-cii/mappings/[one_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cen-cii/src/main/resources/converterdata/converter-cen-cii/mappings/one_to_one.properties)

* One to many field mapping defined in: 
converterdata/converter-cen-cii/mappings/[one_to_many.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cen-cii/src/main/resources/converterdata/converter-cen-cii/mappings/one_to_many.properties)

* Many to one field mapping defined in: 
converterdata/converter-cen-cii/mappings/[many_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cen-cii/src/main/resources/converterdata/converter-cen-cii/mappings/many_to_one.properties)

* Uses custom converters defined in: 
converterdata/converter-cen-cii/mappings/[custom.conf](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cen-cii/src/main/resources/converterdata/converter-cen-cii/mappings/custom.conf)

#### Validation

* XSD validation defined in: 
converterdata/converter-commons/cii/xsd/uncoupled/data/standard/[CrossIndustryInvoice_100pD16B.xsd](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-commons/src/main/resources/converterdata/converter-commons/cii/xsd/uncoupled/data/standard/CrossIndustryInvoice_100pD16B.xsd)

* Schematron validation defined in: 
converter-commons/cii/[schematron](https://gitlab.com/tgi-infocert-eigor/eigor/tree/develop/converter-commons/src/main/resources/converterdata/converter-commons/cii/schematron) ([schematron updating](schematron.html))


### FattPa => CEN Converter

Converts from FatturaPA format to the CEN semantic model.

#### Mapping

* One to one field mapping defined in: 
converterdata/converter-fattpa-cen/mappings/[one_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-fattpa-cen/src/main/resources/converterdata/converter-fattpa-cen/mappings/one_to_one.properties)

* One to many field mapping defined in: 
converterdata/converter-fattpa-cen/mappings/[one_to_many.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-fattpa-cen/src/main/resources/converterdata/converter-fattpa-cen/mappings/one_to_many.properties)

* Many to one field mapping defined in: 
converterdata/converter-fattpa-cen/mappings/[many_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-fattpa-cen/src/main/resources/converterdata/converter-fattpa-cen/mappings/many_to_one.properties)

* Uses custom converters defined in: 
converterdata/converter-fattpa-cen/mappings/[custom.conf](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-fattpa-cen/src/main/resources/converterdata/converter-fattpa-cen/mappings/custom.conf)

#### Validation

* XSD validation defined in: 
converterdata/converter-commons/fattpa/xsd/[Schema_del_file_xml_FatturaPA_versione_1.2.xsd](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-commons/src/main/resources/converterdata/converter-commons/fattpa/xsd/Schema_del_file_xml_FatturaPA_versione_1.2.xsd)


### CEN => FattPa Converter

Converts from the CEN semantic model to the FatturaPA format.

#### Mapping

* One to one field mapping defined in: 
converterdata/converter-cen-fattpa/mappings/[one_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cen-fattpa/src/main/resources/converterdata/converter-cen-fattpa/mappings/one_to_one.properties)

* One to many field mapping defined in: 
converterdata/converter-cen-fattpa/mappings/[one_to_many.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cen-fattpa/src/main/resources/converterdata/converter-cen-fattpa/mappings/one_to_many.properties)

* Many to one field mapping defined in: 
converterdata/converter-cen-fattpa/mappings/[many_to_one.properties](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cen-fattpa/src/main/resources/converterdata/converter-cen-fattpa/mappings/many_to_one.properties)

* Uses custom converters defined in: 
converterdata/converter-cen-fattpa/mappings/[custom.conf](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-cen-fattpa/src/main/resources/converterdata/converter-cen-fattpa/mappings/custom.conf)

#### Validation

* XSD validation defined in: 
converterdata/converter-commons/fattpa/xsd/[Schema_del_file_xml_FatturaPA_versione_1.2.xsd](https://gitlab.com/tgi-infocert-eigor/eigor/blob/develop/converter-commons/src/main/resources/converterdata/converter-commons/fattpa/xsd/Schema_del_file_xml_FatturaPA_versione_1.2.xsd)


### CEN CSV => CEN Converter

Converts from .CSV files into CEN semantic model.
No need of configurations, this module is used only for testing.