package uk.gov.hmcts.futurehearings.snl.acceptance.common.test;

import static io.restassured.config.EncoderConfig.encoderConfig;
import static uk.gov.hmcts.futurehearings.snl.acceptance.common.helper.CommonHeaderHelper.createCompletePayloadHeader;
import static uk.gov.hmcts.futurehearings.snl.acceptance.common.helper.CommonHeaderHelper.createHeaderWithAcceptTypeAtSystemValue;
import static uk.gov.hmcts.futurehearings.snl.acceptance.common.helper.CommonHeaderHelper.createHeaderWithAllValuesEmpty;
import static uk.gov.hmcts.futurehearings.snl.acceptance.common.helper.CommonHeaderHelper.createHeaderWithAllValuesNull;
import static uk.gov.hmcts.futurehearings.snl.acceptance.common.helper.CommonHeaderHelper.createHeaderWithCorruptedHeaderKey;
import static uk.gov.hmcts.futurehearings.snl.acceptance.common.helper.CommonHeaderHelper.createHeaderWithDeprecatedHeaderValue;
import static uk.gov.hmcts.futurehearings.snl.acceptance.common.helper.CommonHeaderHelper.createHeaderWithDestinationSystemValue;
import static uk.gov.hmcts.futurehearings.snl.acceptance.common.helper.CommonHeaderHelper.createHeaderWithRemovedHeaderKey;
import static uk.gov.hmcts.futurehearings.snl.acceptance.common.helper.CommonHeaderHelper.createHeaderWithRequestCreatedAtSystemValue;
import static uk.gov.hmcts.futurehearings.snl.acceptance.common.helper.CommonHeaderHelper.createHeaderWithRequestProcessedAtSystemValue;
import static uk.gov.hmcts.futurehearings.snl.acceptance.common.helper.CommonHeaderHelper.createHeaderWithRequestTypeAtSystemValue;
import static uk.gov.hmcts.futurehearings.snl.acceptance.common.helper.CommonHeaderHelper.createHeaderWithSourceSystemValue;
import static uk.gov.hmcts.futurehearings.snl.acceptance.common.helper.CommonHeaderHelper.createStandardPayloadHeader;
import static uk.gov.hmcts.futurehearings.snl.acceptance.common.helper.CommonHeaderHelper.createStandardPayloadHeaderWithDuplicateValues;

import uk.gov.hmcts.futurehearings.snl.acceptance.common.TestingUtils;
import uk.gov.hmcts.futurehearings.snl.acceptance.common.delegate.CommonDelegate;
import uk.gov.hmcts.futurehearings.snl.acceptance.common.delegate.dto.DelegateDTO;
import uk.gov.hmcts.futurehearings.snl.acceptance.common.verify.dto.SNLDTO;
import uk.gov.hmcts.futurehearings.snl.acceptance.common.verify.error.SNLErrorVerifier;
import uk.gov.hmcts.futurehearings.snl.acceptance.common.verify.success.SNLSuccessVerifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.restassured.RestAssured;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@Slf4j
@Setter
@Getter
@SuppressWarnings("java:S5786")
public abstract class SNLCommonHeaderTest {


    private static final String INPUT_FILE_PATH = "uk/gov/hmcts/futurehearings/snl/acceptance/%s/input";

    private String apiSubscriptionKey;
    private String authorizationToken;
    private String relativeURL;
    private String relativeURLForNotFound;
    private HttpMethod httpMethod;
    private HttpStatus httpSucessStatus;
    private String inputFileDirectory;
    private String outputFileDirectory;
    private String inputPayloadFileName;
    private Map<String, String> urlParams;

    @Autowired(required = false)
    public CommonDelegate commonDelegate;

    public SNLSuccessVerifier hmiSuccessVerifier;

    public SNLErrorVerifier hmiErrorVerifier;

    @BeforeAll
    public void beforeAll(TestInfo info) {
        log.debug("Test execution Class Initiated: " + info.getTestClass().get().getName());
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        log.debug("Before execution : " + info.getTestMethod().get().getName());
    }

    @AfterEach
    public void afterEach(TestInfo info) {
        log.debug("After execution : " + info.getTestMethod().get().getName());
    }

