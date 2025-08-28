package de.uoc.dh.idh.autodone.entities;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.UUID;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data()
@Entity()
@JsonNaming(SnakeCaseStrategy.class)
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "domain" }) })
public class ServerEntity {

	@Id()
	@GeneratedValue(strategy = UUID)
	public UUID uuid;

	//

	@OneToMany(cascade = ALL, fetch = LAZY, mappedBy = "server")
	public List<TokenEntity> tokens;

	//

	@Column(nullable = false)
	public String clientId;

	@Column(nullable = false)
	public String clientSecret;

	@Column(nullable = false)
	public String domain;

	//

	@Transient()
	public String description;

	@Transient()
	public List<Map<String, Object>> rules;

	@Transient()
	public Map<String, Object> thumbnail;

	@Transient()
	public String title;

	@Transient()
	public String version;

}
