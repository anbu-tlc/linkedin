package com.meta.model;

/**
 * <p>
 * Communicates with the Controller to update the data.
 * </p>
 *
 * @author Anbu
 * @version 1.0
 */
public class Post {

    private Long id;
    private String message;
    private Long profileId;
    private int postLikeCount;
    private int commentCount;
    private LinkedinProfile linkedinProfile;

    public void setId(final Long id) {
        this.id = id;
    }

    public void setPostLikeCount(final int postLikeCount) {
        this.postLikeCount = postLikeCount;
    }

    public void setCommentCount(final int commentCount) {
        this.commentCount = commentCount;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setProfileId(final Long profileId) {
        this.profileId = profileId;
    }

    public void setLinkedinProfile(final LinkedinProfile linkedinProfile) {
        this.linkedinProfile = linkedinProfile;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Long getProfileId() {
        return profileId;
    }

    public LinkedinProfile getLinkedinProfile() {
        return linkedinProfile;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getPostLikeCount() {
        return postLikeCount;
    }

    public String toString() {
        if (getCommentCount() == 0 && getPostLikeCount() == 0) {
            return String.format("model.Post id : %s%nPost Message : %s%n%s%n", id, message, linkedinProfile);
        } else if (getPostLikeCount() == 0 && getCommentCount() != 0) {
            return String.format("model.Post id : %s%nPost Message : %s%nComment Count :%s%n%s%n", id, message, commentCount, linkedinProfile);
        } else if (getPostLikeCount() != 0 && getCommentCount() == 0) {
            return String.format("model.Post id : %s%nPost Message : %s%nPost model.Like Count :%s%n%s%n", id, message, postLikeCount, linkedinProfile);
        } else {
            return String.format("model.Post id : %s%nPost Message : %s%nPost model.Like Count :%s%nComment Count :%s%n%s%n", id, message, postLikeCount, commentCount, linkedinProfile);
        }
    }
}
