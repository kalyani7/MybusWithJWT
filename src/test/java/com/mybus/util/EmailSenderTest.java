package com.mybus.util;

import com.mybus.controller.AbstractControllerIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by srinikandula on 3/28/17.
 */
public class EmailSenderTest extends AbstractControllerIntegrationTest {

    @Autowired
    private EmailSender emailSender;

    @Test
    public void testSendEmail() throws Exception {
        //emailSender.sendEmail("kandula.srinivasarao@gmail.com", "Test email", "test");
    }
}