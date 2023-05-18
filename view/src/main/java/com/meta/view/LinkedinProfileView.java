package com.meta.view;

import com.meta.controller.CommentController;
import com.meta.controller.LikeController;
import com.meta.controller.LinkedinController;
import com.meta.controller.PostController;
import com.meta.model.Comment;
import com.meta.model.Like;
import com.meta.model.LinkedinProfile;
import com.meta.model.Post;
import com.meta.model.Reaction;
import com.meta.validation.Validation;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

/**
 * <p>
 * Gets the input from the user.
 * </p>
 *
 * @author Anbu
 * @version 1.0
 */
public class LinkedinProfileView {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final LinkedinController LINKEDIN_CONTROLLER = LinkedinController.getLinkedinControllerInstance();
    private static final Validation VALIDATION = Validation.getValidationInstance();
    private static final PostController POST_CONTROLLER = PostController.getInstance();
    private static final CommentController COMMENT_CONTROLLER = CommentController.getInstance();
    private static final LikeController LIKE_CONTROLLER = LikeController.getInstance();
    private static final Logger LOGGER = Logger.getLogger(LinkedinProfileView.class);

    /**
     * <p>
     * Signs up with new {@link LinkedinProfile}
     * </p>
     */
    private void signUp() {
        final LinkedinProfile profile = new LinkedinProfile();

        getSignUpChoice(profile);
        profile.setPassword(getPassword());
        profile.setName(getName());
        profile.setEducation(getEducation());
        profile.setExperience(getExperience());
        profile.setSkill(getSkill());
        final Long id = LINKEDIN_CONTROLLER.signUp(profile);

        if (Objects.isNull(id)) {
            LOGGER.info("User already exist");
            userChoiceOption();
        } else {
            LOGGER.info("Signup successfully\nYour profile id :" + id);
            userChoice();
        }
    }

    /**
     * <p>
     * Gets the user sign up choice.
     * </p>
     *
     * @param profile Represents the user LinkedIn profile.
     */
    private void getSignUpChoice(final LinkedinProfile profile) {
        try {
            LOGGER.info("Choose 1 login with mobile\nChoose 2 login with email\nChoose any other option to leave the page");
            final int input = Integer.parseInt(SCANNER.nextLine());

            switch (input) {
                case 1:
                    profile.setMobileNumber(getMobileNumber());
                    break;
                case 2:
                    profile.setEmailAddress(getEmailAddress());
                    break;
                default:
                    userChoiceOption();
            }
        } catch (Exception numberFormatException) {
            LOGGER.info("Please enter a valid input");
            userChoiceOption();
        }
    }

    /**
     * <p>
     * Gets the user choice for multiple operation.
     * </p>
     */
    private void userChoice() {
        int input = 0;

        do {

            try {
                LOGGER.info("Choose 1 to update your profile\nChoose 2 to get user profile\nChoose 3 to delete user profile\nChoose 4 to create post\nChoose 5 to update post\nChoose 6 to get all post\nChoose 7 to delete post\nChoose 8 to create comment\nChoose 9 to update comment\nChoose 10 to delete comment\nChoose 11 to get all comment\nChoose 12 to create like\nChoose 13 to get all like\nTo exit choose any other number");
                input = Integer.parseInt(SCANNER.nextLine());

                switch (input) {
                    case 1:
                        updateProfile();
                        break;
                    case 2:
                        getProfile();
                        break;
                    case 3:
                        deleteProfile();
                        break;
                    case 4:
                        createPost();
                        break;
                    case 5:
                        updatePost();
                        break;
                    case 6:
                        getAllPost();
                        break;
                    case 7:
                        deletePost();
                        break;
                    case 8:
                        createComment();
                        break;
                    case 9:
                        updateComment();
                        break;
                    case 10:
                        deleteComment();
                        break;
                    case 11:
                        getAllComment();
                        break;
                    case 12:
                        createLike();
                        break;
                    case 13:
                        getAllLike();
                        break;
                    default:
                        LOGGER.info("Please enter valid input");
                        userChoiceOption();
                }
            } catch (NumberFormatException numberFormatException) {
                LOGGER.info("Please enter a valid input");
            }
        } while (input < 14);
    }

    /**
     * <p>
     * Signs in with the new {@link LinkedinProfile}
     * </p>
     */
    private void signIn() {
        final LinkedinProfile profile = new LinkedinProfile();

        getSignUpChoice(profile);
        profile.setPassword(getPassword());

        if (LINKEDIN_CONTROLLER.signIn(profile)) {
            LOGGER.info("Signin successfully");
            userChoice();
        } else {
            LOGGER.info("Signin failed");
            userChoiceOption();
        }
    }

