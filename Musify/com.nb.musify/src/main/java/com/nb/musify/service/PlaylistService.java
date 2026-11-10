package com.nb.musify.service;

import com.nb.musify.dto.request.PlaylistRequest;
import com.nb.musify.dto.response.MessageResponse;
import com.nb.musify.dto.response.PlaylistResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PlaylistService {
    PlaylistResponse createPlaylist(PlaylistRequest request, MultipartFile imageFile, String email);

    PlaylistResponse updatePlaylistPrivacy(Long id, Boolean isPublic, String email);

    MessageResponse addSongToPlaylist(Long playlistId, Long songId, String email);

    MessageResponse removeSongFromPlaylist(Long playlistId, Long songId, String email);

    MessageResponse reorderSongInPlaylist(Long playlistId, Long songId, Integer newPosition, String email);
}
