package com.collabed.core.batch.data;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IntelTextContentRepository extends CassandraRepository<IntelTextContent, UUID> {}