    /**
     * <p>
     * Updates the existing {@link LinkedinProfile}
     * </p>
     */
    private void updateProfile() {
        final LinkedinProfile profile = new LinkedinProfile();
        final Long id = getProfileId();
        final LinkedinProfile linkedinProfile = LINKEDIN_CONTROLLER.get(id);

        if (Objects.isNull(linkedinProfile)) {
            LOGGER.info("Please enter valid profile id.");
        } else {
            profile.setId(id);
            profile.setExperience(getChoice("experience") ? getExperience() : linkedinProfile.getExperience());
            profile.setSkill(getChoice("skills") ? getSkill() : linkedinProfile.getSkill());
            profile.setName(getChoice("name") ? getName() : linkedinProfile.getName());
            profile.setEducation(getChoice("education") ? getEducation() : linkedinProfile.getEducation());
            profile.setPassword(getChoice("password") ? getPassword() : linkedinProfile.getPassword());

            if (Objects.isNull(linkedinProfile.getMobileNumber())) {
                profile.setMobileNumber(getChoice("mobile number") ? getMobileNumber() : linkedinProfile.getMobileNumber());
                profile.setEmailAddress(linkedinProfile.getEmailAddress());
            } else {
                profile.setEmailAddress(getChoice("email address") ? getEmailAddress() : linkedinProfile.getEmailAddress());
                profile.setMobileNumber(linkedinProfile.getMobileNumber());
            }
            LOGGER.info(LINKEDIN_CONTROLLER.update(profile) ? "update successfully" : "Update failed ");
        }
    }

    /**
     * <p>
     * Gets a linkedin profile by its id.
     * </p>
     */
    private void getProfile() {
        final LinkedinProfile linkedinProfile = LINKEDIN_CONTROLLER.get(getProfileId());

        LOGGER.info((Objects.isNull(linkedinProfile)) ? "Please Enter valid profile id" : linkedinProfile);
    }

    /**
     * <p>
     * Deletes a linkedin profile by its id.
     * </P>
     */
    private void deleteProfile() {
        LOGGER.info(LINKEDIN_CONTROLLER.delete(getProfileId()) ? "We’ve closed your account successfully" : "Please enter a valid id");
    }

    /**
     * <p>
     * Users sign up and sign in choice
     * </p>
     */
    public void userChoiceOption() {
        int input = 0;

        do {

            try {
                LOGGER.info("Choose 1 to sign up\nChoose 2 to sign in\nChoose any other option to leave the page.");
                input = Integer.parseInt(SCANNER.nextLine());

                switch (input) {
                    case 1:
                        signUp();
                        break;
                    case 2:
                        signIn();
                        break;
                    default:
                        SCANNER.close();
                        System.exit(0);
                }
            } catch (NumberFormatException exception) {
                LOGGER.info("Please enter a valid input");
            }
        } while (input < 3);
    }

    /**
     * <p>
     * Gets the user mobile number.
     * </p>
     *
     * @return User Mobile number entered by the user.
     */
    private Long getMobileNumber() {
        try {
            LOGGER.info("Enter mobile number: (You can use 6 to 9 are the first digits of mobile numbers). Press (^) symbol to leave the page.");
            final String input = SCANNER.nextLine();

            handleExitRequest(input);
            final Long mobileNumber = Long.valueOf(input);
            final String number = String.valueOf(mobileNumber);

            if (VALIDATION.isValidMobileNumber(number)) {
                return mobileNumber;
            } else {
                LOGGER.info("Mobile number invalid");
            }
        } catch (NumberFormatException numberFormatException) {
            LOGGER.info("Please enter a valid mobile number:");
        }

        return getMobileNumber();
    }

    /**
     * <p>
     * Gets the user email address.
     * </p>
     *
     * @return User email address entered by the user.
     */
    private String getEmailAddress() {
        LOGGER.info("Enter email address: (You can use letters, numbers, and symbols). Press (^) symbol to leave the page.");
        final String email = SCANNER.nextLine();

        handleExitRequest(email);

        if (VALIDATION.isValidEmail(email)) {
            return email;
        } else {
            LOGGER.info("Please enter a valid email");
        }

        return getEmailAddress();
    }

