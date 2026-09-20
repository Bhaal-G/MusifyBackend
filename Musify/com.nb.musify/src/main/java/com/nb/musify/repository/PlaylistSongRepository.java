package com.nb.musify.repository;

import com.nb.musify.entity.PlaylistSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong,Long> {
}
