package com.college;

/**
 * Runner thread/class for CollegeDatabase JDBC operations.
 */
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
