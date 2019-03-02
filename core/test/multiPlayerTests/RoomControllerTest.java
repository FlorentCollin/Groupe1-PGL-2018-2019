package multiPlayerTests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import roomController.RoomController;
import server.Client;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class RoomControllerTest {

    private RoomController roomController;

    @Before
    public void initialize() {
        roomController = new RoomController(new LinkedBlockingQueue<>());
    }

}
