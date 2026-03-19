package de.uoc.dh.idh.autodone.services;

import static org.springframework.data.domain.Sort.by;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import de.uoc.dh.idh.autodone.base.BaseService;
import de.uoc.dh.idh.autodone.entities.PollEntity;
import de.uoc.dh.idh.autodone.repositories.PollRepository;
import jakarta.transaction.Transactional;

@Service()
@Transactional()
public class PollService extends BaseService<PollEntity> {

	@Autowired()
	private PollRepository pollRepository;

	//

	public PollEntity save(PollEntity poll) {
		return pollRepository.save(poll);
	}

	//

	@Override()
	public void delete(UUID uuid, String username, String domain) {
		pollRepository //
				.deleteByUuidAndStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(uuid, username, domain);
	}

	@Override()
	public PollEntity getOne(UUID uuid, String username, String domain) {
		return pollRepository //
				.findOneByUuidAndStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(uuid, username, domain);
	}

	@Override
	public Iterable<PollEntity> getOwn(String username, String domain) {
		return pollRepository //
				.findAllByStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(username, domain);
	}

	@Override()
	public Page<PollEntity> getPage(Pageable request, String username, String domain) {
		return pollRepository //
				.findAllByStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(request, username, domain);
	}

	//

	@Override()
	protected Sort getSort() {
		return by("status.group.name");
	}

}
