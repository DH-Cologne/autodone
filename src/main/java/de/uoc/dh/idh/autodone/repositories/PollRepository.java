package de.uoc.dh.idh.autodone.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import de.uoc.dh.idh.autodone.base.BaseRepository;
import de.uoc.dh.idh.autodone.entities.PollEntity;
import jakarta.transaction.Transactional;

@Repository()
@Transactional()
public interface PollRepository extends BaseRepository<PollEntity> {

	void deleteByUuidAndStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(

			UUID uuid,

			String username,

			String domain

	);

	//

	Iterable<PollEntity> findAllByStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(

			String username,

			String domain

	);

	//

	Page<PollEntity> findAllByStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(

			Pageable pageable,

			String username,

			String domain

	);

	//

	PollEntity findOneByUuidAndStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(

			UUID uuid,

			String username,

			String domain

	);

}
