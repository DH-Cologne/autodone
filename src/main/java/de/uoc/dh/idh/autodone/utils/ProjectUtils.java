package de.uoc.dh.idh.autodone.utils;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;

@Component()
public class ProjectUtils {

	public final Model projectModel;

	//

	private ProjectUtils(BuildProperties buildProperties, EntityManager entityManager) throws Exception {
		var file = "META-INF/maven/" + buildProperties.getGroup() + "/" + buildProperties.getArtifact() + "/pom.xml";
		projectModel = new MavenXpp3Reader().read(getClass().getClassLoader().getResourceAsStream(file));
	}

}
