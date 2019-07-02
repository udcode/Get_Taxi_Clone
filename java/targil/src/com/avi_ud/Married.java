package com.avi_ud;

/**
 * Married - Represents a Married member of the community
 */
public class Married extends CommunityMember {
    private int idPartner;
    private int childrenUnder18;

    /**
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
     * @param idPartner partner id
     * @param childrenUnder18 number of children under 18
     */
    public Married(int id, Gender gender, String name, String address,
                   String birthday, int torahStudyHours, int workingHours,
                   int income, int loanUsage, VolunteeringType volunteeringType,
                   int idPartner, int childrenUnder18) {
        super(id, gender, name, address, birthday, torahStudyHours, workingHours,
                income, loanUsage, volunteeringType);
        this.idPartner = idPartner;
        this.childrenUnder18 = childrenUnder18;
    }

    /********** Getters and Setters ************/

    public int getIdPartner() {
        return idPartner;
    }

    public void setIdPartner(int idPartner) {
        this.idPartner = idPartner;
    }

    public int getChildrenUnder18() {
        return childrenUnder18;
    }

    public void setChildrenUnder18(int childrenUnder18) {
        this.childrenUnder18 = childrenUnder18;
    }


    /**
     * Get the Tax rate of community member
     *
     * @return the tax rate
     */
    @Override
    public double getTaxRate() {
        double factor = 1;
        if(getWorkingHours() < 10 || getChildrenUnder18() > 6) //low income
            factor = 2;
        if(getChildrenUnder18() > 10)
            factor = 3;
        if(getTorahStudyHours() >= WEEK_HOURS) //gets free
            return 0;
        return getIncome() * ((double)1/10) * (1/factor);
    }

    /**
     * Get the maximum loan that community member can get
     * Every child give 1000, and every Torah hour give 500
     * @return the maximum loan
     */
    @Override
    public double getMaxLoan() {
        return getChildrenUnder18() * 1000 + getTorahStudyHours() * 500;
    }

    /**
     * Get the recommended Volunteer hours for community member
     *
     * @return the recommended Volunteer hours
     */
    @Override
    public double getRecommendedVolunteerHours() {
        double hours = WEEK_HOURS - (getTorahStudyHours() + getWorkingHours());
        return hours < 0 ? 0 : hours/2;
    }
}
