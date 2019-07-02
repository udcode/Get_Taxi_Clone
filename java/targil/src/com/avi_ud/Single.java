package com.avi_ud;

/**
 * Single - Represents a single member o the community
 */
public class Single extends CommunityMember {

    private int educationYears;

    /***
     * Constructor
     * @param id member id
     * @param gender member gender
     * @param name member name
     * @param address member address
     * @param birthday member birthday
     * @param torahStudyHours amount of Torah study hours by member
     * @param workingHours amount of working hours by member
     * @param income member income
     * @param loanUsage member loan usage from Gmach
     * @param volunteeringType member volunteering type
     * @param educationYears number of education years
     */
    public Single(int id, Gender gender, String name, String address,
                  String birthday, int torahStudyHours, int workingHours,
                  int income, int loanUsage, VolunteeringType volunteeringType,
                  int educationYears) {
        super(id, gender, name, address, birthday, torahStudyHours,
                workingHours, income, loanUsage, volunteeringType);
        this.educationYears = educationYears;
    }

    /********** Getters and Setters ************/

    public int getEducationYears() {
        return educationYears;
    }

    public void setEducationYears(int educationYears) {
        this.educationYears = educationYears;
    }

    /**
     * Get the Tax rate of community member
     *
     * @return the tax rate
     */
    @Override
    public double getTaxRate() {
        double factor = 1;
        if(getEducationYears() < 12 || getWorkingHours() < 10) //low income
            factor = 2;
        if(getTorahStudyHours() >= WEEK_HOURS) //gets free
            return 0;
        return getIncome() * ((double)1/10) * (1/factor);
    }

    /**
     * Get the maximum loan that community member can get
     * A single member can't get a loan from the Gmach
     * @return the maximum loan
     */
    @Override
    public double getMaxLoan() {
        return 0;
    }

    /**
     * Get the recommended Volunteer hours for community member
     *
     * @return the recommended Volunteer hours
     */
    @Override
    public double getRecommendedVolunteerHours() {
        //return all extra hours
        double hours = WEEK_HOURS - (getTorahStudyHours() + getWorkingHours());
        return hours < 0 ? 0 : hours;
    }
}
