package CollegeAdministrationSystem;
public class Examination {
    String name;
    int roll;
    String[] subjects;
    int[] marks;
    String examName;
    Examination(String name, int roll, String[] subjects, int[] marks, String examName) {
        this.name = name;
        this.roll = roll;
        this.subjects = subjects;
        this.marks = marks;
        this.examName = examName;
    }
    void getdata()
    {
    System.out.println("Enter the name:"+name);
    System.out.println("Enter the roll number:"+roll);
    System.out.println("Enter the subjects:"+subjects);
    System.out.println("Enter the marks:"+marks);
    System.out.println("Enter the type of exam:"+examName);
}
void showdata()
{
    System.out.println("Name:"+name);
    System.out.println("Roll number:"+roll);
    System.out.println("Subjects:"+subjects);
    System.out.println("Marks:"+marks);
    System.out.println("Type of exam:"+examName);
}
}
