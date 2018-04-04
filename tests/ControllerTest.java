package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import sneps.exceptions.DuplicateContextNameException;
import sneps.snebr.Context;
import sneps.snebr.Controller;

public class ControllerTest {

    String testContext;
    @Before
    public void setUp() throws Exception {
        testContext = "test context";
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createContext() {
    }

    @Test
    public void createNewContextWithNoProps() throws DuplicateContextNameException {
       Context expectedContext = Controller.createContext(testContext);
       Context actualContext = Controller.getContextByName(testContext);

       assertEquals(expectedContext, actualContext);
    }

    @Test
    public void createContext2() {
    }

    @Test
    public void removeContext() {
    }

    @Test
    public void addPropToContext() {
    }

    @Test
    public void addPropsToContext() {
    }

    @Test
    public void addPropToCurrentContext() {
    }

    @Test
    public void setCurrentContext() {
    }

    @Test
    public void getCurrentContext() {
    }

    @Test
    public void checkForContradiction() {
    }

    @Test
    public void getContextByName() {
    }
}