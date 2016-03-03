package StudentOrganizer;

import java.io.Serializable;
import java.util.ArrayList;

//This handles the student's information
public class Student implements Serializable {

    private String name;
    private ArrayList<Data> data;

    public Student(String name, String... studentData) {
        this.name = name;
        data = new ArrayList<>();

        String[] temp;
        for (String x : studentData) {
            temp = x.split(":");
            data.add(new Data(temp[0], temp[1]));
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

    public String getName() {
        return name;
    }

    public boolean equals(String student) {
        return name.equals(student);
    }
}
