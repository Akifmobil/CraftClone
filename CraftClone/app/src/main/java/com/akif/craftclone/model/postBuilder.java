package com.akif.craftclone.model;

public class postBuilder {
    private String email;
    private String comment;
    private String downloadUrl;

    public postBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public postBuilder setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public postBuilder setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }

    public post createPost() {
        return new post(email, comment, downloadUrl);
    }
}