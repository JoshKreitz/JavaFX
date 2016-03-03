package StudentOrganizer;

import java.io.Serializable;

//This class is only used to save the students arraylist and allow it to be serialized
public class SerializableList implements Serializable {

    public Student[] students;

    public SerializableList(Object[] students) {
        Student[] studs = new Student[students.length];
        for (int i = 0; i < students.length; i++)
            studs[i] = (Student) students[i];
        this.students = studs;
    }

    public Student[] getStudents() {
        return students;
    }
}


