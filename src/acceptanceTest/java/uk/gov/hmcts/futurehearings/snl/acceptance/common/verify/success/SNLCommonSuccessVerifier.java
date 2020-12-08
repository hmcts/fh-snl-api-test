package uk.gov.hmcts.futurehearings.snl.acceptance.common.verify.success;

import static org.junit.jupiter.api.Assertions.assertEquals;

import uk.gov.hmcts.futurehearings.snl.acceptance.common.dto.SNLDto;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("HMICommonSuccessVerifier")
public class SNLCommonSuccessVerifier implements SNLSuccessVerifier {
    public void verify(SNLDto snlDTO,
                       Response response) {
        log.debug("Response" + response.getBody().asString());
        uk.gov.hmcts.futurehearings.snl.acceptance.common.verify.dto.SNLDTO snlVerificationDTO = null;
        if (snlDTO instanceof uk.gov.hmcts.futurehearings.snl.acceptance.common.verify.dto.SNLDTO){
            snlVerificationDTO = (uk.gov.hmcts.futurehearings.snl.acceptance.common.verify.dto.SNLDTO) snlDTO;
        }
        assertEquals(snlVerificationDTO.httpStatus().value(),response.statusCode());
    }
}
