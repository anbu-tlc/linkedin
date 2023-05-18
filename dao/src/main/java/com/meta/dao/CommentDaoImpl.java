package com.meta.dao;

import com.meta.exception.DataInvalidException;
import com.meta.model.Comment;
import com.meta.model.LinkedinProfile;
import com.meta.model.Post;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 * Gets the implementation of the linkedin comment service.
 * </p>
 *
 * @author Anbu
 * @version 1.0
 */
public class CommentDaoImpl implements DAO<Comment> {

    private final static DatabaseConnection DATABASE_CONNECTION = DatabaseConnection.getDatabaseConnectionInstance();
    private static CommentDaoImpl commentDao;

    private CommentDaoImpl() {
    }

    /**
     * <p>
     * Gets the {@link CommentDaoImpl} instance.
     * </p>
     *
     * @return comment dao instance.
     */
    public static CommentDaoImpl getDaoImplInstance() {
        if (commentDao == null) {
            commentDao = new CommentDaoImpl();
        }

        return commentDao;
    }

    /**
     * {@inheritDoc}
     *
     * @param comment The {@link Comment} to create.
     * @return The id of the comment.
     */
    @Override
    public Long create(final Comment comment) {
        try (Connection connection = DATABASE_CONNECTION.getConnection()) {
            return createComment(connection, comment);
        } catch (Exception exception) {
            throw new DataInvalidException("Please Enter valid details");
        }
    }

    /**
     * <p>
     * Creates a new comment in to the table.
     * </p>
     *
     * @param connection To connect the database object.
     * @param comment    The {@link Comment} to inserted into the table.
     * @return The id of the comment.
     * @throws SQLException
     */
    private Long createComment(final Connection connection, final Comment comment) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO COMMENT(POST_ID, PROFILE_ID, MESSAGE) VALUES (?,?,?) RETURNING ID");

        preparedStatement.setLong(1, comment.getPostID());
        preparedStatement.setLong(2, comment.getProfileId());
        preparedStatement.setString(3, comment.getMessage());
        final ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            return resultSet.getLong("ID");
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return A {@link Collection} of all comments, keyed by their id.
     */
    @Override
    public Collection<Comment> getAll(final Long profileId) {
        try (Connection connection = DATABASE_CONNECTION.getConnection()) {

            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM COMMENT WHERE POST_ID = ? ");

            preparedStatement.setLong(1, profileId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final Collection<Comment> collection = new ArrayList<>();

            while (resultSet.next()) {
                final Long post = resultSet.getLong("POST_ID");
                final String message = resultSet.getString("MESSAGE");
                final Long commentId = resultSet.getLong("ID");
                final Comment comment = new Comment();

                comment.setPostId(post);
                comment.setId(commentId);
                comment.setMessage(message);
                comment.setCommentLikeCount(commentLikeCount(connection, commentId));
                comment.setPost(selectPost(connection, profileId));

                collection.add(comment);
            }

            return collection;
        } catch (SQLException | IOException e) {
            throw new DataInvalidException("Please Enter valid details");
        }
    }

    /**
     * @param connection connection To connect the database object.
     * @param postId     The {@link Post} to select from the table.
     * @return {@link Post}
     * @throws SQLException
     */
    private Post selectPost(final Connection connection, final Long postId) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID, MESSAGE, PROFILE_ID FROM POST WHERE ID = ?");

        preparedStatement.setLong(1, postId);
        final ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            final String postMessage = resultSet.getString("MESSAGE");
            final Long profile_id = resultSet.getLong("PROFILE_ID");
            final Long id = resultSet.getLong("ID");
            final Post post = new Post();

            post.setMessage(postMessage);
            post.setProfileId(profile_id);
            post.setId(id);
            post.setLinkedinProfile(selectProfile(connection, profile_id));

            return post;
        }

