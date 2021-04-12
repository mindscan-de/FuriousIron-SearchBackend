package de.mindscan.furiousiron.incubator.hfb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class HFBFilterDataTest {

    @Test
    public void testSetSliceMaskSize_SetMaskSizeTo16Bit_expect16BitIsSet() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 0 );

        // act
        data.setSliceMaskSize( 16 );

        // assert
        int result = data.getSliceBitSize();
        assertThat( result, equalTo( 16 ) );
    }

    @Test
    public void testSetSliceMaskSize_SetMaskSizeTo4Bit_expect4BitIsSet() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 0 );

        // act
        data.setSliceMaskSize( 4 );

        // assert
        int result = data.getSliceBitSize();
        assertThat( result, equalTo( 4 ) );
    }

    @Test
    public void testGetSliceBitMask_SetMsakSizeTo4Bit_expectMaskIs15() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 0 );
        data.setSliceMaskSize( 4 );

        // act
        long result = data.getSliceBitMask();

        // assert
        assertThat( result, equalTo( 15L ) );
    }

    @Test
    public void testGetSliceBitMask_SetMsakSizeTo4BitCtor_expectMaskIs15() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 4 );

        // act
        long result = data.getSliceBitMask();

        // assert
        assertThat( result, equalTo( 15L ) );
    }

    @Test
    public void testGetSliceBitMask_SetMsakSizeTo16Bit_expectMaskIs65535() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 0 );
        data.setSliceMaskSize( 16 );

        // act
        long result = data.getSliceBitMask();

        // assert
        assertThat( result, equalTo( 65535L ) );
    }

    @Test
    public void testGetSliceBitMask_SetMsakSizeTo16BitCtor_expectMaskIs65535() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 16 );

        // act
        long result = data.getSliceBitMask();

        // assert
        assertThat( result, equalTo( 65535L ) );
    }

    @Test
    public void testGetSliceBitMask_SetMaskSizeTo10Bit_expectMaskIs1023() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 0 );
        data.setSliceMaskSize( 10 );

        // act
        long result = data.getSliceBitMask();

        // assert
        assertThat( result, equalTo( 1023L ) );
    }

    @Test
    public void testGetSliceBitMask_SetMaskSizeTo10BitCtor_expectMaskIs1023() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 10 );

        // act
        long result = data.getSliceBitMask();

        // assert
        assertThat( result, equalTo( 1023L ) );
    }

    @Test
    public void testGetSlicePosition_SetSlicePositonTo0Ctor_expectSlicePositionIsZero() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 10 );

        // act
        int result = data.getSlicePosition();

        // assert
        assertThat( result, equalTo( 0 ) );
    }

    @Test
    public void testGetSlicePosition_SetSlicePositonTo9Ctor_expectSlicePositionIsNine() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 9, 10 );

        // act
        int result = data.getSlicePosition();

        // assert
        assertThat( result, equalTo( 9 ) );
    }

    @Test
    public void testGetSlicePosition_SetSlicePositonTo23Ctor_expectSlicePositionIsTwentyThree() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 23, 10 );

        // act
        int result = data.getSlicePosition();

        // assert
        assertThat( result, equalTo( 23 ) );
    }

    @Test
    public void testGetSlicePosition_SetSlicePositonTo23_expectSlicePositionIsTwentyThree() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 0 );

        // act
        data.setSlicePosition( 23 );

        // assert
        int result = data.getSlicePosition();
        assertThat( result, equalTo( 23 ) );
    }

    @Test
    public void testGetSlicePosition_SetSlicePositonTo9_expectSlicePositionIsNine() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 0 );

        // act
        data.setSlicePosition( 9 );

        // assert
        int result = data.getSlicePosition();
        assertThat( result, equalTo( 9 ) );
    }

}
