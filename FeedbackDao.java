/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import model.Feedback;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.DBConnection;

public class FeedbackDao {

   

    public static List<Feedback> getAllFeedbacks() {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM Feedback";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Feedback fb = new Feedback(
                        rs.getInt("feedbackID"),
                        rs.getInt("customerID"),
                        rs.getInt("serviceID"),
                        rs.getInt("rating"),
                        rs.getString("comments")
                );
                list.add(fb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Feedback getFeedbackById(int id) {
        String sql = "SELECT * FROM Feedback WHERE feedbackID=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Feedback(
                            rs.getInt("feedbackID"),
                            rs.getInt("customerID"),
                            rs.getInt("serviceID"),
                            rs.getInt("rating"),
                            rs.getString("comments")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }
    
     public static boolean addFeedback(int cid, int sid, int r, String comment) {
        String query = "INSERT INTO Feedback(customerID,serviceID,rating, comments) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, cid);
            ps.setInt(2, sid);
            ps.setInt(3, r);
            ps.setString(4, comment);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
