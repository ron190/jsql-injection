package model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * This class represents a persistent class for Student.
 * @author codesjava
 */
@Entity
public class Student {
    //data members
    @Id
    private int studentId;
    private String firstName;
    private String lastName;
    private String className;
    private String rollNo;
    private int age;
 
    //no-argument constructor
    public Student(){
 
    }
 
    //getter and setter methods
    public int getStudentId() {
        return studentId;
    }
 
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
 
    public String getFirstName() {
        return firstName;
    }
 
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
 
    public String getLastName() {
        return lastName;
    }
 
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
 
    public String getClassName() {
        return className;
    }
 
    public void setClassName(String className) {
        this.className = className;
    }
 
    public String getRollNo() {
        return rollNo;
    }
 
    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }
 
    public int getAge() {
        return age;
    }
 
    public void setAge(int age) {
        this.age = age;
    }
}