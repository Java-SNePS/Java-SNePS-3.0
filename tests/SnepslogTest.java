package tests;

import org.junit.Test;

import junit.framework.TestCase;
import sneps.snepslog.AP;

public class SnepslogTest extends TestCase {

	@Test
    public void testActivate() {
		try{
		      AP.executeSnepslogCommand("activate dog(Fido)");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testActivateBang() {
		try{
		      AP.executeSnepslogCommand("activate! dog(Fido)");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testAddToContext() {
		try{
		      AP.executeSnepslogCommand("add-to-context mythology {winged(Pegasus), dog(Fido), winged(Fido)}");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testAsk() {
		try{
		      AP.executeSnepslogCommand("ask dog(Fido)");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testAskIfNot() {
		try{
		      AP.executeSnepslogCommand("askifnot dog(Fido)");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testAskWh() {
		try{
		      AP.executeSnepslogCommand("askwh dog(Fido)");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testAskWhNot() {
		try{
		      AP.executeSnepslogCommand("askwhnot dog(Fido)");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testBrMode() {
		try{
		      AP.executeSnepslogCommand("br-mode");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testBrModeAuto() {
		try{
		      AP.executeSnepslogCommand("br-mode auto");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testBrModeManual() {
		try{
		      AP.executeSnepslogCommand("br-mode manual");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testClearInfer() {
		try{
		      AP.executeSnepslogCommand("clear-infer");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testClearKB() {
		try{
		      AP.executeSnepslogCommand("clearkb");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testCopyright() {
		try{
		      AP.executeSnepslogCommand("copyright");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testSetModeOne() {
		try{
		      AP.executeSnepslogCommand("set-mode-1");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testSetModeTwo() {
		try{
		      AP.executeSnepslogCommand("set-mode-2");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testSetModeThree() {
		try{
		      AP.executeSnepslogCommand("set-mode-3");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
}
