package CollegeAdministrationSystem;
public class Attendance {
    int roll;
    String name;
    int year;
    float attendance;
    public Attendance(int roll, String name, int year, float attendance) {
        this.roll = roll;
        this.name = name;
        this.year = year;
        this.attendance = attendance;
    }
    void getdata()
    {
        System.out.println("Enter Roll Number: "+roll);
        System.out.println("Enter Name: "+name);
        System.out.println("Enter Year: "+year);
        System.out.println("Enter Attendance: "+attendance);
}
void showdata()
{
System.out.println("Roll number: "+roll);
System.out.println("Name: "+name);
System.out.println("Year:"+year);
System.out.println("Attendance: "+attendance);
}
}

