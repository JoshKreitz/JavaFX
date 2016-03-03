package StudentOrganizer;

import java.io.Serializable;

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


