package de.uoc.dh.idh.autodone.controller;

import static de.uoc.dh.idh.autodone.utils.ObjectUtils.FORCE;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.mapFields;
import static de.uoc.dh.idh.autodone.utils.WebUtils.href;
import static java.util.Map.of;

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

import de.uoc.dh.idh.autodone.entities.MediaEntity;
import de.uoc.dh.idh.autodone.entities.PollEntity;
import de.uoc.dh.idh.autodone.entities.StatusEntity;
import de.uoc.dh.idh.autodone.services.GroupService;
import de.uoc.dh.idh.autodone.services.MediaService;
import de.uoc.dh.idh.autodone.services.StatusService;

@Controller()
@RequestMapping("/status")
public class StatusController {

	@Autowired()
	private GroupService groupService;

	@Autowired()
	private MediaService mediaService;

	@Autowired()
	private StatusService statusService;

	//

	@DeleteMapping()
	public String delete(@RequestParam() Map<String, String> params) {
		statusService.delete(params.get("uuid"));
		return "redirect:" + href();
	}

	//

	@GetMapping()
	public String get(Model model, @RequestParam() Map<String, String> params) {
		if (params.containsKey("uuid")) {
			MediaEntity mediaEntity = new MediaEntity();
			PollEntity pollEntity = new PollEntity();
			pollEntity.options = new ArrayList<>();

			var status = statusService.getOne(params.get("uuid"));
			var media = mapFields(of("status", status), mediaEntity);
			var poll = mapFields(of("status", status), pollEntity);
			var page = mediaService.getPage(params.get("page"), params.get("sort"), status);
			var statusPoll = status.poll;

			model.addAttribute("status", status);
			model.addAttribute("media", media);
			model.addAttribute("poll", poll);
			model.addAttribute("page", page);
			model.addAttribute("statusPoll", statusPoll);
			return "entity/status";
		} else {
			var status = new StatusEntity();
			var groups = groupService.getOwn();
			var page = statusService.getPage(params.get("page"), params.get("sort"));

			model.addAttribute("status", status);
			model.addAttribute("groups", groups);
			model.addAttribute("page", page);
			return "entities/status";
		}
	}

	//

	@PostMapping()
	public String post(@RequestParam() Map<String, Object> form) {
		var status = new StatusEntity();

		if (form.containsKey("uuid")) {
			status = statusService.getOne((String) form.get("uuid"));
		} else {
			form.put("group", groupService.getOne((String) form.get("group.uuid")));
		}

		var save = statusService.save(mapFields(form, status, FORCE));
		return "redirect:/status?uuid=" + save.uuid;
	}

}
