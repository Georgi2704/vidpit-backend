package com.its.springjwt.models;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(	name = "videos")
public class Video {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@NotBlank
	@Valid
	private User uploaded_by;

	@NotBlank
	@Size(max = 30)
	private String title;

	@Size(max = 600)
	private String description;

	@NotBlank
	private long views;

	@NotBlank
	private double rating;

	@NotBlank
	private long times_rated;

	@NotBlank
	private String uploaded_at;

	@NotBlank
	private String videoContent;

	private String thumbnail;

	@ManyToOne
	@JoinTable(	name = "video_categories",
			joinColumns = @JoinColumn(name = "video_id"),
			inverseJoinColumns = @JoinColumn(name = "category_id"))
	private Category category = new Category();

	public Video() {
	}

	public Video(String title,String description,Category category) {
		this.title = title;
		this.description = description;
		this.category = category;
	}

	public Video(User uploaded_by,String title,String description,String video_content,Category category)
	{
		this.uploaded_by = uploaded_by;
		this.title = title;
		this.description = description;
		this.videoContent = video_content;
		this.category = category;
		this.uploaded_at = LocalDateTime.now().toString();
		this.views = 0;
		this.rating = 0;
		this.times_rated = 0;
		this.thumbnail = "";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUploaded_by() {
		return uploaded_by;
	}

	public void setUploaded_by(User uploaded_by) {
		this.uploaded_by = uploaded_by;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getViews() {
		return views;
	}

	public void setViews(long views) {
		this.views = views;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public long getTimes_rated() {
		return times_rated;
	}

	public void setTimes_rated(long times_rated) {
		this.times_rated = times_rated;
	}

	public String getUploaded_at() {
		return uploaded_at;
	}

	public void setUploaded_at(String uploaded_at) {
		this.uploaded_at = uploaded_at;
	}

	public String getVideoContent() {
		return videoContent;
	}

	public void setVideoContent(String video_content) {
		this.videoContent = video_content;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	@Override
	public String toString() {
		return "Video{" +
				"id=" + id +
				", uploaded_by=" + uploaded_by +
				", title='" + title + '\'' +
				", description='" + description + '\'' +
				", views=" + views +
				", rating=" + rating +
				", times_rated=" + times_rated +
				", uploaded_at='" + uploaded_at + '\'' +
				", video_content='" + videoContent + '\'' +
				", category=" + category +
				'}';
	}
}
