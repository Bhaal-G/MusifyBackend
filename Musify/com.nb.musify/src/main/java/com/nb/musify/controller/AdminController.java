package com.nb.musify.controller;


import com.nb.musify.dto.request.SongRequest;
import com.nb.musify.dto.response.PaginatedResponse;
import com.nb.musify.dto.response.SongResponse;
import com.nb.musify.service.SongService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {
    @Autowired
    private SongService songService;

    @PostMapping("/addSong")
    public ResponseEntity<SongResponse> addSong(
            @RequestParam("title")
            @NotBlank(message = "Title is required")
            @Size(max = 100, message = "Title must not exceed 100 characters")
            String title,

            @RequestParam("artist")
            @NotBlank(message = "Artist is required")
            @Size(max = 100, message = "Artist must not exceed 100 characters")
            String artist,

            @RequestParam("songFile")
            MultipartFile songFile,

            @RequestParam(value = "imageFile")
            MultipartFile imageFile,
            Authentication authentication) {
            String email = authentication.getName();
            SongRequest request = new SongRequest(title, artist);
            SongResponse response =
                songService.addSong(request, songFile, imageFile, email);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/getAllSongs")
    public ResponseEntity<?> getAllSongs(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(songService.getAllSongs(userId, page, size, search));
    }
}
