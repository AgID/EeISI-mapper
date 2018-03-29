/**
 * This package offers various conversion services that can be useful in multiple scenarios
 * where data should be converted between formats.
 *
 * The main services are:
 * <ul>
 *     <li>{@link it.infocert.eigor.api.conversion.converter.TypeConverter}: a generic service that is able to convert a value to a different format.</li>
 *     <li>Lots of basic {@link it.infocert.eigor.api.conversion.converter.TypeConverter converters} such as:
 *      <ul>
 *          <li>{@link it.infocert.eigor.api.conversion.converter.StringToDoublePercentageConverter percentages as "%25" to corresponding double value};</li>
 *          <li>{@link it.infocert.eigor.api.conversion.converter.CountryNameToIso31661CountryCodeConverter country codes as "DK" to corresponding} country {@link it.infocert.eigor.model.core.enums.Iso31661CountryCodes#DK Denmark}.</li>
 *      </ul>
 *     </li>
 *     <li>{@link it.infocert.eigor.api.conversion.ConversionRegistry}: that automatically tries to convert a value delegating to multiple {@link it.infocert.eigor.api.conversion.converter.TypeConverter converters}.</li>
 * </ul>
 *
 */
package it.infocert.eigor.api.conversion;