    /**
     * <p>
     * Gets the user password.
     * </p>
     *
     * @return User password entered by the user.
     */
    private String getPassword() {
        LOGGER.info("Enter password: (You can use 8 or more characters with a mix of capital, small letters, numbers & special characters). Press (^) symbol to leave the page.");
        final String password = SCANNER.nextLine();

        handleExitRequest(password);

        if (VALIDATION.isValidPassword(password)) {
            return password;
        } else {
            LOGGER.info("Please provide a strong password");
        }

        return getPassword();
    }

    /**
     * <p>
     * Gets the user name.
     * </p>
     *
     * @return Username entered by the user.
     */
    private String getName() {
        LOGGER.info("Enter your name: (You can only use letters. Required minimum of two characters).Press (^) symbol to leave the page.");
        final String Name = SCANNER.nextLine();

        handleExitRequest(Name);

        if (VALIDATION.isValidName(Name.trim())) {
            return Name;
        } else {
            LOGGER.info("Please enter a valid name");
        }

        return getName();
    }

    /**
     * <p>
     * Gets the user skills.
     * </p>
     *
     * @return User skills entered by the user.
     */
    private String getSkill() {
        LOGGER.info("Enter your skill: Press (^) symbol to leave the page.");
        final String skill = SCANNER.nextLine();

        handleExitRequest(skill);

        if (VALIDATION.isValidSkill(skill.trim())) {
            return skill;
        } else {
            LOGGER.info("Please enter a valid skill");
        }

        return getSkill();
    }

    /**
     * <p>
     * Gets the user education.
     * </p>
     *
     * @return User education entered by the user.
     */
    private String getEducation() {
        LOGGER.info("Enter your education: Press (^) symbol to leave the page.");
        final String education = SCANNER.nextLine();

        handleExitRequest(education);

        if (VALIDATION.isValidEducation(education.trim())) {
            return education;
        } else {
            LOGGER.info("Please enter a valid education details");
        }

        return getEducation();
    }

    /**
     * <p>
     * Gets the user experience.
     * </p>
     *
     * @return User Experience entered by the user.
     */
    private Double getExperience() {
        try {
            LOGGER.info("Enter your experience in digits : Press (^) symbol to leave the page.");
            final String education = SCANNER.nextLine();

            handleExitRequest(education);
            final Double userEducation = Double.parseDouble(education);
            final String input = String.valueOf(userEducation);

            if (VALIDATION.isValidExperience(input.trim())) {
                return userEducation;
            } else {
                LOGGER.info("Please enter a valid experience");
            }
        } catch (NumberFormatException numberFormatException) {
            LOGGER.info("Please enter a valid experience");
        }

        return getExperience();
    }

    /**
     * <p>
     * Gets the profile id.
     * </p>
     *
     * @return Profile id entered by the user.
     */
    private Long getProfileId() {
        try {
            LOGGER.info("Enter your profile id: Press (^) symbol to leave the page.");
            final String input = SCANNER.nextLine();

            handleExitRequest(input);
            final Long userId = Long.parseLong(input);
            final String id = String.valueOf(userId);

            if (VALIDATION.isValidId(id.trim())) {
                return userId;
            } else {
                LOGGER.info("Please enter a valid id");
            }
        } catch (NumberFormatException numberFormatException) {
            LOGGER.info("Please enter a valid id");
        }

        return getProfileId();
    }

    /**
     * <p>
     * Gets the post id.
     * </p>
     *
     * @return model.Post id entered by the user.
     */
    private Long getPostId() {
        try {
            LOGGER.info("Enter your post id: Press (^) symbol to leave the page.");
            final String input = SCANNER.nextLine();
            final Long userId = Long.parseLong(input);
            final String id = String.valueOf(userId);

            handleExitRequest(id);

            if (VALIDATION.isValidId(id.trim())) {
                return userId;
            } else {
                LOGGER.info("Please enter a valid id");
            }
        } catch (NumberFormatException numberFormatException) {
            LOGGER.info("Please enter a valid id");
        }

        return getPostId();
    }

    /**
     * <p>
     * Gets the comment id.
     * </p>
     *
     * @return model.Comment id entered by the user.
     */
    private Long getCommentId() {
        try {
            LOGGER.info("Enter your comment id: Press (^) symbol to leave the page.");
            final String input = SCANNER.nextLine();
            final Long userId = Long.parseLong(input);
            final String id = String.valueOf(userId);

            handleExitRequest(id);

            if (VALIDATION.isValidId(id.trim())) {
                return userId;
            } else {
                LOGGER.info("Please enter a valid id");
            }
        } catch (NumberFormatException numberFormatException) {
            LOGGER.info("Please enter a valid id");
        }

        return getCommentId();
    }

