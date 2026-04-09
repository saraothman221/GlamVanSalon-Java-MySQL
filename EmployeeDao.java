package Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Employee;
import util.DBConnection;

public class EmployeeDao {

    // ----------------------------------------------------------
    // INSERT EMPLOYEE
    // ----------------------------------------------------------
    public static boolean addEmployee(Employee emp) {
        String sql = "INSERT INTO Employee (employeeID, specialization, salary, phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, emp.getEmployeeId());
            stmt.setString(2, emp.getSpecialization());
            stmt.setDouble(3, emp.getSalary());
            stmt.setString(4, emp.getPhone());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ----------------------------------------------------------
    // UPDATE ALL EMPLOYEE DATA (specialization, salary, phone)
    // ----------------------------------------------------------
    public static boolean updateEmployee(Employee emp) {
        String sql = "UPDATE Employee SET specialization = ?, salary = ?, phone = ? WHERE employeeID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emp.getSpecialization());
            stmt.setDouble(2, emp.getSalary());
            stmt.setString(3, emp.getPhone());
            stmt.setInt(4, emp.getEmployeeId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ----------------------------------------------------------
    // DELETE EMPLOYEE
    // ----------------------------------------------------------
    public static boolean deleteEmployee(int employeeId) {
        String sql = "DELETE FROM Employee WHERE employeeID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ----------------------------------------------------------
    // CHECK DUPLICATE PHONE (Exclude employee)
    // ----------------------------------------------------------
    public static boolean phoneExists(String phone, int excludeUserId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Employee WHERE phone = ? AND employeeID <> ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        ps.setString(1, phone);
        ps.setInt(2, excludeUserId);

        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }

    // ----------------------------------------------------------
    // GET EMPLOYEE BY ID
    // ----------------------------------------------------------
    public static Employee getEmployeeById(int id) throws SQLException {
        String sql = "SELECT * FROM Employee WHERE employeeID = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Employee(
                rs.getInt("employeeID"),
                rs.getString("specialization"),
                rs.getDouble("salary"),
                rs.getString("phone")
            );
        }
        return null;
    }

    // ----------------------------------------------------------
    // UPDATE ONLY PHONE
    // ----------------------------------------------------------
    public static boolean updateEmployeePhone(int employeeId, String phone) throws SQLException {
        String sql = "UPDATE Employee SET phone = ? WHERE employeeID = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        ps.setString(1, phone);
        ps.setInt(2, employeeId);

        return ps.executeUpdate() > 0;
    }

    // ----------------------------------------------------------
    // LIST ALL EMPLOYEES
    // ----------------------------------------------------------
    public static List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();

        String sql = "SELECT * FROM Employee";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Employee emp = new Employee();
                emp.setEmployeeId(rs.getInt("employeeID"));
                emp.setSpecialization(rs.getString("specialization"));
                emp.setSalary(rs.getDouble("salary"));
                emp.setPhone(rs.getString("phone")); // NEW

                employees.add(emp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }
}
