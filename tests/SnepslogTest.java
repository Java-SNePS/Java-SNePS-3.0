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
    public void testDefineFrame() {
		try{
		      AP.executeSnepslogCommand("define-frame Entity cf (member class)");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testDefinePath() {
		try{
		      AP.executeSnepslogCommand("define-path member forward-unit(member)");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testDefineSemantic() {
		try{
		      AP.executeSnepslogCommand("define-semantic prop");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testDemo() {
		try{
		      AP.executeSnepslogCommand("demo");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testDescribeContext() {
		try{
		      AP.executeSnepslogCommand("describe-context mythology");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testDescribeTerms() {
		try{
		      AP.executeSnepslogCommand("describe-terms");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testExpert() {
		try{
		      AP.executeSnepslogCommand("expert");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testListAssertedWffs() {
		try{
		      AP.executeSnepslogCommand("list-asserted-wffs mythology");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testListContexts() {
		try{
		      AP.executeSnepslogCommand("list-contexts");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testListTerms() {
		try{
		      AP.executeSnepslogCommand("list-terms");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testLoad() {
		try{
		      AP.executeSnepslogCommand("load test.snepslog");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testNormal() {
		try{
		      AP.executeSnepslogCommand("normal");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testPerform() {
		try{
		      AP.executeSnepslogCommand("perform kill(Enemy)");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testSetContext() {
		try{
		      AP.executeSnepslogCommand("set-context mythology");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testSetDefaultContext() {
		try{
		      AP.executeSnepslogCommand("set-default-context mythology");
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
	
	@Test
    public void testShow() {
		try{
		      AP.executeSnepslogCommand("show");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testTrace() {
		try{
		      AP.executeSnepslogCommand("trace parsing");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testUndefinePath() {
		try{
		      AP.executeSnepslogCommand("undefine-path member");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testUnlabeled() {
		try{
		      AP.executeSnepslogCommand("unlabeled");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testUntrace() {
		try{
		      AP.executeSnepslogCommand("untrace parsing");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
	@Test
    public void testOrEntailment() {
		try{
		      AP.executeSnepslogCommand("{in(Hilda, Boston), in(Kathy, Las_Vegas)} v=> {in(Eve, Providence)}.");
		   }
		   catch(Exception e){
		      fail("No exception is expected to be thrown");
		   }
    }
	
}
