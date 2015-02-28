package org.mifosplatform.useradministration.domain;

import com.google.gson.JsonParser;
import org.junit.Test;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class PermissionExpressionTest {
    private static final Logger logger = LoggerFactory.getLogger(AppUser.class);

    private JsonParser p = new JsonParser();

    @Test
    public void testCreateLoan() {
        Boolean result = eval(
                "json.get(\"principal\").getAsDouble() >= 0 && json.get(\"principal\").getAsDouble() <= 500000",
                parse("{\"productId\":\"51\",\"principal\":500000,\"numberOfRepayments\":12,\"interestRatePerPeriod\":24,\"repaymentEvery\":1,\"loanTermFrequency\":12,\"loanTermFrequencyType\":2,\"repaymentFrequencyType\":2,\"amortizationType\":1,\"interestCalculationPeriodType\":1,\"interestType\":1,\"transactionProcessingStrategyId\":8,\"expectedDisbursementDate\":\"28/2/2015\",\"loanOfficerId\":\"7\",\"submittedOnDate\":\"2/2/2015\",\"locale\":\"en\",\"dateFormat\":\"dd/MM/yyyy\",\"loanType\":\"individual\",\"clientId\":\"154\"}"));

        assertTrue(result);
    }

    private Boolean eval(String expression, Map<String, Object> vars) {
        return (Boolean) MVEL.eval(expression, vars);
    }

    private Map<String, Object> parse(String json) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("json", p.parse(json));
        return vars;
    }
}
