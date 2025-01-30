
import model.Model;
import controller.Controller;
import java.io.IOException;
import view.View;

public class Driver {

	public static void main(String[] args) throws IOException {
		//model
		Model m = new Model();

		//view
		View v = new View();

		//controller
		Controller c = new Controller(m,v);
		v.setController(c);

		c.start(); 
	}
}
