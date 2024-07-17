package codesquad.business.repository;

import codesquad.business.domain.Member;
import codesquad.was.exception.DBException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberJdbcRepository implements MemberRepository {
    public static JdbcTemplate jdbcTemplate = JdbcTemplate.jdbcTemplate;

    public static MemberJdbcRepository memberJdbcRepository = new MemberJdbcRepository();

    @Override
    public Member findById(Long key) {
        String sql = "SELECT * FROM member WHERE id = ?";
        try (
                Connection conn = JdbcTemplate.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, key);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Long id = rs.getLong("id");
                String userId = rs.getString("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                return Member.factoryMethod(id, userId, username, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long save(Member value) {
        String sql = "INSERT INTO member (user_id, username, password) VALUES (?, ?, ?)";
        try (
                Connection conn = JdbcTemplate.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, value.getUserId());
            stmt.setString(2, value.getUsername());
            stmt.setString(3, value.getPassword());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new DBException("Insert failed");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    System.out.println("첫번째 값 존재");
                    return generatedKeys.getLong(1);
                }else {
                    throw new DBException("Insert failed");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBException("db Exception");
        }
    }


    @Override
    public void deleteById(Long key) {
        String sql = "DELETE FROM member WHERE id = ?";
        try (
                Connection conn = JdbcTemplate.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, key);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("db Exception: " + e.getMessage());
        }
    }

    @Override
    public boolean isExists(String userId) {
        String sql = "SELECT COUNT(*) FROM member WHERE user_id = ?";
        try (
                Connection conn = JdbcTemplate.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DBException("db Exception: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Member findById(String userId) {
        String sql = "SELECT * FROM member WHERE user_id = ?";
        try (
                Connection conn = JdbcTemplate.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Long id = rs.getLong("id");
                String userIdFromDb = rs.getString("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                return Member.factoryMethod(id, userIdFromDb, username, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM member";
        try (
                Connection conn = JdbcTemplate.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                String userId = rs.getString("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                members.add(Member.factoryMethod(id, userId, username, password));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }


}
