/**
 * Copyright (c) 2025 Sami Menik, PhD. All rights reserved.
 * 
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 * This software is provided "as is," without warranty of any kind.
 */
package uga.cs4370.mydbimpl;

import java.util.ArrayList;
import java.util.List;

import uga.cs4370.mydb.RAimpl;
import uga.cs4370.mydb.Relation;
import uga.cs4370.mydb.RelationBuilder;
import uga.cs4370.mydb.Type;



public class Driver {
    
    public static void main(String[] args) {

        // List names of students(who have taken 0 credits) and their advisors.
        // Return student names, advisor names, and total credits taken by the student.
        query();
    }

        private static void query() {

                Relation instructor = new RelationBuilder()
                        .attributeNames(List.of("ID", "Name", "Dept", "Salary"))
                        .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.DOUBLE))
                        .build();
                instructor.loadData("C:/Users/landi/OneDrive/Desktop/UgaCS/4370/ICA2/mysql-files/instructor_export.csv");
                
                Relation student = new RelationBuilder()
                        .attributeNames(List.of("ID", "name", "dept_name", "tot_cred"))
                        .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.INTEGER))
                        .build();
                student.loadData("C:/Users/landi/OneDrive/Desktop/UgaCS/4370/ICA2/mysql-files/student_export.csv");

                
                Relation advisor = new RelationBuilder()
                        .attributeNames(List.of("s_ID", "i_ID"))
                        .attributeTypes(List.of(Type.INTEGER, Type.INTEGER))
                        .build();
                advisor.loadData("C:/Users/landi/OneDrive/Desktop/UgaCS/4370/ICA2/mysql-files/advisor.csv");

                RAimpl DB = new RAimpl();

                // List names of students(who have taken 0 credits) and their advisors.
                // Return student names, advisor names, and total credits taken by the student.

                List<String> studentaddr = new ArrayList<>();
                studentaddr.add("sID");
                studentaddr.add("sName");
                studentaddr.add("sDept");
                studentaddr.add("tot_cred");

                List<String> instraddr = new ArrayList<>();
                instraddr.add("iID");
                instraddr.add("iName");
                instraddr.add("iDept");
                instraddr.add("iSalary");

                Relation renamedStudent = DB.rename(student, student.getAttrs(), studentaddr);
                Relation renamedInstr = DB.rename(instructor, instructor.getAttrs(), instraddr);
                
                Relation studentAdvisor = DB.join(renamedStudent, advisor, p -> {
                        return p.get(0).getAsInt() == p.get(4).getAsInt();

                });

                Relation fullJoin = DB.join(studentAdvisor, renamedInstr, p -> {
                        return p.get(5).getAsInt() == p.get(6).getAsInt();
                });
                    
                Relation filter = DB.select(fullJoin, p -> {
                        return p.get(3).getAsInt() == 0;
                });

                List<String> resultattrs = new ArrayList<>();
                resultattrs.add("sName");
                resultattrs.add("iName");
                resultattrs.add("tot_cred");

                Relation result = DB.project(filter, resultattrs);

                List<String> newResultattrs = new ArrayList<>();
                newResultattrs.add("Student");
                newResultattrs.add("Advisor");
                newResultattrs.add("Total_Credits");

                Relation renamedResult = DB.rename(result, result.getAttrs(), newResultattrs);
                renamedResult.print();
        }
}
