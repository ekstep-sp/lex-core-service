package com.infosys.lex.areaofexpertise.bodhi.repo;

import com.infosys.lex.areaofexpertise.entities.AreaOfExpertise;
import com.infosys.lex.areaofexpertise.entities.AreaOfExpertiseKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaOfExpertiseCassandraRepo extends CassandraRepository<AreaOfExpertise, AreaOfExpertiseKey> {
}
