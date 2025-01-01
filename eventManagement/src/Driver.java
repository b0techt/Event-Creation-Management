
import model.Model;
import controller.Controller;
import view.View;

public class Driver {

	public static void main(String[] args) {
		//model
		Model m = new Model();

		//view
		View v = new View();

		//controller
		Controller c = new Controller(m,v);
		c.start();
	}
}
