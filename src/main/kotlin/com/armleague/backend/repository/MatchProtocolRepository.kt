package com.armleague.backend.repository

import com.armleague.backend.model.MatchProtocol
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MatchProtocolRepository : JpaRepository<MatchProtocol, Int>