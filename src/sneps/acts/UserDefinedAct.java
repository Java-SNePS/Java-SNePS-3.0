package sneps.acts;

import java.io.IOException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import sneps.network.ActNode;


public class UserDefinedAct extends ActNode {
	
	public UserDefinedAct(){
		super();
	}
	
	
	public static Class<? extends UserDefinedAct> createClass( String fullName,String m )
            throws NotFoundException, CannotCompileException, IOException
    {
        ClassPool pool = ClassPool.getDefault();

        // Create the class.
        CtClass subClass = pool.makeClass( fullName );
        final CtClass superClass = pool.get( UserDefinedAct.class.getName() );
        subClass.setSuperclass( superClass );
        subClass.setModifiers( Modifier.PUBLIC );

        // Add a constructor which will call super( ... );
        CtClass[] params = new CtClass[]{
            pool.get( UserDefinedAct.class.getName() ),
            pool.get( UserDefinedAct.class.getName()) 
        };
        final CtConstructor ctor = CtNewConstructor.make( params, null, CtNewConstructor.PASS_PARAMS, null, null, subClass );
        subClass.addConstructor( ctor );
        
        CtMethod newmethod = CtNewMethod.make(m,subClass);
        subClass.addMethod(newmethod);
        subClass.writeFile();
        
        return subClass.toClass();
    }
	 
	
	public static void main(String args[]) {
		
		try {
		Object pickup=createClass("pickup","m");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
}
