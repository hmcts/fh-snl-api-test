package uk.gov.hmcts.futurehearings.snl.acceptance.common.verify.error;

import static org.junit.jupiter.api.Assertions.assertEquals;

import uk.gov.hmcts.futurehearings.snl.acceptance.common.dto.SNLDto;

import java.util.Map;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("HMICommonErrorVerifier")
public class SNLCommonErrorVerifier implements SNLErrorVerifier {
    public void verify(SNLDto snlDTO,
                       Response response) {
        log.info("Response" + response.getBody().asString());
        uk.gov.hmcts.futurehearings.snl.acceptance.common.verify.dto.SNLDTO snlVerificationDTO = null;
        if (snlDTO instanceof uk.gov.hmcts.futurehearings.snl.acceptance.common.verify.dto.SNLDTO) {
            snlVerificationDTO = (uk.gov.hmcts.futurehearings.snl.acceptance.common.verify.dto.SNLDTO) snlDTO;
        }
        assertEquals(snlVerificationDTO.httpStatus().value(), response.statusCode());
        assertEquals(3, response.getBody().jsonPath().getMap("$").size());
        Map<String, ?> responseMap = response.getBody().jsonPath().getMap("$");
        assertEquals(snlVerificationDTO.errorCode(), responseMap.get("errCode"));
        assertEquals(snlVerificationDTO.errorDescription(), responseMap.get(("errorDesc")));

    }
}
