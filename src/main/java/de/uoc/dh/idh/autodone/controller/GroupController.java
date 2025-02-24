package de.uoc.dh.idh.autodone.controller;

import static de.uoc.dh.idh.autodone.utils.ObjectUtils.FORCE;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.mapFields;
import static de.uoc.dh.idh.autodone.utils.WebUtils.href;
import static java.util.Map.of;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.uoc.dh.idh.autodone.entities.GroupEntity;
import de.uoc.dh.idh.autodone.entities.StatusEntity;
import de.uoc.dh.idh.autodone.services.GroupService;
import de.uoc.dh.idh.autodone.services.StatusService;
import de.uoc.dh.idh.autodone.utils.SupportedLocales;

@Controller()
@RequestMapping("/group")
public class GroupController {

	@Autowired()
	private GroupService groupService;

	@Autowired()
	private StatusService statusService;

	//

	@DeleteMapping()
	public String delete(@RequestParam() Map<String, String> params) {
		groupService.delete(params.get("uuid"));
		return "redirect:" + href();
	}

	//

	@GetMapping()
	public String get(Model model, @RequestParam() Map<String, String> params) {
		Map<String, String> languages = SupportedLocales.getLanguages();
		model.addAttribute("languages", languages);
	
		if (params.containsKey("uuid")) {
			var group = groupService.getOne(params.get("uuid"));
			var status = mapFields(of("group", group), new StatusEntity());
			var page = statusService.getPage(params.get("page"), params.get("sort"), group);

			model.addAttribute("group", group);
			model.addAttribute("status", status);
			model.addAttribute("page", page);
			return "entity/group";
		} else {
			var group = new GroupEntity();
			var page = groupService.getPage(params.get("page"), params.get("sort"));

			model.addAttribute("group", group);
			model.addAttribute("page", page);
			return "entities/group";
		}
	}

	//

	@PostMapping()
	public String post(@RequestParam() Map<String, String> form) {
		var group = new GroupEntity();

		if (form.containsKey("uuid")) {
			group = groupService.getOne((String) form.get("uuid"));
		}

		var save = groupService.save(mapFields(form, group, FORCE));
		return "redirect:/group?uuid=" + save.uuid;
	}

}