    /**
     * <p>
     * Handles user's exit request.
     * </p>
     *
     * @param input A string representing the user's input.
     */
    private void handleExitRequest(final String input) {
        if ("^".equals(input.trim())) {
            userChoiceOption();
        }
    }

    /**
     * <p>
     * Handles user's choice option.
     * </p>
     *
     * @return True, if choice is yes otherwise false.
     */
    private boolean getChoice(final String updateName) {
        LOGGER.info("Do you want to update your " + updateName + " ? choose (Yes/No)");
        final String choice = SCANNER.nextLine().toUpperCase().trim();

        if ("YES".equals(choice)) {
            return true;
        } else if ("NO".equals(choice)) {
            return false;
        } else {
            LOGGER.info("Enter valid option, choose (Yes/No).");

            return getChoice(updateName);
        }
    }

    /**
     * <p>
     * Creates the new {@link Post}
     * </p>
     */
    private void createPost() {
        try {
            final Post post = new Post();

            post.setProfileId(getProfileId());
            post.setMessage(getMessage());
            final Long id = POST_CONTROLLER.create(post);

            if (Objects.isNull(id)) {
                LOGGER.info("please enter a valid profile id");
            } else {
                LOGGER.info("model.Post created successfully\nYour post id: " + id);
            }
        } catch (IllegalArgumentException exception) {
            LOGGER.info("please enter a valid option");
        }
    }

    /**
     * <p>
     * Updates the existing {@link Post}
     * </p>
     */
    private void updatePost() {
        final Long id = getPostId();
        final Post existingPost = POST_CONTROLLER.get(id);

        if (Objects.isNull(existingPost)) {
            LOGGER.info("Please enter valid post id");
        } else {
            final Post post = new Post();

            post.setId(id);
            post.setProfileId(getProfileId());
            post.setMessage(getChoice("message") ? getMessage() : existingPost.getMessage());
            LOGGER.info(POST_CONTROLLER.update(post) ? "Successfully post updated" : "Please enter valid profile id");
        }
    }

    /**
     * <p>
     * Deletes a post by its id.
     * </p>
     */
    private void deletePost() {
        LOGGER.info(POST_CONTROLLER.delete(getPostId()) ? "We’ve deleted your post successfully" : "Please enter a valid id");
    }

    /**
     * <p>
     * Gets all post using their profile id.
     * </p>
     */
    private void getAllPost() {
        final Collection<Post> posts = POST_CONTROLLER.getAll(getProfileId());

        LOGGER.info(Objects.isNull(posts) ? "Please enter valid profile id" : posts);
    }

    /**
     * <p>
     * Get the message from the user
     * </p>
     *
     * @return Message entered by the user.
     */
    private String getMessage() {
        LOGGER.info("Enter your message:");

        return SCANNER.nextLine();
    }

    /**
     * <p>
     * Creates the new {@link Comment}
     * </p>
     */
    private void createComment() {
        final Comment comment = new Comment();

        comment.setProfileId(getProfileId());
        comment.setPostId(getPostId());
        comment.setMessage(getMessage());
        final Long id = COMMENT_CONTROLLER.create(comment);

        if (Objects.isNull(id)) {
            LOGGER.info("please enter a valid profile id");
        } else {
            LOGGER.info("Comment created successfully\nYour comment id:" + id);
        }
    }

    /**
     * <p>
     * Updates the existing {@link Comment}
     * </p>
     */
    private void updateComment() {
        final Long id = getCommentId();
        final Comment exitingComment = COMMENT_CONTROLLER.get(id);

        if (Objects.isNull(exitingComment)) {
            LOGGER.info("Please enter valid id");
        } else {
            final Comment comment = new Comment();

            comment.setId(id);
            comment.setPostId(getPostId());
            comment.setProfileId(getProfileId());
            comment.setMessage(getChoice("comment") ? getMessage() : exitingComment.getMessage());
            LOGGER.info(COMMENT_CONTROLLER.update(comment) ? "Successfully comment updated" : "Please enter valid id");
        }
    }

    /**
     * <p>
     * Deletes a comment by its id.
     * </p>
     */
    private void deleteComment() {
        LOGGER.info(COMMENT_CONTROLLER.delete(getCommentId()) ? "We’ve deleted your comment successfully" : "Please enter a valid id");
    }

