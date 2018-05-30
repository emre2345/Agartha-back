package agartha.starter;

import agartha.site.ServerKt;

/**
 * Purpose of this file is to start the Site WebServer
 * In this package because was not able to start Kotlin from Heroku
 * 
 * <p>
 * Created by Jorgen Andersson on 2018-04-06.
 */
public class StarterMain {

    public static void main(String[] args) {
        ServerKt.startServer(args);
        System.out.println("***************************************************");
        System.out.println("**              Agartha Site                     **");
        System.out.println("***************************************************");
    }
}
