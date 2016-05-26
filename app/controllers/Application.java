package controllers;

import jws.mvc.*;

@With(Handler.class)
public class Application extends Controller {

    public static void index() {
        render();
    }
    
    public static void error() {
    	throw new RuntimeException("a test exception");
    }
}