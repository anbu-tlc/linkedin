package com.meta.model;

/**
 * <p>
 * Communicates with the Controller to update the data.
 * </p>
 *
 * @author Anbu
 * @version 1.0
 */
public class Comment {

    private Long id;
    private String message;
    private Long postID;
    private Long profileId;
    private int commentLikeCount;
    private Post post;

    public void setId(final Long id) {
        this.id = id;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setPostId(final Long postID) {
        this.postID = postID;
    }

    public void setProfileId(final Long profileId) {
        this.profileId = profileId;
    }

    public void setPost(final Post post) {
        this.post = post;
    }

    public void setCommentLikeCount(final int commentLikeCount) {
        this.commentLikeCount = commentLikeCount;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Long getPostID() {
        return postID;
    }

    public Long getProfileId() {
        return profileId;
    }

    public Post getPost() {
        return post;
    }

    public int getCommentLikeCount() {
        return commentLikeCount;
    }

    public String toString() {
        if (getCommentLikeCount() != 0) {
            return String.format("Comment id : %s%nComment message : %s%nComment like count : %s%n%s%n", id, message, commentLikeCount, post);
        } else {
            return String.format("Comment id : %s%nComment message : %s%n%s%n", id, message, post);
        }
    }
}
