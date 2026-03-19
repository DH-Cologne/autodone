package de.uoc.dh.idh.autodone.entities;

import static jakarta.persistence.GenerationType.UUID;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

import de.uoc.dh.idh.autodone.serde.PollOptionDeserializer;

@Data()
@Entity()
@JsonNaming(SnakeCaseStrategy.class)
public class PollEntity {

	@Id()
	@GeneratedValue(strategy = UUID)
	public UUID uuid;

	//

	@OneToOne(optional = false)
	public StatusEntity status;

	@JsonDeserialize(contentUsing = PollOptionDeserializer.class)
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "poll_options", joinColumns = @JoinColumn(name = "poll_id"))
	@Column(name = "option", nullable = false)
	public List<String> options;

	//

	@Column(nullable = false)
	public Integer expiresIn;

	@Column(nullable = false)
	public boolean multiple;

	@Column()
	public String id;

}