        return null;
    }

    /**
     * @param connection  To connect the database object.
     * @param profileId  postId The {@link LinkedinProfile} to select from the table.
     * @return {@link LinkedinProfile}
     * @throws SQLException
     */
    private LinkedinProfile selectProfile(final Connection connection, final Long profileId) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM PROFILE WHERE ID = ? ");

        preparedStatement.setLong(1, profileId);
        final ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            final String education = resultSet.getString("EDUCATION");
            final String name = resultSet.getString("NAME");
            final LinkedinProfile linkedinProfile = new LinkedinProfile();

            linkedinProfile.setName(name);
            linkedinProfile.setEducation(education);

            return linkedinProfile;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @param comment The {@link Comment} to update.
     * @return True if the comment was updated successfully, false otherwise.
     */
    @Override
    public boolean update(final Comment comment) {
        try (Connection connection = DATABASE_CONNECTION.getConnection()) {

            if (updateComment(connection, comment) != 0) {
                return true;
            }
        } catch (SQLException | IOException exception) {
            throw new DataInvalidException("Please Enter valid details");
        }

        return false;
    }

    /**
     * <p>
     * Updates the existing comments in the table
     * </p>
     *
     * @param connection To connect the database object.
     * @param comment    The {@link Comment} to updated into the table.
     * @throws SQLException
     */
    private int updateComment(final Connection connection, final Comment comment) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE COMMENT SET MESSAGE = ? WHERE ID = ? AND PROFILE_ID = ? AND POST_ID = ?");

        preparedStatement.setString(1, comment.getMessage());
        preparedStatement.setLong(2, comment.getId());
        preparedStatement.setLong(3, comment.getProfileId());
        preparedStatement.setLong(4, comment.getPostID());

        return preparedStatement.executeUpdate();
    }

    /**
     * {@inheritDoc}
     *
     * @param commentId The id of the comment to get.
     * @return The {@link Comment} with the specified id, or null if no such comment exists.
     */
    @Override
    public Comment get(final Long commentId) {
        try (Connection connection = DATABASE_CONNECTION.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT C.POST_ID, C.MESSAGE, C.ID, C.PROFILE_ID FROM COMMENT C WHERE C.ID = ? ");

            preparedStatement.setLong(1, commentId);
            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final Long post = resultSet.getLong("POST_ID");
                final String message = resultSet.getString("MESSAGE");
                final Long id = resultSet.getLong("ID");
                final Long profileId = resultSet.getLong("PROFILE_ID");
                final Comment comment = new Comment();

                comment.setPostId(post);
                comment.setId(id);
                comment.setMessage(message);
                comment.setProfileId(profileId);

                return comment;
            }
        } catch (SQLException | IOException exception) {
            throw new DataInvalidException("Please Enter valid details");
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @param id The id of the comment to be deleted.
     * @return True if the comment was deleted successfully, false otherwise.
     */
    @Override
    public boolean delete(final Long id) {
        try (Connection connection = DATABASE_CONNECTION.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM COMMENT WHERE ID = ? RETURNING ID");

            preparedStatement.setLong(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return true;
            }
        } catch (SQLException | IOException e) {
            throw new DataInvalidException("Please Enter valid details");
        }

        return false;
    }

    /**
     *<p>
     * Gets the comment like count from the table.
     *</p>
     * @param connection To connect the database object.
     * @param commentId To select the comment like  using comment id.
     * @return Count of the comment like.
     * @throws SQLException
     */
    private int commentLikeCount(final Connection connection, final Long commentId) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("SELECT IS_LIKE, COUNT(IS_LIKE) FROM LIKES AS L INNER JOIN COMMENT_LIKE AS C ON L.ID = C.LIKE_ID WHERE COMMENT_ID = ? GROUP BY L.ID HAVING (IS_LIKE) = TRUE");

        preparedStatement.setLong(1, commentId);
        final ResultSet resultSet = preparedStatement.executeQuery();
        final Collection<Boolean> collection = new ArrayList<>();

        while (resultSet.next()) {
            final boolean isLike = resultSet.getBoolean("IS_LIKE");

            collection.add(isLike);
        }

        return collection.size();
    }
}
