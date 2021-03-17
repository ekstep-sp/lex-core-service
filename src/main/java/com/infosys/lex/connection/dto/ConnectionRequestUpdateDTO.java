package com.infosys.lex.connection.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ConnectionRequestUpdateDTO {
	@NotBlank
	private String rootOrg;

	@NotNull
	private UUID requestId;

	@NotNull
	private UUID actorId;

	@NotNull
	private Action Action;

	public enum Action {
		Accept, Reject, Withdraw
	}
}
