package com.college;
class CollegeDatabase
{
    void runOperations() 
    {
        if(true) 
            {
        System.out.println("Running database operations...");
        } 
        else 
            {
            System.out.println("No operations to run.");
        }
        System.out.println("Database operations completed.");
    }
    void closeDatabase() 
    {
        System.out.println("Closing database...");
    }
    void closeConnection() 
    {
        System.out.println("Closing connection...");
    }
    void closeStatement() 
    {
        System.out.println("Closing statement...");
}
void closeResultSet()
{
    System.out.println("Closing result set...");
}
void printDatabaseStatus() 
{
    System.out.println("Database status: Closed");
}
void printConnectionStatus() 
{
    System.out.println("Connection status: Closed");
}
void printStatementStatus() 
{
    System.out.println("Statement status: Closed");
}
void printResultSetStatus() 
{
    System.out.println("Result set status: Closed");
}
}
public class CollegeDatabaseThread {
    public static void main(String[] args) {
            CollegeDatabase collegeDatabase = new CollegeDatabase();
            collegeDatabase.runOperations();
            collegeDatabase.closeDatabase();
            collegeDatabase.closeConnection();
            collegeDatabase.closeStatement();
            collegeDatabase.closeResultSet();
            collegeDatabase.printDatabaseStatus();
            collegeDatabase.printConnectionStatus();
            collegeDatabase.printStatementStatus();
            collegeDatabase.printResultSetStatus();
            System.out.println("All operations completed successfully.");
    }
}
