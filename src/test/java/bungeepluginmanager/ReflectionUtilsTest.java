package bungeepluginmanager;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ReflectionUtilsTest {


    @Test
    public void classConstructor() throws Throwable {
        Constructor constructor = ReflectionUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch (InvocationTargetException e) {
            assertEquals(IllegalStateException.class, e.getTargetException().getClass());

        }
    }

    @Test
    public void shouldGetPrivateFieldValue() {
        DummyObject obj = new DummyObject();
        int value = ReflectionUtils.getFieldValue(obj, "test");
        assertEquals(1, value);

        assertNull(ReflectionUtils.getFieldValue(obj, "none"));

    }

    @Test
    public void shouldSetPrivateFieldValue() {
        DummyObject obj = new DummyObject();
        ReflectionUtils.setFieldValue(obj, "test", 5);
        assertEquals(5, obj.test);

        ReflectionUtils.setFieldValue(obj, "nothing", 5);
    }

    @Test
    public void shouldGetPrivateStaticFieldValue() {
        assertEquals(1, (int) ReflectionUtils.getStaticFieldValue(DummyObject.class, "testStatic"));
        assertNull(ReflectionUtils.getStaticFieldValue(DummyObject.class, "nothing"));
    }

    @Test
    public void invokeMethodTest() {
        DummyObject object = new DummyObject();
        ReflectionUtils.invokeMethod(object, "add", 3);
        assertEquals(4, object.test);

        ReflectionUtils.invokeMethod(object, "add", "Test");
    }
}

class DummyObject {
    static int testStatic = 1;
    int test = 1;

    private void add(int value) {
        test = test + value;
    }

}
