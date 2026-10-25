package DAO;

import Model.Member;
import Util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    public boolean addMember(Member member) throws SQLException {

        String insertSQL =
                "INSERT INTO members " +
                        "(full_name, email, phone, address) " +
                        "VALUES (?, ?, ?, ?)";

        String updateSQL =
                "UPDATE members SET member_code = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int generatedId;

                try (PreparedStatement ps = con.prepareStatement(
                        insertSQL, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, member.getFullName());
                    ps.setString(2, member.getEmail());
                    ps.setString(3, member.getPhoneNumber());
                    ps.setString(4, member.getAddress());

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Could not generate member ID.");
                        }
                        generatedId = rs.getInt(1);
                    }
                }

                String code = String.format("MB%04d", generatedId);

                try (PreparedStatement ps =
                             con.prepareStatement(updateSQL)) {
                    ps.setString(1, code);
                    ps.setInt(2, generatedId);
                    ps.executeUpdate();
                }

                con.commit();

                member.setId(generatedId);
                member.setMemberId(code);

                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public List<Member> getAllMembers() throws SQLException {

        List<Member> members = new ArrayList<>();

        String sql = "SELECT * FROM members ORDER BY id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Member member = new Member();

                member.setId(rs.getInt("id"));
                member.setMemberId(rs.getString("member_code"));
                member.setFullName(rs.getString("full_name"));
                member.setEmail(rs.getString("email"));
                member.setPhoneNumber(rs.getString("phone"));
                member.setAddress(rs.getString("address"));

                members.add(member);
            }
        }

        return members;
    }

    public boolean updateMember(Member member) throws SQLException {

        String sql =
                "UPDATE members SET full_name=?, email=?, " +
                        "phone=?, address=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, member.getFullName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhoneNumber());
            ps.setString(4, member.getAddress());
            ps.setInt(5, member.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteMember(int id) throws SQLException {

        String sql = "DELETE FROM members WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        }
    }
}