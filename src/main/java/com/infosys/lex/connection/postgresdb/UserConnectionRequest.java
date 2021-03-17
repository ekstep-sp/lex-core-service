package com.infosys.lex.connection.postgresdb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "user_connection_requests",
		schema = "public",
		uniqueConstraints = @UniqueConstraint(columnNames = {"requested_by", "requested_to"})
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@TypeDefs({
		@TypeDef(name = "pg-enum", typeClass = PgSqlEnumType.class),
})
public class UserConnectionRequest {

	@Id
	private UUID requestId;

	@Column(name = "requested_by")
	private UUID requestedBy;

	@Column(name = "requested_to")
	private UUID requestedTo;

	@Type(type = "pg-enum")
	@Enumerated(EnumType.STRING)
	@Column(name = "status", columnDefinition = "user_connection_request_status")
	private UserConnectionRequestStatus status;

	private short requestCount;

	private String comment;

	@CreationTimestamp
	private Timestamp createdOn;

	@UpdateTimestamp
	private Timestamp updatedOn;

	public void incrementRequestCount() {
		++requestCount;
	}

	public enum UserConnectionRequestStatus {
		Pending,
		Accepted,
		Rejected,
		Deleted
	}
}
