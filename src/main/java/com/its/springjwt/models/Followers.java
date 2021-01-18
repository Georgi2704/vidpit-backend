package com.its.springjwt.models;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Table(name = "followers")
public class Followers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotBlank
    @Valid
    private User followed;

    @ManyToOne
    @NotBlank
    @Valid
    private User followedBy;

    @NotBlank
    private boolean isFollow;

    @Temporal(TemporalType.DATE)
    private Date dateOfFollow;

    public Followers(){

    }

    public Followers(User followedBy, User followed, boolean isFollow){
        this.followedBy = followedBy;
        this.followed = followed;
        this.isFollow = isFollow;
        this.dateOfFollow = Date
                .from(LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getFollowed() {
        return followed;
    }

    public void setFollowed(User followed) {
        this.followed = followed;
    }

    public User getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(User followedBy) {
        this.followedBy = followedBy;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public Date getDateOfFollow() {
        return dateOfFollow;
    }

    public void setDateOfFollow(Date dateOfFollow) {
        this.dateOfFollow = dateOfFollow;
    }

    @Override
    public String toString() {
        return "Followers{" +
                "id=" + id +
                ", followed=" + followed +
                ", followedBy=" + followedBy +
                ", isFollow=" + isFollow +
                ", date_of_follow=" + dateOfFollow +
                '}';
    }
}
