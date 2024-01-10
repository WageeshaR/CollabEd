package com.collabed.core.batch.data;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("intel_text_content")
@Data
public class IntelTextContent {
    @PrimaryKeyColumn(
            ordinal = 2,
            type = PrimaryKeyType.PARTITIONED,
            ordering = Ordering.DESCENDING)
    @CassandraType(type = CassandraType.Name.UUID)
    private UUID id;
    private ContentType contentType;
    private String content;
    // which parent datasource (collection) the content is referred to
    private String parentRefType;
    private String parentRefId;
}
