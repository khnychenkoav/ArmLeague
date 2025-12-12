package com.armleague.backend.repository

import com.armleague.backend.model.Referee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefereeRepository : JpaRepository<Referee, Int>