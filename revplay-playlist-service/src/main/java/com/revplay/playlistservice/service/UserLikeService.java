package com.revplay.playlistservice.service;

import com.revplay.playlistservice.common.response.PagedResponseDto;
import com.revplay.playlistservice.dto.request.LikeRequest;
import com.revplay.playlistservice.dto.response.UserLikeResponse;

public interface UserLikeService {

    UserLikeResponse likeContent(LikeRequest request);

    void unlikeContent(Long likeId);

    PagedResponseDto<UserLikeResponse> getUserLikes(Long userId, String likeableType, int page, int size);
}


