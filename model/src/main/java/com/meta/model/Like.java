package com.meta.model;

import java.util.Objects;

/**
 * <p>
 * Communicates with the Controller to update the data.
 * </p>
 *
 * @author Anbu
 * @version 1.0
 */
public class Like {

    private Long id;
    private Long postId;
    private Long commentId;
    private Long profileId;
    private Reaction reaction;
    private boolean isLike;
    private Post post;
    private Comment comment;

    public void setId(final Long id) {
        this.id = id;
    }

    public void setPostId(final Long postId) {
        this.postId = postId;
    }

    public void setPost(final Post post) {
        this.post = post;
    }

    public void setComment(final Comment comment) {
        this.comment = comment;
    }

    public void setLike(final boolean isLike) {
        this.isLike = isLike;
    }

    public void setCommentId(final Long commentId) {
        this.commentId = commentId;
    }

    public void setProfileId(final Long profileId) {
        this.profileId = profileId;
    }

    public void setReaction(final Reaction reaction) {
        this.reaction = reaction;
    }

    public Long getId() {
        return id;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public Reaction getReaction() {
        return reaction;
    }

    public Post getPost() {
        return post;
    }

    public Comment getComment() {
        return comment;
    }

    public boolean getLike() {
        return isLike;
    }

    public String toString() {
        if (Objects.isNull(getCommentId())) {
            return String.format("model.Like id : %s\nmodel.Reaction : %s\nProfile id :%s%n%s%n", id, reaction, profileId, post);
        } else {
            return String.format("model.Like id : %s\nmodel.Reaction : %s\nProfile id :%s%n%s%n", id, reaction, profileId, comment);
        }
    }
}
