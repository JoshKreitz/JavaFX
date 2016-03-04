package StudentOrganizer;

import java.io.Serializable;
import java.util.ArrayList;

//This handles the student's information
public class Student implements Serializable {

    private String name;
    private ArrayList<Data> data;

    //instatiates the student. First variable is the name/identifier, and the strings coming after it are the data in "key:val" format
    public Student(String name, String... studentData) {
        this.name = name;
        data = new ArrayList<>();

        String[] temp;
        for (String x : studentData) {
            temp = x.split(":");
            data.add(new Data(temp[0], temp[1].replaceAll("~~__123", "")));
        }
    }

    public ArrayList<Data> getData() {
        return data;
    }

    public String toString() {
        String output = name + "  ";
        for (Data x : data)
            output += x + ", ";
        return output;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean equals(String student) {
        return name.equals(student);
    }
}
