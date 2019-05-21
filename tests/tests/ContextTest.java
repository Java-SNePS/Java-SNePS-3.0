package tests;

//import org.junit.Before;
//import org.junit.Test;
//import sneps.network.Network;
//import sneps.network.PropositionNode;
//import sneps.network.classes.setClasses.PropositionSet;
//import sneps.network.classes.term.Base;
//import sneps.snebr.Context;
//import sneps.snebr.Support;
//
//import java.util.HashSet;
//
//import static org.junit.Assert.*;
//import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

//public class ContextTest {
//
//    private Context context;
//    private PropositionNode p;
//
//    @Before
//    public void beforeEach() {
//        context = new Context();
//        context.getNames().add("test");
//        p = new PropositionNode(new Base("ducks quack"));
//        p.setId(3);
//    }
//
//
//    @Test
//    public void addProp() {
//        context.addProp(p);
//        assertEquals(context.getHypothesisSet().iterator().next(), p);
//    }
//
//    @Test
//    public void addProps() {
//    }
//
//    @Test
//    public void removeProp() {
//        context.addProp(p);
//        context.removeProp(p);
//        assertNotEquals(true, context.getHypothesisSet().iterator().hasNext());
//    }
//
////    public void setUpSupport() {
////        Support support = new Support();
////        PropositionNode p = new PropositionNode(new Base("p"));
////        PropositionNode s = new PropositionNode(new Base("s"));
////        PropositionNode q = new PropositionNode(new Base("q"));
////        PropositionNode r = new PropositionNode(new Base("r"));
////        PropositionNode t = new PropositionNode(new Base("t"));
////        PropositionNode u = new PropositionNode(new Base("u"));
////        PropositionNode c = new PropositionNode(new Base("c"));
////        PropositionNode d = new PropositionNode(new Base("d"));
////        PropositionNode g = new PropositionNode(new Base("g"));
////        PropositionNode l = new PropositionNode(new Base("l"));
////
////        Network.getPropositionNodes().put("q", q);
////        Network.getPropositionNodes().put("r", r);
////        Network.getPropositionNodes().put("s", s);
////        Network.getPropositionNodes().put("t", t);
////        Network.getPropositionNodes().put("u", u);
////        Network.getPropositionNodes().put("p", p);
////        Network.getPropositionNodes().put("c", c);
////        Network.getPropositionNodes().put("d", d);
////        Network.getPropositionNodes().put("g", g);
////        Network.getPropositionNodes().put("l", l);
////
////
////        PropositionSet sSupportNodes = new PropositionSet();
////        sSupportNodes.add(t);
////        sSupportNodes.add(u);
////        Support sSupport = new Support();
////        sSupport.addAssumptionBasedSupport(sSupportNodes);
////        s.setBasicSupport(sSupport);
////
////        PropositionSet dSupportNodes = new PropositionSet();
////        dSupportNodes.add(g);
////        dSupportNodes.add(l);
////        Support dSupport = new Support();
////        dSupport.addAssumptionBasedSupport(dSupportNodes);
////        d.setBasicSupport(dSupport);
////
////        PropositionSet pSupportNodes = new PropositionSet();
////        pSupportNodes.add(q);
////        pSupportNodes.add(r);
////        pSupportNodes.add(t);
////        pSupportNodes.add(u);
////        Support pSupport = new Support();
////        pSupport.addAssumptionBasedSupport(sSupportNodes);
////        p.setBasicSupport(pSupport);
////
////        context.addProp(q);
////        context.addProp(t);
////        context.addProp(new PropositionNode(new Base("v")));
////        context.addProp(new PropositionNode(new Base("z")));
////        context.addProp(r);
////        context.addProp(c);
////        context.addProp(g);
////        context.addProp(l);
////    }
//
//    @Test
//    public void allAsserted() {
//        setUpSupport();
////        PropositionSet prop = context.allAsserted();
//        PropositionNode p = Network.getPropositionNodes().get("p");
//        PropositionNode q = Network.getPropositionNodes().get("q");
//        PropositionNode t = Network.getPropositionNodes().get("t");
//        PropositionNode v = Network.getPropositionNodes().get("v");
//        PropositionNode z = Network.getPropositionNodes().get("z");
//        PropositionNode r = Network.getPropositionNodes().get("r");
//        PropositionNode c = Network.getPropositionNodes().get("c");
//        PropositionNode g = Network.getPropositionNodes().get("g");
//        PropositionNode l = Network.getPropositionNodes().get("l");
//
////        assertThat(prop.getNodes(), containsInAnyOrder(p,q,t,v,z,r,c,g,l));
//    }
//
//    @Test
//    public void addName() {
//    }
//
//    @Test
//    public void addNames() {
//    }
//
//    @Test
//    public void removeName() {
//    }
//}