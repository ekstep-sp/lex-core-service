package com.infosys.lex.areaofwork.bodhi.repo;

import com.infosys.lex.areaofwork.entities.AreaOfWork;
import com.infosys.lex.areaofwork.entities.AreaOfWorkKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaOfWorkCassandraRepo extends CassandraRepository<AreaOfWork, AreaOfWorkKey> {
}
