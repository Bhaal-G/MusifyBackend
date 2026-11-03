package com.nb.musify.service;

import com.nb.musify.dto.request.SongRequest;
import com.nb.musify.dto.response.MessageResponse;
import com.nb.musify.dto.response.PaginatedResponse;
import com.nb.musify.dto.response.SongResponse;
import org.springframework.web.multipart.MultipartFile;

public interface SongService {
    SongResponse addSong(SongRequest request, MultipartFile songFile, MultipartFile imageFile, String email);

    Object getAllSongs(Long userId, int page, int size, String search);

    SongResponse getSongById(Long id);

    SongResponse updateSong(Long id, SongRequest songRequest, MultipartFile songFile, MultipartFile imageFile, String email);

    MessageResponse deleteSong(Long id, String email);
}
