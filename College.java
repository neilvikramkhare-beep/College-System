package CollegeAdministrationSystem;
class College {
    private String name;
    private String location;
    private int establishedYear;
    public College(String name, String location, int establishedYear) {
        this.name = name;
        this.location = location;
        this.establishedYear = establishedYear;
    }
    public String getName() {
        return name;
    }
    public void addStudent(String student) {
        System.out.println("Student " + student + " added to the college.");
    }
    public void addCourse(String course) {
        System.out.println("Course " + course + " added to the college.");
    }
    public void addFaculty(String faculty) {
        System.out.println("Faculty " + faculty + " added to the college.");
    }
    public void addDepartment(String department) {
        System.out.println("Department " + department + " added to the college.");
    }
    void getdata()
    {
        System.out.println("College Name: " + name);
        System.out.println("Location: " + location);
        System.out.println("Established Year: " + establishedYear);
    }
    void showdata()
    {
        System.out.println("Name:"+ name);
        System.out.println("Location:"+ location);
        System.out.println("Established Year:"+ establishedYear);
    }
}
