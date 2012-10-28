import mvc.controller.InjectionController;
import mvc.model.InjectionModel;

/**
 * Starting point of the application, build the MVC components which will
 * display the GUI and run the injection independently
 */
public class main_prog {

	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	InjectionModel model = new InjectionModel();
        		new InjectionController(model);
            }
        });
	}

}
