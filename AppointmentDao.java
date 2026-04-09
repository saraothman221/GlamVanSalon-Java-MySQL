/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */ 
package Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.Appointment;
import model.User;
import util.DBConnection;

public class AppointmentDao {

    // Method to create an appointment
    public static boolean createAppointment(int customerID, int serviceID, java.sql.Date date, java.sql.Time time) throws SQLException {
        String query = "INSERT INTO Appointment (customerID, serviceID, date, time, status) VALUES (?, ?, ?, ?, 'Scheduled')";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerID);
            stmt.setInt(2, serviceID);
            stmt.setDate(3, date);
            stmt.setTime(4, time);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // Return true if appointment was successfully inserted
        }
    }

    // Method to get service ID by service name
    public int getServiceIdByName(String serviceName) throws SQLException {
        String query = "SELECT serviceID FROM Service WHERE serviceName = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, serviceName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("serviceID");
            } else {
                throw new SQLException("Service not found: " + serviceName);
            }
        }
    }

    public static int[] getLastInsertedAppointmentID(String username) {
        int[] res = new int[2];
        String sql = """
                     SELECT A.appointmentID ,A.serviceID
                             FROM Appointment A 
                     JOIN User C ON A.customerID = C.UserID
                                     WHERE C.username = ? ORDER BY A.appointmentID DESC LIMIT 1""";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                res[0] = rs.getInt("appointmentID");
                res[1] = rs.getInt("serviceID");
                return res;
                
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Indicates failure
    }

    public void addAppointment(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO Appointment (customerID, employeeID, serviceID, date, time, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointment.getCustomerID());
            stmt.setInt(2, appointment.getEmployeeID());
            stmt.setInt(3, appointment.getServiceID());
            stmt.setDate(4, appointment.getDate());
            stmt.setTime(5, appointment.getTime());
            stmt.setString(6, appointment.getStatus());
            stmt.executeUpdate();
        }
    }

    public static void saveAppointment(int customerID, int employeeID, int serviceID, java.util.Date appointmentDate, int appointmentHour) {
        // Convert the appointment date and hour into a time format
        java.sql.Time appointmentTime = new java.sql.Time(appointmentHour, 0, 0);
        java.sql.Date appointmentSqlDate = new java.sql.Date(appointmentDate.getTime());

        String query = "INSERT INTO Appointment (customerID, employeeID, serviceID, date, time) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, customerID);
            ps.setInt(2, employeeID);
            ps.setInt(3, serviceID);
            ps.setDate(4, appointmentSqlDate);
            ps.setTime(5, appointmentTime);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Appointment booked successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error booking appointment: " + ex.getMessage());
        }
    }

    public List<Appointment> getAppointmentsByEmail(String email) throws SQLException {
        System.out.println(email);
        List<Appointment> appointments = new ArrayList<>();
        User u = UserDao.getUserByLogin(email);
        String sql;
        boolean IsCust = false;
        if (u.getRole().equals("Customer")) {
            sql = """
        SELECT a.*,u.role
                FROM Appointment a
                JOIN User u ON a.customerID = u.userID
                WHERE u.email = ?
    """;
            IsCust = true;
        } else {
            sql = """
        SELECT a.*
                FROM Appointment a
                
    """;
        }

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (IsCust) {
                stmt.setString(1, email);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Appointment appt = new Appointment(
                        rs.getInt("appointmentID"),
                        rs.getInt("customerID"),
                        rs.getInt("serviceID"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getString("status")
                );
                appointments.add(appt);
            }
        }

        return appointments;
    }

public static List<Appointment> getAllAppointments() throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM Appointment";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Appointment appt = new Appointment(
                        rs.getInt("appointmentID"),
                        rs.getInt("customerID"),
                        rs.getInt("serviceID"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getString("status")
                );
                appointments.add(appt);
            }
        }
        return appointments;
    }

    public Appointment getAppointmentById(int id) throws SQLException {
        String sql = "SELECT * FROM Appointment WHERE appointmentID = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Appointment(
                        rs.getInt("appointmentID"),
                        rs.getInt("customerID"),
                        rs.getInt("employeeID"),
                        rs.getInt("serviceID"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getString("status")
                );
            }
        }
        return null;
    }

    public void updateAppointment(Appointment appointment) throws SQLException {
        String sql = "UPDATE Appointment SET customerID=?, employeeID=?, serviceID=?, date=?, time=?, status=? WHERE appointmentID=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointment.getCustomerID());
            stmt.setInt(2, appointment.getEmployeeID());
            stmt.setInt(3, appointment.getServiceID());
            stmt.setDate(4, appointment.getDate());
            stmt.setTime(5, appointment.getTime());
            stmt.setString(6, appointment.getStatus());
            stmt.setInt(7, appointment.getAppointmentID());
            stmt.executeUpdate();
        }
    }

    public void deleteAppointment(int appointmentID) throws SQLException {
        String sql = "DELETE FROM Appointment WHERE appointmentID = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentID);
            stmt.executeUpdate();
        }
    }
    public static List<Appointment> getAppointmentsByUsername(String username) throws SQLException {
    List<Appointment> list = new ArrayList<>();

    String sql = """
        SELECT A.*
        FROM Appointment A
        JOIN User U ON A.customerID = U.userID
        WHERE U.username = ?
        ORDER BY A.date, A.time
    """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Appointment a = new Appointment();
            a.setAppointmentID(rs.getInt("appointmentID"));
            a.setCustomerID(rs.getInt("customerID"));
            a.setServiceID(rs.getInt("serviceID"));
            a.setDate(rs.getDate("date"));
            a.setTime(rs.getTime("time"));
            a.setStatus(rs.getString("status"));
            list.add(a);
        }
    }

    return list;
}



}
