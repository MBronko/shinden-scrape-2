package com.bronko.backend.repository;

import com.bronko.backend.model.PlayerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerInfoRepository extends JpaRepository<PlayerInfo, Integer> {
}
