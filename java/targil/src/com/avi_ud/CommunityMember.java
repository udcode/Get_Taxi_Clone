package com.avi_ud;

/***
 * CommunityMember - Represents basic details for community member
 */
public abstract class CommunityMember implements DutiesAndRights {
    final int WEEK_HOURS = 45;
    private int id;
    private Gender gender;
    private String name;
    private String address;
    private String birthday;
    private double torahStudyHours;
    private double workingHours;
    private double income;
    private double loanUsage;
    private VolunteeringType volunteeringType;

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
     */
    public CommunityMember(int id, Gender gender, String name,
                           String address, String birthday,
                           int torahStudyHours, int workingHours,
                           int income, int loanUsage,
                           VolunteeringType volunteeringType) throws ExceptionInInitializerError {
        if(torahStudyHours + workingHours < WEEK_HOURS * ((double)2/3))
            throw new ExceptionInInitializerError("This person doesn't busy at tow third of the week");
        this.id = id;
        this.gender = gender;
        this.name = name;
        this.address = address;
        this.birthday = birthday;
        this.torahStudyHours = torahStudyHours;
        this.workingHours = workingHours;
        this.income = income;
        this.loanUsage = loanUsage;
        this.volunteeringType = volunteeringType;
    }

    /********** Getters and Setters ************/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public double getTorahStudyHours() {
        return torahStudyHours;
    }

    public void setTorahStudyHours(double torahStudyHours) {
        this.torahStudyHours = torahStudyHours;
    }

    public double getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(double workingHours) {
        this.workingHours = workingHours;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getLoanUsage() {
        return loanUsage;
    }

    public void setLoanUsage(double loanUsage) {
        this.loanUsage = loanUsage;
    }

    public VolunteeringType getVolunteeringType() {
        return volunteeringType;
    }

    public void setVolunteeringType(VolunteeringType volunteeringType) {
        this.volunteeringType = volunteeringType;
    }
}
