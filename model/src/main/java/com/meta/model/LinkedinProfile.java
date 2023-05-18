package com.meta.model;

import java.util.Objects;

/**
 * <p>
 * Communicates with the Controller to update the data.
 * </p>
 *
 * @author Anbu
 * @version 1.0
 */
public class LinkedinProfile {

    private Long id;
    private Long mobileNumber;
    private String emailAddress;
    private String name;
    private String password;
    private String skill;
    private Double experience;
    private String education;

    public void setId(final Long id) {
        this.id = id;
    }

    public void setMobileNumber(final Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setSkill(final String skill) {
        this.skill = skill;
    }

    public void setExperience(final Double experience) {
        this.experience = experience;
    }

    public void setEducation(final String education) {
        this.education = education;
    }

    public Long getId() {
        return id;
    }

    public Long getMobileNumber() {
        return mobileNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getSkill() {
        return skill;
    }

    public Double getExperience() {
        return experience;
    }

    public String getEducation() {
        return education;
    }

    public String toString() {

        if (Objects.isNull(getMobileNumber()) && Objects.nonNull(getEmailAddress())) {
            return String.format("Name : %s%nSkill : %s%nExperience : %s%nEducation : %s%nEmail Address : %s%n", name, skill, experience, education, emailAddress);
        } else if (Objects.isNull(getEmailAddress()) && Objects.nonNull(getMobileNumber())) {
            return String.format("Name : %s%nSkill : %s%nExperience : %s%nEducation : %s%nMobile Number : %s%n", name, skill, experience, education, mobileNumber);
        } else if (Objects.isNull(getMobileNumber()) && Objects.isNull(getEmailAddress()) && Objects.isNull(getExperience())) {
            return String.format("Name : %s%nEducation : %s%n", name, education);
        } else if (Objects.isNull(getMobileNumber()) && Objects.isNull(getEmailAddress())) {
            return String.format("Name : %s%nSkill : %s%nExperience : %s%nEducation : %s%n", name, skill, experience, education);
        } else {
            return String.format("Name : %s%nSkill : %s%nExperience : %s%nEducation : %s%nMobile Number : %s%nEmail Address : %s%n", name, skill, experience, education, mobileNumber, emailAddress);
        }
    }
}