    @AfterAll
    public void afterAll(TestInfo info) {
        log.debug("Test execution Class Completed: " + info.getTestClass().get().getName());
    }

    @Test
    @DisplayName("Successfully validated response with all the header values")
    public void test_successful_response_with_a_complete_header() throws Exception {

        DelegateDTO delegateDTO = DelegateDTO.builder()
                .targetSubscriptionKey(getApiSubscriptionKey()).authorizationToken(getAuthorizationToken())
                .targetURL(getRelativeURL())
                .inputPayload(TestingUtils.readFileContents(String.format(INPUT_FILE_PATH, getInputFileDirectory()) +
                        "/" + getInputPayloadFileName()))
                .standardHeaderMap(createCompletePayloadHeader(getApiSubscriptionKey()))
                .headers(null)
                .params(getUrlParams())
                .httpMethod(getHttpMethod())
                .status(getHttpSucessStatus())
                .build();
        commonDelegate.test_expected_response_for_supplied_header(
                delegateDTO,
                getHmiSuccessVerifier(),
                new SNLDTO(HttpStatus.ACCEPTED,null,null,null));
    }

    @Test
    @DisplayName("Successfully validated response with mandatory header values")
    public void test_successful_response_with_a_mandatory_header() throws Exception {

        DelegateDTO delegateDTO = DelegateDTO.builder()
                .targetSubscriptionKey(getApiSubscriptionKey()).authorizationToken(getAuthorizationToken())
                .targetURL(getRelativeURL())
                .inputPayload(TestingUtils.readFileContents(String.format(INPUT_FILE_PATH, getInputFileDirectory()) +
                        "/" + getInputPayloadFileName()))
                .standardHeaderMap(createStandardPayloadHeader(getApiSubscriptionKey()))
                .headers(null)
                .params(getUrlParams())
                .httpMethod(getHttpMethod())
                .status(getHttpSucessStatus())
                .build();

        commonDelegate.test_expected_response_for_supplied_header(delegateDTO,
                getHmiSuccessVerifier(),
                new SNLDTO(HttpStatus.ACCEPTED,null,null,null));
    }


