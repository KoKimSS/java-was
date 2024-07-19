package codesquad.business.repository;

import codesquad.business.dao.CommentDao;
import codesquad.business.domain.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static codesquad.business.repository.JdbcTemplate.getConnection;

public class CommentJdbcRepository implements CommentRepository{

    public static CommentJdbcRepository commentJdbcRepository = new CommentJdbcRepository();

    @Override
    public Comment findById(Long key) {
        String query = "SELECT * FROM comment WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, key);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Comment.factoryMethod(
                        resultSet.getLong("id"),
                        resultSet.getString("contents"),
                        resultSet.getLong("user_Id"),
                        resultSet.getLong("poster_Id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long save(Comment value) {
        String query = "INSERT INTO comment (contents, user_Id, poster_Id) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, value.getContents());
            statement.setLong(2, value.getUserId());
            statement.setLong(3, value.getPosterId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteById(Long key) {
        String query = "DELETE FROM comment WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, key);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Comment> findAllByArticleId(Long articleId) {
        String query = "SELECT * FROM comment WHERE poster_Id = ?";
        List<Comment> comments = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, articleId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Comment comment = Comment.factoryMethod(
                        resultSet.getLong("id"),
                        resultSet.getString("contents"),
                        resultSet.getLong("user_Id"),
                        resultSet.getLong("poster_Id")
                );
                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    @Override
    public List<CommentDao> findAllDaoByArticleId(Long articleId) {
        String query = "SELECT c.*, m.username FROM comment c JOIN member m ON c.user_id = m.id WHERE c.poster_id = ?";
        List<CommentDao> comments = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, articleId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                CommentDao comment = new CommentDao(
                        resultSet.getLong("id"),
                        resultSet.getString("contents"),
                        resultSet.getLong("user_id"),
                        resultSet.getString("username"),
                        resultSet.getLong("poster_id")
                );
                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

}
