package com.meta.dao;

import com.meta.exception.DataInvalidException;
import com.meta.model.Comment;
import com.meta.model.Like;
import com.meta.model.LinkedinProfile;
import com.meta.model.Post;
import com.meta.model.Reaction;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * <p>
 * Gets the implementation of the linkedin like service.
 * </p>
 *
 * @author Anbu
 * @version 1.0
 */
public class LikeDaoImpl extends AbstractLikeDao<Like> {

    private static final DatabaseConnection DATABASE_CONNECTION = DatabaseConnection.getDatabaseConnectionInstance();
    private static LikeDaoImpl likeDao;

    private LikeDaoImpl() {
    }

    /**
     * <p>
     * Gets the {@link LikeDaoImpl} instance.
     * </p>
     *
     * @return model.Like dao instance.
     */
    public static LikeDaoImpl getDaoImplInstance() {
        if (likeDao == null) {
            likeDao = new LikeDaoImpl();
        }

        return likeDao;
    }

    /**
     * {@inheritDoc}
     *
     * @param like The {@link Like} to update.
     * @return True if the like was updated successfully, false otherwise.
     */
    @Override
    public boolean update(final Like like) {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @param id The id of the like to get.
     * @return The {@link Like} with the specified id, or null if no such like exists.
     */
    @Override
    public Like get(final Long id) {
        try (final Connection connection = DATABASE_CONNECTION.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT L.ID, L.REACTION, L.PROFILE_ID, C.POST_ID, C.COMMENT_ID FROM LIKES L LEFT JOIN POST_LIKE P ON L.ID = P.LIKE_ID LEFT JOIN COMMENT_LIKE C ON L.ID = C.LIKE_ID WHERE L.ID = ?");

            preparedStatement.setLong(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final String type = resultSet.getString("REACTION");
                final Reaction reaction = Reaction.valueOf(type);
                final Long profileId = resultSet.getLong("PROFILE_ID");
                final Long postId = resultSet.getLong("POST_ID");
                final Long commentId = resultSet.getLong("COMMENT_ID");
                final Long likeId = resultSet.getLong("ID");
                final Like like = new Like();

                like.setReaction(reaction);
                like.setId(postId);
                like.setCommentId(commentId);
                like.setProfileId(profileId);
                like.setId(likeId);

                return like;
            }
        } catch (SQLException | IOException exception) {
            throw new DataInvalidException("Please Enter valid details");
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @param id The id of the like to delete.
     * @return True if the like was deleted successfully, false otherwise.
     */
    @Override
    public boolean delete(final Long id) {
        try (final Connection connection = DATABASE_CONNECTION.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM LIKES WHERE ID = ? RETURNING ID");

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
     * @param like The {@link Like} to create.
     * @return The id of the like.
     */
    @Override
    public Long create(final Like like) {
        try (final Connection connection = DATABASE_CONNECTION.getConnection()) {
            return createLike(connection, like);
        } catch (SQLException | IOException exception) {
            throw new DataInvalidException("Please Enter valid details");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return A {@link Collection} of all likes, keyed by their id.
     */
    @Override
    public Collection<Like> getAll(final Long id) {
        try (final Connection connection = DATABASE_CONNECTION.getConnection()) {
            return selectPostLike(connection, id);
        } catch (SQLException | IOException e) {
            throw new DataInvalidException("Please Enter valid details");
        }
    }

    /**
     * <p>
     * Selects the comment like from the table
     * </p>
     *
     * @param connection To connect the database object.
     * @param commentId  The {@link Collection<Like>} to be selected from the table.
     * @param postId     The {@link Collection<Like>} to be selected from the table.
     * @return The {@link Collection<Like>} to selected from the table.
     * @throws SQLException
     */
    private Collection<Like> selectCommentLike(final Connection connection, final Long commentId, final Long postId) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("SELECT L.REACTION, L.IS_LIKE, L.PROFILE_ID, C.POST_ID, C.LIKE_ID, C.COMMENT_ID FROM COMMENT_LIKE C LEFT JOIN LIKES L ON L.ID = C.LIKE_ID WHERE C.COMMENT_ID = ? AND C.POST_ID = ?");

        preparedStatement.setLong(1, commentId);
        preparedStatement.setLong(2, postId);
        final ResultSet resultSet = preparedStatement.executeQuery();
        final Collection<Like> likes = new ArrayList<>();

        while (resultSet.next()) {
            final boolean isLike = resultSet.getBoolean("IS_LIKE");

            if (isLike) {
                final String type = resultSet.getString("REACTION");
                final Reaction reaction = Reaction.valueOf(type);
                final Long profileId = resultSet.getLong("PROFILE_ID");
                final Long likeId = resultSet.getLong("LIKE_ID");
                final Like like = new Like();

                like.setReaction(reaction);
                like.setId(likeId);
                like.setPostId(postId);
                like.setCommentId(commentId);
                like.setProfileId(profileId);
                like.setComment(selectComment(connection, commentId));
                likes.add(like);
            }
        }

        return likes;
    }

    /**
     * <p>
     * Selects the post like from the table
     * </p>
     *
     * @param connection To connect the database object.
     * @param postId     The {@link Collection<Like>} to be selected from the table.
     * @return The {@link Collection<Like>} to selected from the table.
     * @throws SQLException
     */
    private Collection<Like> selectPostLike(final Connection connection, final Long postId) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("SELECT L.REACTION, L.IS_LIKE, L.PROFILE_ID, P.POST_ID, P.LIKE_ID FROM POST_LIKE P LEFT JOIN LIKES L ON L.ID = P.LIKE_ID WHERE POST_ID = ?");

        preparedStatement.setLong(1, postId);
        final ResultSet resultSet = preparedStatement.executeQuery();
        final Collection<Like> likes = new ArrayList<>();

        while (resultSet.next()) {
            final boolean isLike = resultSet.getBoolean("IS_LIKE");

            if (isLike) {
                final String type = resultSet.getString("REACTION");
                final Reaction reaction = Reaction.valueOf(type);
                final Long id = resultSet.getLong("POST_ID");
                final Long profileId = resultSet.getLong("PROFILE_ID");
                final Long likeId = resultSet.getLong("LIKE_ID");
                final Like like = new Like();

                like.setReaction(reaction);
                like.setPostId(id);
                like.setProfileId(profileId);
                like.setId(likeId);
                like.setPost(selectPost(connection, id));
                likes.add(like);
            }
        }

        return likes;
    }

    /**
     * <p>
     * Selects the post from the table
     * </p>
     *
     * @param connection To connect the database object.
     * @param postId     The {@link Post} to be selected from the table.
     * @return The {@link Post} from the table.
     * @throws SQLException
     */
    private Post selectPost(final Connection connection, final Long postId) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID, MESSAGE, PROFILE_ID FROM POST WHERE ID = ?");

        preparedStatement.setLong(1, postId);
        final ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            final String message = resultSet.getString("MESSAGE");
            final Long profileId = resultSet.getLong("PROFILE_ID");
            final Long id = resultSet.getLong("ID");
            final Post post = new Post();

            post.setMessage(message);
            post.setProfileId(profileId);
            post.setId(id);
            post.setLinkedinProfile(selectProfile(connection, profileId));

            return post;
        }

        return null;
    }

    /**
     * <p>
     * Selects the comment from the table
     * </p>
     *
     * @param connection To connect the database object.
     * @param commentId  The {@link Comment} to be selected from the table.
     * @return The {@link Comment} from the table.
     * @throws SQLException
     */
    private Comment selectComment(final Connection connection, final Long commentId) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID, MESSAGE, PROFILE_ID, POST_ID FROM COMMENT WHERE ID = ?");

        preparedStatement.setLong(1, commentId);
        final ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            final String message = resultSet.getString("MESSAGE");
            final Long profileId = resultSet.getLong("PROFILE_ID");
            final Long id = resultSet.getLong("ID");
            final Long postId = resultSet.getLong("POST_ID");
            final Comment comment = new Comment();

            comment.setMessage(message);
            comment.setProfileId(profileId);
            comment.setId(id);
            comment.setPostId(postId);
            comment.setPost(selectPost(connection, postId));

            return comment;
        }

        return null;
    }

    /**
     * <p>
     * Selects the profile from the table
     * </p>
     *
     * @param connection To connect the database object.
     * @param profileId  The {@link LinkedinProfile} to selected.
     * @return The {@link LinkedinProfile} from the table.
     * @throws SQLException
     */
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


    /**
     * <p>
     * Creates the like option.
     * </p>
     *
     * @param connection To connect the database object.
     * @param like       The {@link Like} to be created.
     * @return The id of the like.
     * @throws SQLException
     */
    private Long createLike(final Connection connection, final Like like) throws SQLException {
        if (selectLikePost(connection, like) && Objects.isNull(like.getCommentId())) {

            if (Objects.isNull(like.getReaction())) {
                final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE LIKES SET REACTION = ?, PROFILE_ID = ?, IS_LIKE = ? WHERE LIKES.ID IN (SELECT LIKES.ID FROM LIKES LEFT JOIN POST_LIKE AS P ON LIKES.ID = P.LIKE_ID WHERE P.POST_ID = ?)");

                preparedStatement.setString(1, null);
                preparedStatement.setLong(2, like.getProfileId());
                preparedStatement.setBoolean(3, like.getLike());
                preparedStatement.setLong(4, like.getPostId());
                preparedStatement.executeUpdate();

                return like.getPostId();
            } else {
                final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE LIKES SET REACTION = ?, PROFILE_ID = ?, IS_LIKE = ? WHERE LIKES.ID IN (SELECT LIKES.ID FROM LIKES LEFT JOIN POST_LIKE AS P ON LIKES.ID = P.LIKE_ID WHERE P.POST_ID = ?)");

                preparedStatement.setString(1, like.getReaction().name());
                preparedStatement.setLong(2, like.getProfileId());
                preparedStatement.setBoolean(3, like.getLike());
                preparedStatement.setLong(4, like.getPostId());
                preparedStatement.executeUpdate();

                return like.getPostId();
            }
        } else if (selectLikeComment(connection, like)) {
            if (Objects.isNull(like.getReaction())) {
                final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE LIKES SET REACTION = ?, PROFILE_ID = ?, IS_LIKE = ? WHERE LIKES.ID IN (SELECT LIKES.ID FROM LIKES LEFT JOIN COMMENT_LIKE AS C ON LIKES.ID = C.LIKE_ID WHERE C.COMMENT_ID = ?)");

                preparedStatement.setString(1, null);
                preparedStatement.setLong(2, like.getProfileId());
                preparedStatement.setBoolean(3, like.getLike());
                preparedStatement.setLong(4, like.getCommentId());
                preparedStatement.executeUpdate();

                return like.getCommentId();
            } else {
                final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE LIKES SET REACTION = ?, PROFILE_ID = ?, IS_LIKE = ? WHERE LIKES.ID IN (SELECT LIKES.ID FROM LIKES LEFT JOIN COMMENT_LIKE AS C ON LIKES.ID = C.LIKE_ID WHERE C.COMMENT_ID = ?)");

                preparedStatement.setString(1, like.getReaction().name());
                preparedStatement.setLong(2, like.getProfileId());
                preparedStatement.setBoolean(3, like.getLike());
                preparedStatement.setLong(4, like.getCommentId());
                preparedStatement.executeUpdate();

                return like.getCommentId();
            }
        } else {
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO LIKES(REACTION, PROFILE_ID, IS_LIKE) VALUES (?,?,?) RETURNING ID");

            preparedStatement.setString(1, like.getReaction().name());
            preparedStatement.setLong(2, like.getProfileId());
            preparedStatement.setBoolean(3, like.getLike());
            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final Long id = resultSet.getLong("ID");

                if (Objects.isNull(like.getCommentId())) {

                    if (selectPostLike(connection, like)) {
                        createPostLike(connection, like.getPostId(), id);

                        return id;
                    }
                } else {

                    if (selectCommentLike(connection, like)) {
                        createCommentLike(connection, like.getPostId(), id, like.getCommentId());

                        return id;
                    }
                }
            }
        }

        return null;
    }

