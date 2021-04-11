package de.mindscan.furiousiron.incubator.hfb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class HFBFilterDataTest {

    @Test
    public void testSetSliceMaskSize_SetMaskSizeTo16Bit_expect16BitIsSet() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData();

        // act
        data.setSliceMaskSize( 16 );

        // assert
        int result = data.getSliceBitSize();
        assertThat( result, equalTo( 16 ) );
    }

    @Test
    public void testSetSliceMaskSize_SetMaskSizeTo4Bit_expect4BitIsSet() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData();

        // act
        data.setSliceMaskSize( 4 );

        // assert
        int result = data.getSliceBitSize();
        assertThat( result, equalTo( 4 ) );
    }

    @Test
    public void testGetSliceBitMask_SetMsakSizeTo4Bit_expectMaskIs15() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData();
        data.setSliceMaskSize( 4 );

        // act
        String result = data.getSliceBitMask().toString();

        // assert
        assertThat( result, equalTo( "15" ) );
    }

    @Test
    public void testGetSliceBitMask_SetMsakSizeTo16Bit_expectMaskIs65535() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData();
        data.setSliceMaskSize( 16 );

        // act
        String result = data.getSliceBitMask().toString();

        // assert
        assertThat( result, equalTo( "65535" ) );
    }

    @Test
    public void testGetSliceBitMask_SetMsakSizeTo10Bit_expectMaskIs1023() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData();
        data.setSliceMaskSize( 10 );

        // act
        String result = data.getSliceBitMask().toString();

        // assert
        assertThat( result, equalTo( "1023" ) );
    }

}
