/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import model.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.DBConnection;

public class ServiceDao {

    public static List<Service> getAllServices() {
        List<Service> Service = new ArrayList<>();
        String query = "SELECT * FROM Service";

        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Service service = new Service();
                service.setServiceID(rs.getInt("serviceID"));
                service.setName(rs.getString("name"));
                service.setPrice(rs.getDouble("price"));
                Service.add(service);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Service;
    }

    public static boolean addService(String name, double price) {
        String query = "INSERT INTO Service(name, price) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, name);
            ps.setDouble(2, price);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateService(int id, String name, double price) {

        String query = "UPDATE Service SET name=?, price=? WHERE serviceID=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteService(int id) {
        String query = "DELETE FROM Service WHERE serviceID=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
     public static int getServicePriceByID(int id) throws SQLException {
        String query = "SELECT price FROM Service WHERE serviceID = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("price");
            } else {
                throw new SQLException("Service not found: " + id);
            }
        }
    }
}
