package sk.janobono.wiwa.business.impl.util;

import org.springframework.stereotype.Service;
import sk.janobono.wiwa.dal.domain.OrderViewDo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class OrderCsvUtilService {

    public Path generateCsv(final OrderViewDo orderViewDo) {
        try {
            final Path path = Files.createTempFile("wiwa", ".csv");
            try (final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(path.toFile(), true)))) {
                writer.println("WIWA CSV");
                writer.flush();
            }
            return path;
        } catch (final IOException e) {
            throw new RuntimeException("Line write error.", e);
        }
    }
}