   /* @Test
    @DisplayName("Successfully validated response with a valid payload but a ,charset appended to the Content-Type")
    @Disabled("Initial Setup")
    void test_successful_response_for_content_type_with_charset_appended() throws Exception {
        RestAssured.config = RestAssured.config()
                .encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(true));
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                createStandardPayloadHeader(getApiSubscriptionKey()),
                null,
                getUrlParams(),
                getHttpMethod(),
                getHttpSucessStatus(),
                getHmiSuccessVerifier(),
                new SNLDTO(HttpStatus.ACCEPTED,null,null,null));
        RestAssured.config = RestAssured.config()
                .encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false));
    }


    @Test
    @DisplayName("API call with Standard Header but slight Error URL")
    @Disabled("Initial Setup")
    void test_invalid_URL() throws Exception {
        commonDelegate.test_expected_response_for_supplied_header(
                getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURLForNotFound(),
                //Performed a near to the Real URL Transformation
                getInputPayloadFileName(),
                createStandardPayloadHeader(getApiSubscriptionKey()),
                null,
                getUrlParams(),
                getHttpMethod(),
                HttpStatus.NOT_FOUND,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.NOT_FOUND,null,null,null));
    }
*/
    /*@Test
    @DisplayName("Successfully validated response with mandatory header values")
    void test_successful_response_with_a_mandatory_header() throws Exception {

        String inputPayload = TestingUtils.readFileContents(String.format(INPUT_FILE_PATH, getInputFileDirectory()) + "/" + getInputPayloadFileName());
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), inputPayload,
                createStandardPayloadHeader(getApiSubscriptionKey()),
                null,
                getUrlParams(),
                getHttpMethod(),
                getHttpSucessStatus(),
                getHmiSuccessVerifier(),
                new SNLDTO(HttpStatus.ACCEPTED,null,null,null));
    }

    @Test
    @DisplayName("Successfully validated response with an empty payload")
    @Disabled("Initial Setup")
    void test_successful_response_for_empty_json_body() throws Exception {
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), "empty-json-payload.json",
                createStandardPayloadHeader(getApiSubscriptionKey()),
                null,
                getUrlParams(),
                getHttpMethod(),
                getHttpSucessStatus(),
                getHmiSuccessVerifier(),
                new SNLDTO(HttpStatus.ACCEPTED,null,null,null));
    }

    @Test
    @DisplayName("API call with Standard Header but unimplemented METHOD")
    @Disabled("Initial Setup")
    void test_invalid_REST_method() throws Exception {
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(),
                getInputPayloadFileName(),
                createStandardPayloadHeader(getApiSubscriptionKey()),
                null,
                getUrlParams(),
                HttpMethod.OPTIONS,
                HttpStatus.NOT_FOUND,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.NOT_FOUND,null,null,null));
    }


    @Test
    @DisplayName("Headers with all empty and null values")
    @Disabled("Initial Setup")
    void test_no_headers_populated() throws Exception {
        //2 Sets of Headers Tested - Nulls and Empty
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(),
                getInputPayloadFileName(),
                createHeaderWithAllValuesEmpty(),
                //The Content Type Has to be Populated for Rest Assured to function properly
                //So this Test was manually executed in Postman Manually as well with the same Order Number
                null,
                getUrlParams(),
                getHttpMethod(),
                HttpStatus.UNAUTHORIZED,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.UNAUTHORIZED,null,null,null));

        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(),
                getInputPayloadFileName(),
                createHeaderWithAllValuesNull(),
                //The Content Type Has to be Populated for Rest Assured to function properly
                //So this Test was manually executed in Postman Manually as well with the same Order Number
                null,
                getUrlParams(),
                getHttpMethod(),
                HttpStatus.UNAUTHORIZED,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.UNAUTHORIZED,null,null,null));
    }


    @Test
    @DisplayName("Subscription Key Truncated in the Header")
    @Disabled("Initial Setup")
    void test_subscriptionkey_key_truncated() throws Exception {
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                createHeaderWithCorruptedHeaderKey(getApiSubscriptionKey(),
                        Arrays.asList("Ocp-Apim-Subscription-Key")), null,
                getUrlParams(), getHttpMethod(),
                HttpStatus.UNAUTHORIZED,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.UNAUTHORIZED,null,null,null));
    }

    @Test
    @DisplayName("Subscription Key Value Truncated in the Header")
    @Disabled("Initial Setup")
    void test_subscriptionkey_value_truncated() throws Exception {
        commonDelegate.test_expected_response_for_supplied_header(
                getApiSubscriptionKey().substring(0, getApiSubscriptionKey().length() - 1),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                createStandardPayloadHeader("  "),
                null,
                getUrlParams(),
                getHttpMethod(),
                HttpStatus.UNAUTHORIZED,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.UNAUTHORIZED,null,null,null));
    }


    @ParameterizedTest(name = "Subscription Key with invalid values  - Param : {0} --> {1}")
    @CsvSource(value = {"Null_Value, null", "Empty_Space,''", "Tab, \"\\t\"", "Newline, \"\\n\"", "Wrong_Value,c602c8ed3b8147be910449b563dce008"}, nullValues = "NIL")
    @Disabled("Initial Setup")
    void test_subscription_key_invalid_values(String subKey, String subKeyVal) throws Exception {
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                createStandardPayloadHeader(subKeyVal),
                null,
                getUrlParams(),
                getHttpMethod(),
                HttpStatus.UNAUTHORIZED,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.UNAUTHORIZED,null,null,null));
    }


    @ParameterizedTest(name = "Source System Header invalid values - Param : {0} --> {1}")
    @CsvSource(value = {"Null_Value, NIL", "Empty_Space,''", "Invalid_Value, SNL", "Invalid_Source_System, S&L"}, nullValues = "NIL")
    @Disabled("Initial Setup")
    void test_source_system_invalid_values(String sourceSystemKey, String sourceSystemVal) throws Exception {
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                createHeaderWithSourceSystemValue(getApiSubscriptionKey(), sourceSystemVal),
                null,
                getUrlParams(),
                getHttpMethod(),
                HttpStatus.BAD_REQUEST,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.UNAUTHORIZED,null,null,null));
    }


    @ParameterizedTest(name = "Destination System Header with invalid values - Param : {0} --> {1}")
    @CsvSource(value = {"Null_Value, NIL", "Empty_Space,''", "Invalid_Value, SNL", "Invalid_Destination_System, CFT"}, nullValues = "NIL")
    @Disabled("Initial Setup")
    void test_destination_system_invalid_values(String destinationSystemKey, String destinationSystemVal) throws Exception {
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                createHeaderWithDestinationSystemValue(getApiSubscriptionKey(), destinationSystemVal),
                null,
                getUrlParams(),
                getHttpMethod(),
                HttpStatus.BAD_REQUEST,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.BAD_REQUEST,null,null,null));
    }


    @ParameterizedTest(name = "Request Created At System Header invalid values - Param : {0} --> {1}")
    @CsvSource({"Null_Value, null", "Empty_Space,\" \"", "Invalid_Value, value",
            "Invalid_Date_Format, 2002-02-31T10:00:30-05:00Z",
            "Invalid_Date_Format, 2002-02-31T1000:30-05:00",
            "Invalid_Date_Format, 2002-02-31T10:00-30-05:00",
            "Invalid_Date_Format, 2002-10-02T15:00:00*05Z",
            "Invalid_Date_Format, 2002-10-02 15:00?0005Z",
            "Invalid_Date_Format, 2002-10-02T15:00:00",
    })
    @Disabled("Initial Setup")
    void test_request_created_at_invalid_values(String requestCreatedAtKey, String requestCreatedAtVal) throws Exception {
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                createHeaderWithRequestCreatedAtSystemValue(getApiSubscriptionKey(), requestCreatedAtVal),
                null,
                getUrlParams(),
                getHttpMethod(),
                HttpStatus.BAD_REQUEST,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.BAD_REQUEST,null,null,null));
    }


    @ParameterizedTest(name = "Mandatory Keys truncated from the Header - Key : {0}")
    @ValueSource(strings = {"Content-Type", "Accept", "Source-System",
            "Destination-System", "Request-Created-At",
            "Request-Processed-At", "Request-Type"})
    @Disabled("Initial Setup")
    void test_header_keys_truncated(String keyToBeTruncated) throws Exception {

        final HttpStatus httpStatus =
                keyToBeTruncated.equalsIgnoreCase("Accept") ? HttpStatus.NOT_ACCEPTABLE : HttpStatus.BAD_REQUEST;
        final String expectedErrorMessage =
                keyToBeTruncated.equalsIgnoreCase("Accept") ||
                        keyToBeTruncated.equalsIgnoreCase("Content-Type") ?
                        "Missing/Invalid Media Type" : "Missing/Invalid Header " + keyToBeTruncated;

        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                createHeaderWithCorruptedHeaderKey(getApiSubscriptionKey(),
                        Arrays.asList(keyToBeTruncated)),
                null,
                getUrlParams(),
                getHttpMethod(),
                httpStatus,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.BAD_REQUEST,null,null,null));
    }


    @ParameterizedTest(name = "Mandatory keys removed from the Header - Key : {0}")
    @ValueSource(strings = {"Content-Type", "Accept", "Source-System",
            "Destination-System", "Request-Created-At",
            "Request-Processed-At", "Request-Type"})
    @Disabled("Initial Setup")
    void test_with_keys_removed_from_header(String keyToBeRemoved) throws Exception {
        final HttpStatus httpStatus = keyToBeRemoved.equalsIgnoreCase("Accept") ? HttpStatus.NOT_ACCEPTABLE : HttpStatus.BAD_REQUEST;
        final String expectedErrorMessage =
                keyToBeRemoved.equalsIgnoreCase("Accept") ||
                        keyToBeRemoved.equalsIgnoreCase("Content-Type") ?
                        "Missing/Invalid Media Type" : "Missing/Invalid Header " + keyToBeRemoved;

        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                createHeaderWithRemovedHeaderKey(getApiSubscriptionKey(),
                        Arrays.asList(keyToBeRemoved)),
                null,
                getUrlParams(),
                getHttpMethod(),
                httpStatus,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.BAD_REQUEST,null,null,null));
    }


    @ParameterizedTest(name = "Request Processed At System Header With Invalid Values - Param : {0} --> {1}")
    @CsvSource(value = {"Null_Value, NIL", "Empty_Space,''", "Invalid_Value, value",
            "Invalid_Date_Format, 2002-02-31T10:00:30-05:00Z",
            "Invalid_Date_Format, 2002-02-31T1000:30-05:00",
            "Invalid_Date_Format, 2002-02-31T10:00-30-05:00",
            "Invalid_Date_Format, 2002-10-02T15:00:00*05Z",
            "Invalid_Date_Format, 2002-10-02 15:00?0005Z",
            "Invalid_Date_Format, 2002-10-02T15:00:00",
    }, nullValues = "NIL")
    @Disabled("Initial Setup")
    void test_request_processed_at_with_invalid_values(String requestProcessedAtKey, String requestProcessedAtVal) throws Exception {
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                createHeaderWithRequestProcessedAtSystemValue(getApiSubscriptionKey(), requestProcessedAtVal),
                null,
                getUrlParams(),
                getHttpMethod(),
                HttpStatus.BAD_REQUEST,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.BAD_REQUEST,null,null,null));
    }


    @ParameterizedTest(name = "Request Type System Header with invalid values - Param : {0} --> {1}")
    @CsvSource(value = {"Null_Value, NIL", "Invalid_Value, Robbery"}, nullValues = "NIL")
    @Disabled("Initial Setup")
    void test_request_type_at_with_invalid_values(String requestTypeKey, String requestTypeVal) throws Exception {
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                createHeaderWithRequestTypeAtSystemValue(getApiSubscriptionKey(), requestTypeVal),
                null,
                getUrlParams(),
                getHttpMethod(),
                HttpStatus.BAD_REQUEST,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.BAD_REQUEST,null,null,null));
    }


    @ParameterizedTest(name = "Accept System Header with invalid format - Param : {0} --> {1}")
    @CsvSource({"Invalid_Value, Random", "Invalid_Format, application/pdf", "Invalid_Format, application/text"})
    @Disabled("Initial Setup")
    void test_accept_at_with_invalid_values(String acceptTypeKey, String acceptTypeVal) throws Exception {
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                createHeaderWithAcceptTypeAtSystemValue(getApiSubscriptionKey(), acceptTypeVal),
                null,
                getUrlParams(),
                getHttpMethod(),
                HttpStatus.NOT_ACCEPTABLE,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.NOT_ACCEPTABLE,null,null,null));
    }


    @ParameterizedTest(name = "Request Type System Header with valid values - Value : {0}")
    @ValueSource(strings = {"Assault", "Theft"})
    @Disabled("Initial Setup")
    void test_request_type_at_with_valid_values(String requestType) throws Exception {
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                createHeaderWithRequestTypeAtSystemValue(getApiSubscriptionKey(), requestType),
                null,
                getUrlParams(),
                getHttpMethod(),
                getHttpSucessStatus(),
                getHmiSuccessVerifier(),
                new SNLDTO(HttpStatus.NOT_ACCEPTABLE,null,null,null));
    }


    @ParameterizedTest(name = "Request Processed At System Header With Valid Date Format - Param : {0} --> {1}")
    @CsvSource({"Valid_Date_Format,2002-10-02T10:00:00-05:00",
            "Valid_Date_Format,2002-10-02T15:00:00Z",
            "Valid_Date_Format,2002-10-02T15:00:00.05Z",
            "Valid_Date_Format,2019-10-12 07:20:50.52Z"
    })
    @Disabled("Initial Setup")
    void test_request_processed_at_with_valid_values(String requestProcessedAtKey, String requestProcessedAtVal) throws Exception {
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                createHeaderWithRequestProcessedAtSystemValue(getApiSubscriptionKey(), requestProcessedAtVal),
                null,
                getUrlParams(),
                getHttpMethod(),
                getHttpSucessStatus(),
                getHmiSuccessVerifier(),
                new SNLDTO(HttpStatus.NOT_ACCEPTABLE,null,null,null));
    }

    @ParameterizedTest(name = "Request Created At System Header With Valid Date Format - Param : {0} --> {1}")
    @CsvSource({"Valid_Date_Format, 2012-03-19T07:22:00Z", "Valid_Date_Format, 2002-10-02T15:00:00Z",
            "Valid_Date_Format, 2002-10-02T15:00:00.05Z",
            "Valid_Date_Format, 2019-10-12 07:20:50.52Z"})
    @Disabled("Initial Setup")
    void test_request_created_at_with_valid_values(String requestCreatedAtKey, String requestCreatedAtVal) throws Exception {
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                createHeaderWithRequestCreatedAtSystemValue(getApiSubscriptionKey(), requestCreatedAtVal),
                null,
                getUrlParams(),
                getHttpMethod(),
                getHttpSucessStatus(),
                getHmiSuccessVerifier(),
                new SNLDTO(HttpStatus.NOT_ACCEPTABLE,null,null,null));

    }


    @ParameterizedTest(name = "Deprecated System headers with valid values - Param : {0} --> {1}")
    @CsvSource({"X-Accept, application/json",
            "X-Source-System, CFT",
            "X-Destination-System, S&L",
            "X-Request-Created-At, 2012-03-19T07:22:00Z",
            "X-Request-Processed-At, 2012-03-19T07:22:00Z",
            "X-Request-Type, Assault"
    })
    @Disabled("Initial Setup")
    void test_deprecated_header_values(String deprecatedHeaderKey, String deprecatedHeaderVal) throws Exception {

        final HttpStatus httpStatus = deprecatedHeaderKey.equalsIgnoreCase("X-Accept") ? HttpStatus.NOT_ACCEPTABLE : HttpStatus.BAD_REQUEST;
        final String expectedErrorMessage =
                deprecatedHeaderKey.equalsIgnoreCase("X-Accept") ||
                        deprecatedHeaderKey.equalsIgnoreCase("X-Content-Type") ?
                        "Missing/Invalid Media Type" : "Missing/Invalid Header " + deprecatedHeaderKey.replace("X-", "");
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                createHeaderWithDeprecatedHeaderValue(getApiSubscriptionKey(), deprecatedHeaderKey, deprecatedHeaderVal),
                null,
                getUrlParams(),
                getHttpMethod(),
                httpStatus,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.NOT_ACCEPTABLE,null,null,null));

    }

    @ParameterizedTest(name = "Duplicate System headers with valid values - Param : {0} --> {1}")
    @CsvSource(value = {
            //System Headers of Accept and Content-Type could not be duplicated as Rest Assured seems to remove the Duplication of valid same values.
            //This should be tested manually using Postman.
            "Source-System,NIL","Source-System,''","Source-System,CFT",
            "Destination-System,NIL","Destination-System,''","Destination-System,S&L",
            "Request-Created-At,NIL","Request-Created-At,''","Request-Created-At,2002-10-02T15:00:00Z",
            "Request-Processed-At,NIL","Request-Processed-At,''","Request-Processed-At,2002-10-02T15:00:00Z",
            "Request-Type,NIL","Request-Type,''","Request-Type,THEFT","Request-Type,ASSAULT"
    }, nullValues = "NIL")
    @Disabled("Initial Setup")
    void test_duplicate_headers(String duplicateHeaderKey, String duplicateHeaderValue) throws Exception {

        final String expectedErrorMessage =
                        "Missing/Invalid Header " + duplicateHeaderKey;
        Map<String,String> duplicateHeaderField  = new HashMap<String,String>();
        duplicateHeaderField.put(duplicateHeaderKey,duplicateHeaderValue);
        commonDelegate.test_expected_response_for_supplied_header(getApiSubscriptionKey(),
                getAuthorizationToken(),
                getRelativeURL(), getInputPayloadFileName(),
                null,
                createStandardPayloadHeaderWithDuplicateValues(getApiSubscriptionKey(),
                        duplicateHeaderField),
                getUrlParams(),
                getHttpMethod(),
                HttpStatus.BAD_REQUEST,
                getHmiErrorVerifier(),
                new SNLDTO(HttpStatus.BAD_REQUEST,null,null,null));

    }*/
}