package org.apache.edcoleman.cuddly_chainsaw.top_k;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.hadoop.io.Text;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ValueCounterTest {

    @Test
    public void emptyTest() {
        ValueCounter vc = new ValueCounter();

        assertAll(
                () -> assertEquals(0, vc.getTotalCount()),
                () -> assertEquals(0, vc.getNumCFs()),
                () -> assertEquals(0,vc.getCfNames().size()),
                () -> assertEquals(-1, vc.getCfCount(null)),
                () -> assertEquals(Collections.emptySet(), vc.getCfCounts())
        );

    }

    @Test
    public void oneTest() {

        ValueCounter vc = new ValueCounter();

        Text rowId = new Text("row1");
        Text colFam = new Text("cf1");
        Text colQual = new Text("cq1");
        ColumnVisibility vis = new ColumnVisibility("public");

        Key key = new Key(rowId, colFam, colQual, vis, System.currentTimeMillis());

        vc.update(key);

        Map<Text,Long> expectedCf = new HashMap<>();

        expectedCf.put(colFam,1L);

        assertAll(
                () -> assertEquals(1, vc.getTotalCount()),
                () -> assertEquals(1, vc.getNumCFs()),
                () -> assertEquals(1,vc.getCfNames().size()),
                () -> assertEquals(-1, vc.getCfCount(null)),
                () -> assertEquals(1, vc.getCfCount(colFam)),
                () -> assertEquals(expectedCf.entrySet(), vc.getCfCounts())
        );

    }


    @Test
    public void twoTest() {

        ValueCounter vc = new ValueCounter();

        Text rowId = new Text("row1");
        Text colFam = new Text("cf1");
        Text colQual = new Text("cq1");
        ColumnVisibility vis = new ColumnVisibility("public");

        Key key = new Key(rowId, colFam, colQual, vis, System.currentTimeMillis());

        vc.update(key);
        vc.update(key);

        Map<Text,Long> expectedCf = new HashMap<>();

        expectedCf.put(colFam,2L);

        assertAll(
                () -> assertEquals(2, vc.getTotalCount()),
                () -> assertEquals(1, vc.getNumCFs()),
                () -> assertEquals(1,vc.getCfNames().size()),
                () -> assertEquals(-1, vc.getCfCount(null)),
                () -> assertEquals(2, vc.getCfCount(colFam)),
                () -> assertEquals(expectedCf.entrySet(), vc.getCfCounts())
        );

    }

    @Test
    public void noCfTest() {

        ValueCounter vc = new ValueCounter();

        Text rowId = new Text("row1");
//        Text colFam = new Text("cf1");
        Text colQual = new Text("cq1");
        ColumnVisibility vis = new ColumnVisibility("public");

        Key key = new Key(rowId);

        vc.update(key);
        vc.update(key);

        Map<Text,Long> expectedCf = new HashMap<>();

        expectedCf.put(new Text(),2L);

        assertAll(
                () -> assertEquals(2, vc.getTotalCount()),
                () -> assertEquals(1, vc.getNumCFs()),
                () -> assertEquals(1,vc.getCfNames().size()),
                () -> assertEquals(-1, vc.getCfCount(null)),
                () -> assertEquals(2, vc.getCfCount(new Text())),
                () -> assertEquals(expectedCf.entrySet(), vc.getCfCounts())
        );

    }



    @Test
    public void multipleCfTest() {

        ValueCounter vc = new ValueCounter();

        vc.update(new Key(new Text("row1"),
                new Text("cf1"),
                new Text("cq1"),
                new ColumnVisibility("public"),
                System.currentTimeMillis()));

        vc.update(new Key(new Text("row1"),
                new Text("cf1"),
                new Text("cq1"),
                new ColumnVisibility("public"),
                System.currentTimeMillis()));

        vc.update(new Key(new Text("row2"),
                new Text("cf2"),
                new Text("cq2"),
                new ColumnVisibility("public"),
                System.currentTimeMillis()));

        Map<Text,Long> expectedCf = new HashMap<>();

        expectedCf.put(new Text("cf1"),2L);
        expectedCf.put(new Text("cf2"),1L);

        assertAll(
                () -> assertEquals(3, vc.getTotalCount()),
                () -> assertEquals(2, vc.getNumCFs()),
                () -> assertEquals(2,vc.getCfNames().size()),
                () -> assertEquals(-1, vc.getCfCount(null)),
                () -> assertEquals(2, vc.getCfCount(new Text("cf1"))),
                () -> assertEquals(1, vc.getCfCount(new Text("cf2"))),
                () -> assertEquals(expectedCf.entrySet(), vc.getCfCounts())
        );

    }


}
