package application.mechanics.base;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CoordinatesTest {

    @Test
    public void addTest() {
        Coordinates point1 = new Coordinates(42, 16.07);
        Coordinates point2 = new Coordinates(13.3, 21.095);

        assertNotEquals(point1, point2);

        assertEquals(point1.add(point2), new Coordinates(55.3, 37.165));
        assertEquals(point1, new Coordinates(55.3, 37.165));

        point1.set(4, -9);

        assertEquals(point1.getX(), 4, 1e-15);
        assertEquals(point1.getY(), -9, 1e-15);
    }
}
