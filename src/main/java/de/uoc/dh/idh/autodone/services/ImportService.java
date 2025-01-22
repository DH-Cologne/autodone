package de.uoc.dh.idh.autodone.services;

import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMPORT_SKIP;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMPORT_SNIP;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.mapFields;
import static java.time.Instant.now;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneOffset.UTC;
import static java.util.Map.of;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.uoc.dh.idh.autodone.entities.GroupEntity;
import de.uoc.dh.idh.autodone.entities.MediaEntity;
import de.uoc.dh.idh.autodone.entities.StatusEntity;
import de.uoc.dh.idh.autodone.utils.DateTimeUtils;
import jakarta.transaction.Transactional;

@Service()
@Transactional()
public class ImportService {

	@Autowired()
	private DateTimeUtils dateTimeUtils;

	//

	public GroupEntity importGroup(InputStream inputStream) throws Exception {
		var group = new GroupEntity();
		var index = new AtomicInteger(1);
		var line = (String) null;

		group.exceptions = new ArrayList<Exception>();
		group.status = new ArrayList<StatusEntity>();

		try (var reader = new BufferedReader(new InputStreamReader(inputStream))) {
			while ((line = reader.readLine()) != null) {
				var number = index.getAndIncrement();

				if (!line.isEmpty() && !line.matches(AUTODONE_IMPORT_SKIP)) {
					try {
						group.status.add(mapFields(of("group", group), importStatus(line, number)));
					} catch (Exception exception) {
						group.exceptions.add(new ParseException(exception.getMessage(), number));
					}
				}
			}
		} catch (Exception exception) {
			throw exception;
		}

		return group;
	}

	//

	public StatusEntity importStatus(String line, int number) throws Exception {
		var columns = line.trim().split(AUTODONE_IMPORT_SNIP);
		var status = new StatusEntity();

		status.exceptions = new ArrayList<Exception>();
		status.media = new ArrayList<MediaEntity>();

		if (columns.length < 3) {
			throw new Exception("Please enter at least date, time and content in the first three columns");
		} else {
			var date = dateTimeUtils.parseDate(columns[0]);
			var time = dateTimeUtils.parseTime(columns[1]);

			if (date == null) {
				status.exceptions.add(new ParseException("Date not parseable (1st column)", number));
			}

			if (time == null) {
				status.exceptions.add(new ParseException("Time not parseable (2nd column)", number));
			}

			if (date != null && time != null) {
				status.date = dateTimeUtils.parse(date, time);

				while (status.date.isBefore(now())) {
					status.date = ofInstant(status.date, UTC).plusYears(1).toInstant(UTC);
				}
			} else {
				throw new Exception("Please enter a correct date and time in the first two columns");
			}

			if (columns[2].isEmpty()) {
				status.exceptions.add(new ParseException("No content found (3rd column)", number));
			} else if (columns[2].length() > 500) {
				status.exceptions.add(new ParseException("Content trimmed (3rd column)", number));
				status.status = columns[2].substring(0, 495) + "[...]";
			} else {
				status.status = columns[2];
			}

			if (columns.length > 3) {
				try {
					MediaEntity media = new MediaEntity();
					media.url = columns[3];

					if (columns.length > 4) {
						if (columns[4].length() > 1500) {
							status.exceptions.add(new ParseException("Image caption too long (5th column)", number));
						} else {
							media.description = columns[4];
						}
					}

					status.media.add(mapFields(of("status", status), media));

				} catch (Exception exception) {
					status.exceptions.add(new ParseException("Image not usable (4th column)", number));
				}
			}
		}

		return status;
	}

	//
}
