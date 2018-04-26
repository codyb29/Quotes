package assignment11;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UnitTests {

	// predicate: (month2 == month1) row 1
	@Test
	void testp1r1() {
		int month1 = 6; // should be equivalent to month2
		int month2 = 6; // should be equivalent to month1
		int day1 = 4;
		int day2 = 21;
		int year = 2008;
		int ans = Cal.cal(month1, day1, month2, day2, year);
		// Should execute statement within if statement if predicate evaluates to true
		assertEquals(day2 - day1, ans);
	}

	// predicate: (month2 == month1) row 2
	@Test
	void testp1r2() {
		int month1 = 3; // should differ from month2
		int month2 = 5; // should differ from month1
		int day1 = 4;
		int day2 = 21;
		int year = 2008;
		int ans = Cal.cal(month1, day1, month2, day2, year);
		// Should not execute statement within if statement if predicate evaluates to
		// false
		assertNotEquals(day2 - day1, ans);
	}

	// predicate: ((m4 !=0) || ((m100 == 0) && (m400 != 0))) row 3
	@Test
	void testp2r3() {
		// should differ from month2, Make it February and check to see if February was
		// assigned correctly. Since predicate should evaluate to true, cal should
		// return 28.
		int month1 = 2;
		int month2 = 3; // should differ from month1, set it to March for simplicity.
		int day1 = 1;
		int day2 = 1;
		int year = 7; // critical value
		int ans = Cal.cal(month1, day1, month2, day2, year);
		assertEquals(ans, 28);
	}

	// predicate: ((m4 !=0) || ((m100 == 0) && (m400 != 0))) row 7
	@Test
	void testp2r7() {
		// should differ from month2, Make it February and check to see if February was
		// assigned correctly. Since predicate should evaluate to false, cal should
		// return 29.
		int month1 = 2;
		int month2 = 3; // should differ from month1, set it to March for simplicity.
		int day1 = 1;
		int day2 = 1;
		int year = 4; // critical value
		int ans = Cal.cal(month1, day1, month2, day2, year);
		assertEquals(ans, 29);
	}

	// predicate: ((m4 !=0) || ((m100 == 0) && (m400 != 0))) row 5
	@Test
	void testp2r5() {
		int month1 = 2;
		int day1 = 1;
		int month2 = 3;
		int day2 = 1;
		int year = 100;

		// fTtt

		// a = m4 != 0
		assertFalse(year % 4 != 0);
		// b = m100 == 0
		assertTrue(year % 100 == 0);
		// c = m400 != 0
		assertTrue(year % 400 != 0);
		// p = m4 != 0 || (m100 == 0 && m400 !=0)
		assertTrue(year % 4 != 0 || (year % 100 == 0 && year % 400 != 0));

		/*
		 * here since the months are different, we expect to go to the else part the
		 * year is not a leap year since it is divisible by 100 but not 400!
		 */

		int ans = Cal.cal(month1, day1, month2, day2, year);
		assertEquals(ans, 28);
	}

	// predicate: ((m4 !=0) || ((m100 == 0) && (m400 != 0))) row 6
	@Test
	void testp2r6() {
		int month1 = 2;
		int day1 = 1;
		int month2 = 3;
		int day2 = 1;
		int year = 400;

		// ftFf

		// a = m4 != 0
		assertFalse(year % 4 != 0);
		// b = m100 == 0
		assertTrue(year % 100 == 0);
		// c = m400 != 0
		assertFalse(year % 400 != 0);
		// p = m4 != 0 || (m100 == 0 && m400 !=0)
		assertFalse(year % 4 != 0 || (year % 100 == 0 && year % 400 != 0));

		/*
		 * here since the months are different, we expect to go to the else part the
		 * year is leap year since it is divisible by 400
		 */

		int ans = Cal.cal(month1, day1, month2, day2, year);
		assertEquals(ans, 29);
	}

	// predicate: (i <= month2-1) row 1.
	@Test
	void testp3r1() {
		int day1 = 1;
		int day2 = 1;
		int month1 = 1;
		int month2 = 3;
		int year = 400;
		int i = month1 + 1;

		assertTrue(i <= month2 - 1);

		int ans = Cal.cal(month1, day1, month2, day2, year);
		// we should go in the else part of the Cal method which has our predicate
		// ans should calculate the days between month2 and month1 by adding
		// the values in daysIn[] and since we are in a leap year, february = 29.
		assertEquals(ans, 60);

	}

	// predicate: (i <= month2-1) row 2.
	@Test
	void testp3r2() {
		int day1 = 5;
		int day2 = 15;
		int month1 = 5;
		int month2 = 5;
		int year = 400;
		int i = month1 + 1;

		assertFalse(i <= month2 - 1);

		int ans = Cal.cal(month1, day1, month2, day2, year);
		// we should go in the if part of the Cal method.
		// since the months are equal here, the predicate is never reached
		// and we are just subtracting the days.
		assertEquals(ans, 10);
	}
}
