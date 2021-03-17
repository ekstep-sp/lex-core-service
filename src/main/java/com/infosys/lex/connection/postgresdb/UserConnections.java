package com.infosys.lex.connection.postgresdb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "user_connections",
		schema = "public",
		uniqueConstraints = @UniqueConstraint(columnNames = {"connected_from", "connected_to"})
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@TypeDefs({
		@TypeDef(name = "pg-enum", typeClass = PgSqlEnumType.class),
})
public class UserConnections {

	@Id
	private UUID connectionId;

	@Column(name = "connected_from")
	private UUID connectedFrom;

	@Column(name = "connected_to")
	private UUID connectedTo;

	@Type(type = "pg-enum")
	@Enumerated(EnumType.STRING)
	@Column(name = "status", columnDefinition = "user_connection_status")
	private UserConnectionStatus status;

	@CreationTimestamp
	private Timestamp createdOn;

	@UpdateTimestamp
	private Timestamp updatedOn;

	public enum UserConnectionStatus {
		Active,
		Inactive,
		Deleted
	}
}
