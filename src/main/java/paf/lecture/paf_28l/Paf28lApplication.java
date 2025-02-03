package paf.lecture.paf_28l;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import paf.lecture.paf_28l.repository.GameRepository;
import paf.lecture.paf_28l.repository.SeriesRepository;

@SpringBootApplication
public class Paf28lApplication implements CommandLineRunner {
	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private SeriesRepository seriesRepository;

	public static void main(String[] args) {
		SpringApplication.run(Paf28lApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// gameRepository.findGamesByName("carcassonne")
		// gameRepository.groupCommentsByUser2()
		// 	.forEach(d -> System.out.println(d + "\n"));

		// seriesRepository.listSeriesByGenres()
		// 	.forEach(d -> System.out.println(d + "\n"));
	}

}
