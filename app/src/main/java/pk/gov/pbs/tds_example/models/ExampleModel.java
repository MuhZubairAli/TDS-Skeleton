package pk.gov.pbs.tds_example.models;

import pk.gov.pbs.formbuilder.models.SecondaryModel;
import pk.gov.pbs.formbuilder.models.annotations.PrimaryIdentifier;
import pk.gov.pbs.formbuilder.models.annotations.SecondaryIdentifier;

public class ExampleModel extends SecondaryModel {
    String name;
    Integer age;
    Integer gender;
    String address;
    String university;
    Float cgpa;
    Double fee;

    @PrimaryIdentifier
    String area;

    @SecondaryIdentifier
    Integer building;

    @Override
    public Integer getSecondaryIdentifier() {
        return building;
    }

    @Override
    public void setSecondaryIdentifier(Integer SI) {
        building = SI;
    }

    @Override
    public String getPrimaryIdentifier() {
        return area;
    }

    @Override
    public void setPrimaryIdentifier(String PI) {
        area = PI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public Float getCgpa() {
        return cgpa;
    }

    public void setCgpa(Float cgpa) {
        this.cgpa = cgpa;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Integer getBuilding() {
        return building;
    }

    public void setBuilding(Integer building) {
        this.building = building;
    }
}
