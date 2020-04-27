package com.ctgu.callCharge;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;


public class CallChargeTest {
    private CallCharge callCharge;

    public CallChargeTest() {
        this.callCharge = new CallCharge();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/弱一般等价类测试.csv", numLinesToSkip = 1)
    public void weakGeneral(int num, String startTime, String endTime, boolean isTransform, double pay) throws ParseException {
        callCharge.setTransform(isTransform);
        assertEquals(pay, callCharge.charge(startTime, endTime), 0.01);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/强一般等价类测试.csv", numLinesToSkip = 1)
    public void strongGeneral(int num, String startTime, String endTime, boolean isTransform, double pay) throws ParseException {
        callCharge.setTransform(isTransform);
        assertEquals(pay, callCharge.charge(startTime, endTime), 0.01);
    }

}