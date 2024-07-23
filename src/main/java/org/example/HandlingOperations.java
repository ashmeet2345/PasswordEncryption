package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HandlingOperations {
    private static Connection con;
    public static Passwords passwords;
    public HandlingOperations(Connection connection){
        this.con=connection;
        passwords=new Passwords();
    }

    public boolean checkPassword(String email,String enteredPassword){
        String query="SELECT PASSWORD_HASH FROM PASSWORD WHERE EMAIL = ?";
        String retrievedPassword="";
        try(PreparedStatement ps=con.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                retrievedPassword=rs.getString("PASSWORD_HASH");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if(retrievedPassword.equals(enteredPassword)){
            return true;
        } else return false;
    }


    public boolean saveNewPassword(String email, String password, String salt){
        String query="INSERT INTO PASSWORD(EMAIL,SALT,PASSWORD_HASH) VALUES(?,?,?)";
        try(PreparedStatement ps=con.prepareStatement(query)) {
            ps.setString(1,email);
            ps.setString(2,salt);
            ps.setString(3,password);
            int rs=ps.executeUpdate();
            if(rs > 0){
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean updatePassword(String email, String password, String salt){
        String query="UPDATE PASSWORD SET PASSWORD_HASH = ?, SALT = ? WHERE EMAIL = ?";
        try(PreparedStatement ps=con.prepareStatement(query)) {
            ps.setString(1, password);
            ps.setString(2,salt);
            ps.setString(3,email);
            int rs=ps.executeUpdate();
            if(rs > 0){
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public String saltRetrieval(String email){
        String query="SELECT SALT FROM PASSWORD WHERE EMAIL=?";
        String salt="";
        try(PreparedStatement ps=con.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                salt=rs.getString("SALT");
                if(salt.isBlank()){
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return salt;
    }
}
