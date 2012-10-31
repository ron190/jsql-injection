package mvc.view;

import java.util.Observable;

/**
 * Alternative view, simple console 
 */
public class Console implements java.util.Observer {

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg1 instanceof String)
			System.out.println((String) arg1);
	}
}
