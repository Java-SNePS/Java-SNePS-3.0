package tests;

import org.junit.Test;

import junit.framework.TestCase;
import sneps.snepslog.AP;

public class SnepslogTest extends TestCase {

	@Test
    public void testClearInfer1() {
		try{
		      AP.executeSnepslogCommand("clear-infer");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testClearInfer2() {
		try{
		      AP.executeSnepslogCommand("clear-infer.");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testClearKB1() {
		try{
		      AP.executeSnepslogCommand("clearkb");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testClearKB2() {
		try{
		      AP.executeSnepslogCommand("clearkb.");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testCopyright1() {
		try{
		      AP.executeSnepslogCommand("copyright");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testCopyright2() {
		try{
		      AP.executeSnepslogCommand("copyright.");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testSetModeOne1() {
		try{
		      AP.executeSnepslogCommand("set-mode-1");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testSetModeOne2() {
		try{
		      AP.executeSnepslogCommand("set-mode-1.");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testSetModeTwo1() {
		try{
		      AP.executeSnepslogCommand("set-mode-2");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testSetModeTwo2() {
		try{
		      AP.executeSnepslogCommand("set-mode-2.");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testSetModeThree1() {
		try{
		      AP.executeSnepslogCommand("set-mode-3");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testSetModeThree2() {
		try{
		      AP.executeSnepslogCommand("set-mode-3.");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
}
