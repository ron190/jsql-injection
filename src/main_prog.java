import mvc.controller.InjectionController;
import mvc.model.InjectionModel;


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
