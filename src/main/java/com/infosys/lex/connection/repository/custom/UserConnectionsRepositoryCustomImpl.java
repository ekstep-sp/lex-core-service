package com.infosys.lex.connection.repository.custom;

import com.infosys.lex.connection.postgresdb.UserConnections;
import com.infosys.lex.connection.postgresdb.UserConnectionsView;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class UserConnectionsRepositoryCustomImpl implements UserConnectionsRepositoryCustom {

	private final JdbcTemplate jdbcTemplate;

	public UserConnectionsRepositoryCustomImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Page<UserConnectionsView> findAllByConnectedFromAndStatus(UUID connectedFrom, UserConnections.UserConnectionStatus status, Pageable pageable) {
		List<Map<String, Object>> dataRowSet = jdbcTemplate.queryForList("SELECT connection_id, connected_to, email FROM " +
				"public.user_connections AS con left join public.wingspan_user on (connected_to=wid) WHERE connected_from=? and status='"+ status.name()+"' LIMIT ? OFFSET ?"
		, connectedFrom, pageable.getPageSize(), pageable.getOffset());
		List<UserConnectionsView> data = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		dataRowSet.forEach(row-> {
			data.add(mapper.convertValue(row, UserConnectionsView.class));
		});
		SqlRowSet count = jdbcTemplate.queryForRowSet("SELECT count(*) as total FROM " +
				"public.user_connections AS con WHERE connected_from=? and status='"+ status.name()+"'", connectedFrom);
		count.first();
		int totalElements = count.getInt("total");
		return new PageImpl<>(data, pageable, totalElements);
	}
}
