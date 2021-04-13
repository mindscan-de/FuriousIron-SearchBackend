package de.mindscan.furiousiron.incubator.hfb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void testSetIndex_SetIdx0_expectIdx0BeTrue() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 5 );
        data.initFilter();

        // act
        data.setIndex( 0 );

        // assert
        boolean result = data.isIndexSet( 0 );
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testSetIndex_SetIdx1_expectIdx0BeFalse() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 5 );
        data.initFilter();

        // act
        data.setIndex( 1 );

        // assert
        boolean result = data.isIndexSet( 0 );
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testSetIndex_SetIdx1_expectIdx1BeTrue() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 5 );
        data.initFilter();

        // act
        data.setIndex( 1 );

        // assert
        boolean result = data.isIndexSet( 1 );
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testSetIndex_SetIdx31_expectIdx31BeTrue() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 5 );
        data.initFilter();

        // act
        data.setIndex( 31 );

        // assert
        boolean result = data.isIndexSet( 31 );
        assertThat( result, equalTo( true ) );
    }

    @Test
    public void testClearIndex_SetIdx31ThenClear_expectIdx31BeFalse() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 5 );
        data.initFilter();
        data.setIndex( 31 );

        // act
        data.clearIndex( 31 );

        // assert
        boolean result = data.isIndexSet( 31 );
        assertThat( result, equalTo( false ) );
    }

    @Test
    public void testSetIndex_SetIdx32WhenOnlyBitWideWindow_throwsAIOOBException() throws Exception {
        // arrange
        HFBFilterData data = new HFBFilterData( 0, 5 );
        data.initFilter();

        // act
        assertThrows( ArrayIndexOutOfBoundsException.class, () -> {
            data.setIndex( 32 );
        } );
    }

}
