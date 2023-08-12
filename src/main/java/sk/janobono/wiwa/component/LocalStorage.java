package sk.janobono.wiwa.component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Component
public class LocalStorage {

    protected Path storageLocation;

    @PostConstruct
    public void init() {
        try {
            this.storageLocation = Files.createTempDirectory("wiwa");
        } catch (final IOException e) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", e);
        }
    }

    @PreDestroy
    public void clean() {
        delete(storageLocation);
    }

    public String getFileName(final MultipartFile file) {
        final String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (fileName.contains("..")) {
            throw new RuntimeException("Filename [" + fileName + "] contains invalid path sequence.");
        }
        return fileName;
    }

    public String getFileType(final MultipartFile file) {
        return Optional.ofNullable(file.getContentType()).orElse("application/octet-stream");
    }

    public byte[] getFileData(final MultipartFile file) {
        try (
                final InputStream is = new BufferedInputStream(file.getInputStream());
                final ByteArrayOutputStream os = new ByteArrayOutputStream()
        ) {
            read(is, os);
            return os.toByteArray();
        } catch (final Exception e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public Resource getDataResource(final byte[] data) {
        return new ByteArrayResource(data);
    }

    public byte[] getFileData(final Path file) {
        try (
                final InputStream is = new BufferedInputStream(new FileInputStream(file.toFile()));
                final ByteArrayOutputStream os = new ByteArrayOutputStream()
        ) {
            read(is, os);
            return os.toByteArray();
        } catch (final Exception e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public void setFileData(final Path file, final byte[] data) {
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file.toFile(), false));
            os.write(data, 0, data.length);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(os);
        }
    }

    public Path createDirectory(final String... more) {
        final Path result = Paths.get(
                this.storageLocation.toFile().getAbsolutePath(),
                more
        ).toAbsolutePath().normalize();
        createDirectory(result);
        return result;
    }

    public Path createTempDirectory(final String prefix) {
        try {
            return Files.createTempDirectory(storageLocation, prefix);
        } catch (final IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public Path createTempFile(final String prefix, final String suffix) {
        try {
            return Files.createTempFile(storageLocation, prefix, suffix);
        } catch (final IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public Path createTempFile(final Path dir, final String prefix, final String suffix) {
        try {
            return Files.createTempFile(dir, prefix, suffix);
        } catch (final IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public void saveStream(final InputStream source, final Path target) {
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public void close(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void delete(final Path path) {
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (final IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public void zip(final Path zip, final Path contentDir) {
        try (final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip.toFile()))) {
            zipAddDir(zos, contentDir.toFile(), "");
        } catch (final IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public void zip(final Path zip, final Path[] content) {
        final byte[] buffer = new byte[1024];
        try (final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip.toFile()))) {
            for (final Path element : content) {
                final ZipEntry ze = new ZipEntry(element.toFile().getName());
                zos.putNextEntry(ze);
                try (final FileInputStream in = new FileInputStream(element.toFile())) {
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                }
            }
            zos.closeEntry();
        } catch (final IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public void unzip(final Path zip, final Path targetDir) {
        try (
                final ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zip.toFile()))
        ) {
            Files.createDirectories(targetDir);
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                final Path filePath = Path.of(targetDir.toFile().getAbsolutePath(), entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    extractFile(zipIn, filePath);
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        } catch (final IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public void copy(final Path source, final Path target) {
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public void write(final Path path, final byte[] data) {
        try (final OutputStream os = new BufferedOutputStream(new FileOutputStream(path.toFile(), false))) {
            os.write(data, 0, data.length);
        } catch (final IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public int countLines(final Path file) {
        int result = 0;
        try (final BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
            while (Objects.nonNull(br.readLine())) {
                result++;
            }
        } catch (final IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
        return result;
    }

    public int countFiles(final Path targetDir) {
        return Objects.requireNonNull(new File(targetDir.toUri()).list()).length;
    }

    protected void read(final InputStream is, final ByteArrayOutputStream os) throws IOException {
        final byte[] buffer = new byte[1024];
        int bytesRead = 0;
        while (bytesRead != -1) {
            bytesRead = is.read(buffer);
            if (bytesRead > 0) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    private void createDirectory(final Path path) {
        try {
            Files.createDirectories(path);
        } catch (final IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    private void zipAddDir(final ZipOutputStream zos, final File dir, final String prefix) throws IOException {
        final byte[] buffer = new byte[1024];
        for (final File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                zos.putNextEntry(new ZipEntry(prefix + file.getName() + "/"));
                zipAddDir(zos, file, prefix + file.getName() + "/");
            } else {
                zos.putNextEntry(new ZipEntry(prefix + file.getName()));
                try (final FileInputStream in = new FileInputStream(file)) {
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                }
                zos.closeEntry();
            }
        }
    }

    private void extractFile(final ZipInputStream zipIn, final Path filePath) throws IOException {
        try (final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath.toFile()))) {
            final byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }

    public boolean isImageFile(final String fileType) {
        return fileType.equals(MediaType.IMAGE_GIF_VALUE)
                || fileType.equals(MediaType.IMAGE_JPEG_VALUE)
                || fileType.equals(MediaType.IMAGE_PNG_VALUE);
    }
}
