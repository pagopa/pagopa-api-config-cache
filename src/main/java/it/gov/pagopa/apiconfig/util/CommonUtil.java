package it.gov.pagopa.apiconfig.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class CommonUtil {


  /**
   * @param timestamp {@link Timestamp} to convert
   * @return convert timestamp to {@link ZonedDateTime}
   */
  public static ZonedDateTime toZonedDateTime(Timestamp timestamp) {
    return timestamp != null ? ZonedDateTime.of(timestamp.toLocalDateTime(), ZoneOffset.UTC) : null;
  }

  /**
   * @param calendar to convert
   * @return convert calendar to {@link Timestamp}
   */
  public static Timestamp toTimestamp(XMLGregorianCalendar calendar) {
    return Timestamp.from(calendar.toGregorianCalendar().toZonedDateTime().toInstant());
  }

  /**
   * @param ZonedDateTime to convert
   * @return convert ZonedDateTime to {@link Timestamp}
   */
  public static Timestamp toTimestamp(ZonedDateTime ZonedDateTime) {
    return Timestamp.from(ZonedDateTime.toInstant());
  }

  /**
   * @param value value to deNullify.
   * @return return empty string if value is null
   */
  public static String deNull(String value) {
    return Optional.ofNullable(value).orElse("");
  }

  /**
   * @param value value to deNullify.
   * @return return empty string if value is null
   */
  public static String deNull(Object value) {
    return Optional.ofNullable(value).orElse("").toString();
  }

  /**
   * @param value value to deNullify.
   * @return return false if value is null
   */
  public static Boolean deNull(Boolean value) {
    return Optional.ofNullable(value).orElse(false);
  }


  /**
   * @param example filter
   * @return a new Example using the custom ExampleMatcher
   */
  public static <T> Example<T> getFilters(T example) {
    ExampleMatcher matcher = ExampleMatcher.matching()
        .withIgnoreNullValues()
        .withIgnoreCase(true)
        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
    return Example.of(example, matcher);
  }

  /**
   * @param iban an italian IBAN with length 27
   * @return ABI in the IBAN
   */
  public static String getAbiFromIban(String iban) {
    return iban.substring(5, 10);
  }

  /**
   * @param iban an italian IBAN with length 27
   * @return Conto Corrente in the IBAN
   */
  public static String getCcFromIban(String iban) {
    return iban.substring(15, 27);
  }

  /**
   * @param inputStream file xml to validate
   * @param xsdUrl      url of XSD
   * @throws SAXException       if XML is not valid
   * @throws IOException        if XSD schema not found
   * @throws XMLStreamException error during read XML
   */
  public static void syntaxValidation(InputStream inputStream, String xsdUrl)
      throws SAXException, IOException, XMLStreamException {
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    // to be compliant, prohibit the use of all protocols by external entities:
    factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

    javax.xml.validation.Schema schema = factory.newSchema(new URL(xsdUrl));
    Validator validator = schema.newValidator();

    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    // to be compliant, completely disable DOCTYPE declaration:
    xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

    XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);
    StAXSource source = new StAXSource(xmlStreamReader);
    validator.validate(source);
  }

  public static String getExceptionErrors(String stringException) {
    Matcher matcher = Pattern.compile("lineNumber: \\d*").matcher(stringException);
    String lineNumber = "";
    if (matcher.find()) {
      lineNumber = matcher.group(0);
    }
    String detail = stringException.substring(stringException.lastIndexOf(":") + 1).trim();

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(detail);
    if (lineNumber.length() > 0) {
      stringBuilder.append(" Error at ").append(lineNumber);
    }
    return stringBuilder.toString();
  }

  /**
   * @param headers header of the CSV file
   * @param rows    data of the CSV file
   * @return byte array of the CSV using commas (,) as separator
   */
  public static byte[] createCsv(List<String> headers, List<List<String>> rows) {
    var csv = new StringBuilder();
    csv.append(String.join(",", headers));
    rows.forEach(row -> csv.append(System.lineSeparator()).append(String.join(",", row)));
    return csv.toString().getBytes();
  }


  /**
   * @param env environment of the application
   * @return String to help build the url with the correct application environment
   */
  public String getEnvironment(String env) {
    if (env.equals("PROD")) {
      return "";
    }
    return "." + env.toLowerCase();
  }

}