    /**
     * <p>
     * Gets all comment using their profile id.
     * </p>
     */
    private void getAllComment() {
        final Collection<Comment> comments = COMMENT_CONTROLLER.getAll(getPostId());

        LOGGER.info(Objects.isNull(comments) ? "Please enter valid profile id" : comments);
    }

    /**
     * <p>
     * Creates the like type.
     * </p>
     */
    private void createLike() {
        try {
            LOGGER.info("Choose 1 to create post like\nChoose 2 to create comment like\nChoose any other option to leave the page");
            final int input = Integer.parseInt(SCANNER.nextLine());

            switch (input) {
                case 1:
                    createPostLike();
                    break;
                case 2:
                    createCommentLike();
                    break;
                default:
                    userChoice();
            }
        } catch (NumberFormatException numberFormatException) {
            userChoice();
        }
    }

    /**
     * <p>
     * Creates the new post {@link Like}
     * </p>
     */
    private void createPostLike() {
        final Like like = new Like();

        try {
            like.setProfileId(getProfileId());
            like.setPostId(getPostId());
            LOGGER.info("Choose 1 to add like\nChoose 2 to remove like\nChoose any other option to leave this page");
            final int input = Integer.parseInt(SCANNER.nextLine());

            switch (input) {
                case 1:
                    like.setLike(true);
                    like.setReaction(getReaction());
                    final Long id = LIKE_CONTROLLER.create(like);

                    if (Objects.isNull(id)) {
                        LOGGER.info("Please enter valid details");
                    } else {
                        LOGGER.info("like added successfully\nYour post like id: " + id);
                    }
                    break;
                case 2:
                    like.setLike(false);
                    final Long postId = LIKE_CONTROLLER.create(like);

                    if (Objects.isNull(postId)) {
                        LOGGER.info("Please enter valid details");
                    } else {
                        LOGGER.info("like removed successfully\nYour post like id:" + postId);
                    }
                    break;
                default:
                    createLike();
            }
        } catch (NumberFormatException numberFormatException) {
            createLike();
        }
    }

    /**
     * <p>
     * Creates the new comment {@link Like}
     * </p>
     */
    private void createCommentLike() {
        final Like like = new Like();

        try {
            like.setProfileId(getProfileId());
            like.setPostId(getPostId());
            like.setCommentId(getCommentId());
            LOGGER.info("Choose 1 to add comment like\nChoose 2 to remove comment like\nChoose any other option to leave this page");
            final int input = Integer.parseInt(SCANNER.nextLine());

            switch (input) {
                case 1:
                    like.setLike(true);
                    like.setReaction(getReaction());
                    final Long id = LIKE_CONTROLLER.create(like);

                    if (Objects.isNull(id)) {
                        LOGGER.info("like already added to this comment");
                    } else {
                        LOGGER.info("like added successfully\nYour comment like id: %s%n");
                    }
                    break;
                case 2:
                    like.setLike(false);
                    final Long commentId = LIKE_CONTROLLER.create(like);

                    if (Objects.isNull(commentId)) {
                        LOGGER.info("like already added to this comment");
                    } else {
                        LOGGER.info("like removed successfully\nYour comment like id:" + commentId);
                    }
                    break;
            }
        } catch (NumberFormatException numberFormatException) {
            createLike();
        }
    }

    /**
     * <p>
     * Gets all like using their profile id.
     * </p>
     */
    private void getAllLike() {
        LOGGER.info("Choose 1 to get all post likes\nChoose 2 to get all comment likes\nTo leave choose any other number");
        final int input = Integer.parseInt(SCANNER.nextLine());

        switch (input) {
            case 1:
                final Collection<Like> postLikes = LIKE_CONTROLLER.getAll(getPostId());

                LOGGER.info(Objects.isNull(postLikes) ? "Please enter valid post id" : postLikes);
                break;
            case 2:
                final Collection<Like> commentLikes = LIKE_CONTROLLER.getAll(getCommentId(), getPostId());

                LOGGER.info(Objects.isNull(commentLikes) ? "Please enter valid post id" : commentLikes);
                break;
        }
    }

    /**
     * <p>
     * Gets the types of likes reaction from the user
     * </p>
     */
    private Reaction getReaction() {
        try {
            LOGGER.info("Available like types: LIKE, CELEBRATE, SUPPORT, LOVE, INSIGHTFUL, FUNNY");
            final String input = SCANNER.nextLine();

            return Reaction.valueOf(input.toUpperCase().trim());
        } catch (IllegalArgumentException exception) {
            LOGGER.info("Please enter available like types.");
        }

        return getReaction();
    }
}

