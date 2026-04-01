package de.uoc.dh.idh.autodone.entities;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.UUID;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.Data;

@Data()
@Entity()
@JsonNaming(SnakeCaseStrategy.class)
public class StatusEntity {

	@Id()
	@GeneratedValue(strategy = UUID)
	public UUID uuid;

	//

	@ManyToOne(optional = false)
	public GroupEntity group;

	@OneToMany(cascade = ALL, fetch = LAZY, mappedBy = "status")
	public List<MediaEntity> media;

	//

	@Column(nullable = false)
	public Instant date;

	@Column()
	public String id;

	@Column(nullable = false, length = 500)
	public String status;

	@Column(length = 500)
	public String contentWarning;

	//

	@Transient()
	public List<Exception> exceptions;

}
