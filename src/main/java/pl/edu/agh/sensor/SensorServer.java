package pl.edu.agh.sensor;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bartosz
 */
public class SensorServer extends WebSocketServer {

    private static final int PORT = 1777;

    private static final double THRESHOLD = 0.01;

    private static final String FILENAME = "results_";

    private static int counter = 1;

    private static SensorServer sensorServer;

    private Map<WebSocket, FileWriter> files = new HashMap<>();

    public static SensorServer getInstance() {
        if (sensorServer == null) {
            sensorServer = new SensorServer(PORT);
        }
        return sensorServer;
    }

    private SensorServer(int address) {
        super(new InetSocketAddress(address));
    }

    public static void main(String[] args) {
        SensorServer.getInstance().start();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        try {
            getFileWriter(conn).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FileWriter getFileWriter(WebSocket conn) {
        FileWriter file = files.get(conn);
        if(file == null) {
            try {
                file = new FileWriter(FILENAME + (counter++) + ".csv");
                files.put(conn, file);
                file.append("gyr_x,gyr_y,gyr_z,acc_x,acc_y,acc_z\n");
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            FileWriter fileWriter = getFileWriter(conn);
            fileWriter.append(message);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }
}
