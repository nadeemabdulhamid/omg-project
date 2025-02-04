/* 
OMG Project
Copyright (c) 2025 Nadeem Abdul Hamid
License: MIT
*/
package omg.main;

import omg.impl.*;
import omg.server.OMGServer;

public class Main {
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        OMGServer omgServer = new OMGServer();
        OMGStore omgStore = new OMGStore(omgServer);
        omgServer.start();
    }
}
