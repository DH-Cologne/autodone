package de.uoc.dh.idh.autodone.services;

import static de.uoc.dh.idh.autodone.config.MastodonConfig.MASTODON_API_STATUS;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.FORCE;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.copyFields;
import static de.uoc.dh.idh.autodone.utils.WebUtils.remoteHref;
import static de.uoc.dh.idh.autodone.utils.WebUtils.request;
import static org.springframework.data.domain.Sort.by;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import de.uoc.dh.idh.autodone.base.BaseEntity;
import de.uoc.dh.idh.autodone.base.BaseService;
import de.uoc.dh.idh.autodone.entities.GroupEntity;
import de.uoc.dh.idh.autodone.entities.StatusEntity;
import de.uoc.dh.idh.autodone.repositories.StatusRepository;
import jakarta.transaction.Transactional;

@Service()
@Transactional()
public class StatusService extends BaseService<StatusEntity> {

	@Autowired()
	private StatusRepository statusRepository;

	@Autowired()
	private TimerService timerService;

	//

	public Iterable<BaseEntity> getAll(Instant date) {
		return statusRepository.findAllByDateAfterAndGroupEnabledTrueAndIdIsNull(date);
	}

	public StatusEntity getAny(UUID uuid) {
		return statusRepository.findById(uuid).get();
	}

	public Page<StatusEntity> getPage(String page, String sort, GroupEntity group) {
		return getPage(pageRequest(page, sort), group.status);
	}

	//

	public StatusEntity publish(UUID uuid) {
		return publish(getAny(uuid));
	}

	public StatusEntity publish(StatusEntity status) {
		var data = new HashMap<String, Object>();
		data.put("status", status.status);

		if (status.media != null) {
			data.put("media_ids", status.media.stream().map((media) -> media.id).toList());
		}

		if (status.group.language != null) {
			data.put("language", status.group.language.getLocale());
		}

		if (status.group.threaded) {
			var prev = statusRepository //
					.findTopByGroupAndDateBeforeAndIdIsNotNullOrderByDateDesc(status.group, status.date);

			if (prev != null) {
				data.put("in_reply_to_id", prev.id);
			}
		}

		var href = remoteHref(status.group.token.server.domain, MASTODON_API_STATUS);
		var post = request(StatusEntity.class).auth(status.group.token).post(href, data);

		return save(copyFields(post, status, FORCE));
	}

	//

	public StatusEntity save(StatusEntity status) {
		status = statusRepository.save(status);
		timerService.unscheduleStatus(status);

		if (status.group.enabled) {
			timerService.scheduleStatus(status);
		}

		return status;
	}

	//

	@Override()
	public void delete(UUID uuid, String username, String domain) {
		statusRepository //
				.deleteByUuidAndGroupTokenUsernameAndGroupTokenServerDomain(uuid, username, domain);
	}

	@Override()
	public StatusEntity getOne(UUID uuid, String username, String domain) {
		return statusRepository //
				.findOneByUuidAndGroupTokenUsernameAndGroupTokenServerDomain(uuid, username, domain);
	}

	@Override
	public Iterable<StatusEntity> getOwn(String username, String domain) {
		return statusRepository //
				.findAllByGroupTokenUsernameAndGroupTokenServerDomain(username, domain);
	}

	@Override()
	public Page<StatusEntity> getPage(Pageable request, String username, String domain) {
		return statusRepository //
				.findAllByGroupTokenUsernameAndGroupTokenServerDomain(request, username, domain);
	}

	//

	@Override()
	protected Sort getSort() {
		return by("group.name", "date");
	}

}
