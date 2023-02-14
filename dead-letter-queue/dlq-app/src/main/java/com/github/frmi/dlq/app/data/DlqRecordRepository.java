package com.github.frmi.dlq.app.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DlqRecordRepository extends JpaRepository<DlqRecord, Long> {

}
