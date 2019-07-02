package com.avi_ud;

public class Main {

    public static void main(String[] args) {
        //test the classes
        Community community = new Community();

        try {
            CommunityMember member1 = new Single(209695472, Gender.MALE, "Yoel", "Jerusalem", "22/12/1997", 25, 15, 4000, 0, VolunteeringType.SPIRITUALLY, 10);
            CommunityMember member2 = new Single(209641414, Gender.FEMALE, "Riki", "Jerusalem", "15/01/1990", 0, 40, 5200, 0, VolunteeringType.PHYSICALLY, 13);
            CommunityMember member3 = new Single(425295472, Gender.MALE, "Avi", "Jerusalem", "22/08/1997", 25, 10, 6510, 0, VolunteeringType.MUSICALLY, 15);
            CommunityMember member4 = new Married(256395472, Gender.FEMALE, "Yochi", "Jerusalem", "23/06/1991", 0, 48, 8000, 0, VolunteeringType.SPIRITUALLY, 209785472, 7);
            CommunityMember member5 = new Married(209785472, Gender.MALE, "Moshe", "Jerusalem", "17/06/1992", 45, 5, 2780, 0, VolunteeringType.MUSICALLY, 256395472, 7);
            CommunityMember member6 = new Married(209785472, Gender.MALE, "Reoven", "Jerusalem", "17/04/1870", 30, 10, 5800, 0, VolunteeringType.MUSICALLY, 256395472, 2);


            community.addMember(member1);
            community.addMember(member2);
            community.addMember(member3);
            community.addMember(member4);
            community.addMember(member5);
            community.addMember(member6);

            System.out.println("Community tax for month: " + community.calcCommunityTax());
            System.out.println();

            System.out.println(member1.getName() + " get loan from the Gmach of:" + community.loanRequest(member1, 2000));
            System.out.println(member2.getName() + " get loan from the Gmach of:" + community.loanRequest(member2, 2000));
            System.out.println(member3.getName() + " get loan from the Gmach of:" + community.loanRequest(member3, 2000));
            System.out.println(member4.getName() + " get loan from the Gmach of:" + community.loanRequest(member4, 2000));
            System.out.println(member5.getName() + " get loan from the Gmach of:" + community.loanRequest(member5, 2000));


            double[] volunteerHours = community.volunteerHours();
            System.out.println("\n" + "Volunteer:");
            System.out.println("Volunteer hours of musical: " + volunteerHours[0]);
            System.out.println("Volunteer hours of physically: " + volunteerHours[1]);
            System.out.println("Volunteer hours of spiritual: " + volunteerHours[2]);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
