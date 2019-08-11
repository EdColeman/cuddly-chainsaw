package org.apache.edcoleman.cuddly_chainsaw.top_k;

import org.apache.hadoop.io.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CountMinSketchTest {

    private final Logger log = LogManager.getLogger();

    CountMinSketch genData() {

        Text a = new Text("a");
        Text b = new Text("b");
        Text c = new Text("c");

        CountMinSketch cms = new CountMinSketch(4, 5);

        for (int i = 0; i < 10; i++) {
            cms.incr(a);
        }

        for (int i = 0; i < 7; i++) {
            cms.incr(b);
        }

        for (int i = 0; i < 3; i++) {
            cms.incr(c);
        }

        return cms;
    }

    @Test
    public void roundTrip() {

        CountMinSketch cms = genData();

        assertAll(
                () -> assertEquals(10, cms.count(new Text("a"))),
                () -> assertEquals(7, cms.count(new Text("b"))),
                () -> assertEquals(3, cms.count(new Text("c"))),
                () -> assertEquals(0, cms.count(new Text("A"))),
                () -> assertEquals(0, cms.count(new Text("z")))
        );

    }

    @Test
    public void serialize() {

        String filename = "/tmp/cms_" + System.currentTimeMillis() + ".bin";

        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(new File(filename))))) {

            CountMinSketch cms = genData();

            cms.write(out);

        } catch (IOException ex) {
            ex.printStackTrace();
        }


        try (DataInputStream in = new DataInputStream(new FileInputStream(new File(filename)))) {


            CountMinSketch cms = CountMinSketch.read(in);

            assertAll(
                    () -> assertEquals(10, cms.count(new Text("a"))),
                    () -> assertEquals(7, cms.count(new Text("b"))),
                    () -> assertEquals(3, cms.count(new Text("c"))),
                    () -> assertEquals(0, cms.count(new Text("A"))),
                    () -> assertEquals(0, cms.count(new Text("z")))
            );


        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    @Test
    public void compress() throws Exception {

        String filename = "/tmp/cms_" + System.currentTimeMillis() + ".compressed";

        try (DataOutputStream out = new DataOutputStream(
                new DeflaterOutputStream(new BufferedOutputStream(new FileOutputStream(new File(filename)))))) {

            CountMinSketch cms = genData();

            cms.write(out);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try (DataInputStream in = new DataInputStream(
                new InflaterInputStream(new BufferedInputStream(new FileInputStream(new File(filename)))))) {

            CountMinSketch cms = CountMinSketch.read(in);

            assertAll(
                    () -> assertEquals(10, cms.count(new Text("a"))),
                    () -> assertEquals(7, cms.count(new Text("b"))),
                    () -> assertEquals(3, cms.count(new Text("c"))),
                    () -> assertEquals(0, cms.count(new Text("A"))),
                    () -> assertEquals(0, cms.count(new Text("z")))
            );


        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }
}
