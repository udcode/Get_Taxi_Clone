package com.avi_ud;

import java.util.ArrayList;

/**
 * Community - represent the community members
 */
public class Community {
    private  ArrayList<CommunityMember> communityMembers = new ArrayList<>();

    /**
     * Add new community member
     * @param member details
     */
    public void addMember(CommunityMember member){
        communityMembers.add(member);
    }

    /**
     * @return the sum of the tax from community
     */
    public double calcCommunityTax(){
        double sum = 0;
        for(CommunityMember member : communityMembers){
            sum += member.getTaxRate();
        }
        return sum;
    }

    /**
     * Get a loan request from the Gmach
     * @param member the member that wants the loan
     * @param amount the amount that this member needs
     * @return the amount that the Gmach approved
     */
    public double loanRequest(CommunityMember member, double amount){
        if(amount <= member.getMaxLoan() - member.getLoanUsage()){//the request is less than the maximum
            member.setLoanUsage(member.getLoanUsage() + amount);
            return amount;
        }
        //give the maximum
        member.setLoanUsage( member.getMaxLoan() - member.getLoanUsage());
        return member.getMaxLoan() - member.getLoanUsage();
    }

    /**
     * @return Array of volunteering hours sorting by SPIRITUALLY,PHYSICALLY,MUSICALLY
     */
    public double [] volunteerHours(){
        double volunteeringHoursArray[] = new double[3];
        for(CommunityMember member : communityMembers){
            if(member.getVolunteeringType() == VolunteeringType.SPIRITUALLY)
                volunteeringHoursArray[0]+= member.getRecommendedVolunteerHours();
            else if(member.getVolunteeringType() == VolunteeringType.PHYSICALLY)
                volunteeringHoursArray[1]+= member.getRecommendedVolunteerHours();
            else if(member.getVolunteeringType() == VolunteeringType.MUSICALLY)
                volunteeringHoursArray[2]+= member.getRecommendedVolunteerHours();
        }
        return volunteeringHoursArray;
    }
}
