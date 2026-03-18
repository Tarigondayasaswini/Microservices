package com.revplay.playlistservice.mapper;

import com.revplay.playlistservice.dto.request.LikeRequest;
import com.revplay.playlistservice.dto.response.UserLikeResponse;
import com.revplay.playlistservice.entity.UserLike;
import org.springframework.stereotype.Component;


@Component
public class UserLikeMapper {


    public UserLike toEntity(LikeRequest request, Long userId) {
        return UserLike.builder()
                .userId(userId)
                .likeableId(request.getLikeableId())
                .likeableType(request.getLikeableType().toUpperCase())
                .build();
    }


    public UserLikeResponse toResponse(UserLike like) {
        return UserLikeResponse.builder()
                .id(like.getId())
                .userId(like.getUserId())
                .likeableId(like.getLikeableId())
                .likeableType(like.getLikeableType())
                .createdAt(like.getCreatedAt())
                .build();
    }
}
