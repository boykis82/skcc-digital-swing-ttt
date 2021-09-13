package org.skcc.team1.legacy.customerserver.service;

import org.skcc.team1.legacy.customerserver.exception.TestException;
import org.springframework.stereotype.Service;

@Service
public class TestExceptionService {

    public void testExceptionTestFunction(String message) {
        new TestException("[CUST001] Exception Occur Customer Number: "+ message);
    }

}
