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

class KeyValueCounterTest {

    @Test
    public void emptyTest() {

        KeyValueCounter vc = new KeyValueCounter(new Text("a"), new Text(""));

        assertAll(
                () -> assertEquals(0, vc.getTotalCount()),
                () -> assertEquals(1, vc.getNumCFs()),
                () -> assertEquals(1,vc.getCfNames().size()),
                () -> assertEquals(-1, vc.getCfCount(null))
                // () -> assertEquals(Collections.emptySet(), vc.getCfCounts())
        );

    }

    @Test
    public void oneTest() {

        Text rowId = new Text("row1");
        Text colFam = new Text("cf1");
        Text colQual = new Text("cq1");
        ColumnVisibility vis = new ColumnVisibility("public");

        Key key = new Key(rowId, colFam, colQual, vis, System.currentTimeMillis());
    }


    @Test
    public void twoTest() {
    }

    @Test
    public void noCfTest() {
    }

    @Test
    public void multipleCfTest() {
    }


}
