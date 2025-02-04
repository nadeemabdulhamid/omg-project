/* 
OMG Project
Copyright (c) 2025 Nadeem Abdul Hamid
License: MIT
*/
package omg.test;

import omg.server.OMGServer;

public class Main {
    public static void main(String[] args) {
        OMGServer omgServer = new OMGServer();
        System.out.println(omgServer.fetchItemList());
        omgServer.start();
    }
}
