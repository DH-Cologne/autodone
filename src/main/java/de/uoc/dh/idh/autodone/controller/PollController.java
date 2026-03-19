package de.uoc.dh.idh.autodone.controller;

import static de.uoc.dh.idh.autodone.utils.ObjectUtils.FORCE;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.mapFields;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.uoc.dh.idh.autodone.entities.PollEntity;
import de.uoc.dh.idh.autodone.entities.StatusEntity;
import de.uoc.dh.idh.autodone.services.PollService;
import de.uoc.dh.idh.autodone.services.StatusService;
import jakarta.servlet.http.HttpServletResponse;

@Controller()
@RequestMapping("/poll")
public class PollController {

	public static final int MAX_POLL_OPTIONS = 4;

	@Autowired()
	private PollService pollService;

	@Autowired()
	private StatusService statusService;

	//

	@DeleteMapping()
	public String delete(@RequestParam() Map<String, String> params) {
		var uuid = params.get("uuid");
		var poll = pollService.getOne(params.get("uuid"));
		poll.status.poll = null;
		pollService.delete(uuid);
		return "redirect:/status?uuid=" + poll.status.uuid;
	}

	//

	@GetMapping()
	public String get(Model model, @RequestParam() Map<String, String> params, HttpServletResponse resp) throws Exception {
		if (params.containsKey("uuid")) {
			var poll = pollService.getOne(params.get("uuid"));

			model.addAttribute("poll", poll);
			return "entity/poll";
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter 'uuid'");
			return null;
		}
	}

	//

	@PostMapping()
	public String post(@RequestParam() Map<String, Object> form, HttpServletResponse resp) throws Exception {
		var poll = new PollEntity();

		if (form.containsKey("uuid")) {
			poll = pollService.getOne((String) form.get("uuid"));
		} else {
			StatusEntity status = statusService.getOne((String) form.get("status.uuid"));
			
			if (status.poll != null || !status.media.isEmpty()) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot add a poll because status already has a poll or media");
				return null;
			}

			form.put("status", status);
		}

		var mappedPoll = mapFields(form, poll, FORCE);

		var pollOptions = new ArrayList<String>();

		for (var i = 0; i < MAX_POLL_OPTIONS; i++) {
			var option = (String) form.get("options[" + i + "]");
			if (option != null && !option.isBlank()) {
				pollOptions.add(option.trim());
			}
		}

		if (pollOptions.size() < 2) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "At least two poll options are required");
			return null;
		}

		if (pollOptions.stream().distinct().count() != pollOptions.size()) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Poll options must be unique");
			return null;
		}

		mappedPoll.options = pollOptions;

		var save = pollService.save(mappedPoll);
		return "redirect:/poll?uuid=" + save.uuid;
	}

}
