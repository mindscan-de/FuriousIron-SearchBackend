package de.mindscan.furiousiron.incubator.hfb;

import org.junit.jupiter.api.Test;

public class HashFreeBloomTest {

    @Test
    public void testCreateHFBFilter() throws Exception {
        // arrange
        HashFreeBloom hashFreeBloom = new HashFreeBloom();

        // act
        hashFreeBloom.createHFBFilter();

        // assert
        // cpxuas
    }

    @Test
    public void testFilter() throws Exception {
        // arrange
        HashFreeBloom hashFreeBloom = new HashFreeBloom();
        HFBFilterBank filterbank = hashFreeBloom.createHFBFilter();

        // act
        hashFreeBloom.filter( filterbank );

        // +RDI +RDT -> 212
        // RDT -> 337
        // Filter RDT with RDI 1 Filter -> 244  / elimination rate   93/125 
        // Filter RDT with RDI 2 Filters -> 221 / elimination rate   23/125 | 116/125
        // Filter RDT with RDI 3 Filters -> 213 / elimination rate    8/125 | 124/125 
        // Filter RDT with RDI 4 Filters -> 213 
        // Filter RDT with RDI 5 Filters -> 213
        // Filter RDT with RDI 6 Filters -> 212 / elimination rate    1/125 | 125/125
        // Filter RDT with RDI 7 Filters -> 212
        // Filter RDT with RDI 8 Filters -> 212

        // assert
        // cpxuas
    }

}