    /**
     * <p>
     * Selects the post like from the table.
     * </p>
     *
     * @param connection To connect the database object.
     * @param like       The {@link Like} to be selected from the table.
     * @return True, if the post like does not exist.
     * @throws SQLException
     */
    private boolean selectPostLike(final Connection connection, final Like like) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("SELECT P.POST_ID, P.LIKE_ID, L.PROFILE_ID, L.ID, L.IS_LIKE FROM POST_LIKE P LEFT JOIN LIKES L ON L.ID = P.LIKE_ID");
        final ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            final Long id = resultSet.getLong("ID");
            final Long likeId = resultSet.getLong("LIKE_ID");
            final Long profileId = resultSet.getLong("PROFILE_ID");
            final Long postId = resultSet.getLong("POST_ID");

            if (id.equals(likeId) && profileId.equals(like.getProfileId()) && postId.equals(like.getPostId())) {
                return false;
            }
        }

        return true;
    }

    /**
     * <p>
     * Selects the comment likes from the table.
     * </p>
     *
     * @param connection connection To connect the database object.
     * @param like       The {@link Like} to be selected from the table.
     * @return True, if the comment like does not exist.
     * @throws SQLException
     */
    private boolean selectCommentLike(final Connection connection, final Like like) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("SELECT C.POST_ID, C.LIKE_ID, C.COMMENT_ID, L.PROFILE_ID, L.ID FROM COMMENT_LIKE C LEFT JOIN LIKES L ON L.ID = C.LIKE_ID");
        final ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            final Long id = resultSet.getLong("ID");
            final Long likeId = resultSet.getLong("LIKE_ID");
            final Long profileId = resultSet.getLong("PROFILE_ID");
            final Long postId = resultSet.getLong("POST_ID");
            final Long commentId = resultSet.getLong("COMMENT_ID");

            if (id.equals(likeId) && profileId.equals(like.getProfileId()) && postId.equals(like.getPostId()) && commentId.equals(like.getCommentId())) {
                return false;
            }
        }

        return true;
    }

    /**
     * <p>
     * Selects the post likes from the table.
     * </p>
     *
     * @param connection connection connection To connect the database object.
     * @param like       The {@link Like} to be selected from the table.
     * @return True, if the comment like does not exist.
     * @throws SQLException
     */
    private boolean selectLikePost(final Connection connection, final Like like) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("SELECT L.ID, L.IS_LIKE, L.PROFILE_ID, P.POST_ID, P.LIKE_ID FROM POST_LIKE P LEFT JOIN LIKES L ON L.ID = P.LIKE_ID WHERE PROFILE_ID = ?");

        preparedStatement.setLong(1, like.getProfileId());
        final ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            final Long id = resultSet.getLong("PROFILE_ID");
            final Long postId = resultSet.getLong("POST_ID");

            if (like.getProfileId().equals(id) && like.getPostId().equals(postId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * <p>
     * Selects the comment likes from the table.
     * </p>
     *
     * @param connection connection connection connection To connect the database object.
     * @param like       like The {@link Like} to be selected from the table.
     * @return True, if the comment like does not exist.
     * @throws SQLException
     */
    private boolean selectLikeComment(final Connection connection, final Like like) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("SELECT C.POST_ID, C.LIKE_ID, C.COMMENT_ID, L.PROFILE_ID, L.IS_LIKE, L.ID FROM COMMENT_LIKE C LEFT JOIN LIKES L ON L.ID = C.LIKE_ID WHERE PROFILE_ID = ?");

        preparedStatement.setLong(1, like.getProfileId());
        final ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            final Long id = resultSet.getLong("PROFILE_ID");
            final Long postId = resultSet.getLong("POST_ID");
            final Long commentId = resultSet.getLong("COMMENT_ID");

            if (like.getProfileId().equals(id) && like.getPostId().equals(postId) && like.getCommentId().equals(commentId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * <p>
     * Creates the post like.
     * </p>
     *
     * @param connection To connect the database object.
     * @param postId     The post id added into the post table
     * @param likeId     The like id added into the post table
     * @throws SQLException
     */
    private void createPostLike(final Connection connection, final Long postId, final Long likeId) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO POST_LIKE(LIKE_ID, POST_ID) VALUES (?,?)");

        preparedStatement.setLong(1, likeId);
        preparedStatement.setLong(2, postId);
        preparedStatement.executeUpdate();
    }

    /**
     * <p>
     * Creates the comment like.
     * </p>
     *
     * @param connection To connect the database object.
     * @param postId     The post id added into the comment table.
     * @param likeId     The like id added into the comment table.
     * @param commentId  The comment id added into the comment table.
     * @throws SQLException
     */
    private void createCommentLike(final Connection connection, final Long postId, final Long likeId, final Long commentId) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO COMMENT_LIKE(LIKE_ID, POST_ID, COMMENT_ID) VALUES (?,?,?)");

        preparedStatement.setLong(1, likeId);
        preparedStatement.setLong(2, postId);
        preparedStatement.setLong(3, commentId);
        preparedStatement.executeUpdate();
    }

    /**
     * @param commentId retrieve the data for the corresponding comment id.
     * @param postId    retrieve the data for the corresponding post id.
     * @return {@link Collection<Like>} of likes.
     */
    @Override
    public Collection<Like> getAll(final Long commentId, final Long postId) {
        try (final Connection connection = DATABASE_CONNECTION.getConnection()) {
            return selectCommentLike(connection, commentId, postId);
        } catch (SQLException | IOException e) {
            throw new DataInvalidException("Please Enter valid details");
        }
    }
}

