package model;

import javax.persistence.Column;
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
    @Column(name = "Student_Id")
    private int studentId;
    @Column(name = "First_Name")
    private String firstName;
    @Column(name = "Last_Name")
    private String lastName;
    @Column(name = "Class_Name")
    private String className;
    @Column(name = "Roll_No")
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