package com.meta.dao;

import com.meta.exception.DataInvalidException;
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
 * Gets the implementation of the linkedin post service.
 * </p>
 *
 * @author Anbu
 * @version 1.0
 */
public class PostDaoImpl implements DAO<Post> {

    private static final DatabaseConnection DATABASE_CONNECTION = DatabaseConnection.getDatabaseConnectionInstance();
    private static PostDaoImpl postDao;

    private PostDaoImpl() {
    }

    /**
     * <p>
     * Gets the {@link PostDaoImpl} instance.
     * </p>
     *
     * @return model.Post Dao impl instance.
     */
    public static PostDaoImpl getDaoImplInstance() {
        if (postDao == null) {
            postDao = new PostDaoImpl();
        }

        return postDao;
    }

    /**
     * {@inheritDoc}
     *
     * @param post The {@link Post} to update.
     * @return True if the post was updated successfully, false otherwise.
     */
    @Override
    public boolean update(final Post post) {
        try (Connection connection = DATABASE_CONNECTION.getConnection()) {
            if (updatePost(connection, post) != 0) {
                return true;
            }
        } catch (SQLException | IOException exception) {
            throw new DataInvalidException("Please Enter valid details");
        }

        return false;
    }

    /**
     * <p>
     * Updates the existing post.
     * </p>
     *
     * @param connection To connect the database object.
     * @param post       The {@link Post} to be updated.
     * @throws SQLException
     */
    private int updatePost(final Connection connection, final Post post) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE POST SET MESSAGE = ? WHERE ID = ? AND PROFILE_ID = ?");

        preparedStatement.setString(1, post.getMessage());
        preparedStatement.setLong(2, post.getId());
        preparedStatement.setLong(3, post.getProfileId());

        return preparedStatement.executeUpdate();
    }

    /**
     * {@inheritDoc}
     *
     * @param id The id of the post to get.
     * @return The {@link Post} with the specified id, or null if no such post exists.
     */
    @Override
    public Post get(final Long id) {
        try (Connection connection = DATABASE_CONNECTION.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT P.MESSAGE, P.ID, P.PROFILE_ID FROM POST P WHERE P.ID = ? ");

            preparedStatement.setLong(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final String message = resultSet.getString("MESSAGE");
                final Long postId = resultSet.getLong("ID");
                final Long profileId = resultSet.getLong("PROFILE_ID");
                final Post post = new Post();

                post.setId(postId);
                post.setMessage(message);
                post.setProfileId(profileId);

                return post;
            }
        } catch (SQLException | IOException exception) {
            throw new DataInvalidException("Please Enter valid details");
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @param id The id of the post to delete.
     * @return True if the post was deleted successfully, false otherwise.
     */
    @Override
    public boolean delete(final Long id) {
        try (Connection connection = DATABASE_CONNECTION.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM POST WHERE ID = ? RETURNING ID");

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
     * {@inheritDoc}
     *
     * @param post The {@link Post} to create.
     * @return The id of the post
     */
    @Override
    public Long create(final Post post) {
        try (Connection connection = DATABASE_CONNECTION.getConnection()) {
            return createPost(connection, post);
        } catch (SQLException | IOException exception) {
            throw new DataInvalidException("Please Enter valid details");
        }
    }

    /**
     * <p>
     * Creates the like option.
     * </p>
     *
     * @param connection To connect the database object.
     * @param post       The {@link Post} to be created.
     * @return The id of the post.
     * @throws SQLException
     */
    private Long createPost(final Connection connection, final Post post) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO POST(PROFILE_ID, MESSAGE) VALUES (?,?) RETURNING ID");

        preparedStatement.setLong(1, post.getProfileId());
        preparedStatement.setString(2, post.getMessage());
        final ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            return resultSet.getLong("ID");
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return A {@link Collection} of all posts, keyed by their id.
     */
    @Override
    public Collection<Post> getAll(final Long id) {
        try (Connection connection = DATABASE_CONNECTION.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM POST WHERE PROFILE_ID = ? ");

            preparedStatement.setLong(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final Collection<Post> collection = new ArrayList<>();

            while (resultSet.next()) {
                final String message = resultSet.getString("MESSAGE");
                final Long postId = resultSet.getLong("ID");
                final Long profileId = resultSet.getLong("PROFILE_ID");
                final Post post = new Post();

                post.setId(postId);
                post.setMessage(message);
                post.setProfileId(profileId);
                post.setLinkedinProfile(selectProfile(connection, profileId));
                post.setPostLikeCount(postLikeCount(connection, postId));
                post.setCommentCount(commentCount(connection, postId));

                collection.add(post);
            }

            return collection;
        } catch (SQLException | IOException e) {
            throw new DataInvalidException("Please Enter valid details");
        }
    }

    private LinkedinProfile selectProfile(final Connection connection, final Long profileId) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID, SKILLS, EXPERIENCE, EDUCATION, NAME FROM PROFILE WHERE ID = ? ");

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

    private int postLikeCount(final Connection connection, final Long postId) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("SELECT IS_LIKE, COUNT(IS_LIKE) FROM LIKES AS L INNER JOIN POST_LIKE AS P ON L.ID = P.LIKE_ID WHERE P.POST_ID = ? GROUP BY L.ID HAVING (IS_LIKE) = TRUE ");

        preparedStatement.setLong(1, postId);
        final ResultSet resultSet = preparedStatement.executeQuery();
        final Collection<Boolean> collection = new ArrayList<>();

        while (resultSet.next()) {
            final boolean isLike = resultSet.getBoolean("IS_LIKE");

            collection.add(isLike);
        }

        return collection.size();
    }

    private int commentCount(final Connection connection, final Long postId) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID FROM COMMENT WHERE POST_ID = ?");

        preparedStatement.setLong(1, postId);
        final ResultSet resultSet = preparedStatement.executeQuery();
        final Collection<Long> collection = new ArrayList<>();

        while (resultSet.next()) {
            final Long id = resultSet.getLong("ID");

            collection.add(id);
        }

        return collection.size();
    }
}
