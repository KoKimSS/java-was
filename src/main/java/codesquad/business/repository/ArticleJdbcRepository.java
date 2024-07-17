package codesquad.business.repository;

import codesquad.business.domain.Article;
import codesquad.was.exception.DBException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleJdbcRepository implements ArticleRepository{
    public static ArticleJdbcRepository articleJdbcRepository = new ArticleJdbcRepository();

    @Override
    public List<Article> findAll() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM article";
        try (
                Connection conn = JdbcTemplate.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                String title = rs.getString("title");
                String contents = rs.getString("contents");
                Long userId = rs.getLong("user_id");
                articles.add(Article.FactoryMethod(id, title, contents, userId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

    @Override
    public Article findById(Long key) {
        String sql = "SELECT * FROM article WHERE id = ?";
        try (
                Connection conn = JdbcTemplate.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, key);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Long id = rs.getLong("id");
                String title = rs.getString("title");
                String contents = rs.getString("contents");
                Long userId = rs.getLong("user_id");
                return Article.FactoryMethod(id, title, contents, userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long save(Article value) {
        String sql = "INSERT INTO article (title, contents, user_id) VALUES (?, ?, ?)";
        try (
                Connection conn = JdbcTemplate.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, value.getTitle());
            stmt.setString(2, value.getContents());
            stmt.setLong(3, value.getUserId());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new DBException("Insert failed");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Article creation failed, ID could not be obtained.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("db Exception");
        }
    }

    @Override
    public void deleteById(Long key) {
        String sql = "DELETE FROM article WHERE id = ?";
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
}
