package com.avi_ud;

/**
 * DutiesAndRights - Represents the duties and the rights that every community member has.
 */
public interface DutiesAndRights {
    /**
     * Get the Tax rate of community member
     * @return the tax rate
     */
    double getTaxRate();

    /**
     * Get the maximum loan that community member can get
     * @return the maximum loan
     */
    double getMaxLoan();

    /**
     * Get the recommended Volunteer hours for community member
     * @return the recommended Volunteer hours
     */
    double getRecommendedVolunteerHours();
}
