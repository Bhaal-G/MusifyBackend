package com.nb.musify.service;

import com.nb.musify.dto.request.PlaylistRequest;
import com.nb.musify.dto.response.PlaylistResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PlaylistService {
    PlaylistResponse createPlaylist(PlaylistRequest request, MultipartFile imageFile, String email);
}
