package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.IOException;

/**
 * Server entry point. Creates the tic-tac-toe {@link SimpleServer} and starts
 * listening. The port may be passed as the first argument (default 3000).
 */
public class App
{

	private static SimpleServer server;
    public static void main( String[] args ) throws IOException
    {
        int port = 3000;
        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid port '" + args[0] + "', using 3000.");
            }
        }
        server = new SimpleServer(port);
        server.listen();
        System.out.println("Tic-tac-toe server is listening on port " + port);
    }
}
