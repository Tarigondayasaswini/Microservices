package com.revplay.catalogservice.integration.playlist;

public class PlaylistResponse {
    private Long id;
    private String name;
    private Long userId;
    private String ownerUsername;
    private String description;
    private Boolean isPublic;

    public PlaylistResponse() {}

    public PlaylistResponse(Long id, String name, Long userId, String ownerUsername, String description, Boolean isPublic) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.ownerUsername = ownerUsername;
        this.description = description;
        this.isPublic = isPublic;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
}
