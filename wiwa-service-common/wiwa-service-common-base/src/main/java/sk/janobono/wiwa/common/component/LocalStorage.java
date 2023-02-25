package sk.janobono.wiwa.common.component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
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
        } catch (IOException e) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", e);
        }
    }

    @PreDestroy
    public void clean() {
        delete(storageLocation);
    }

    public String getFileName(MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (fileName.contains("..")) {
            throw new RuntimeException("Filename [" + fileName + "] contains invalid path sequence.");
        }
        return fileName;
    }

    public String getFileType(MultipartFile file) {
        String result = file.getContentType();
        if (!StringUtils.hasLength(result)) {
            result = "application/octet-stream";
        }
        return result;
    }

    public byte[] getFileData(MultipartFile file) {
        try (
                InputStream is = new BufferedInputStream(file.getInputStream());
                ByteArrayOutputStream os = new ByteArrayOutputStream()
        ) {
            read(is, os);
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public Resource getDataResource(byte[] data) {
        return new ByteArrayResource(data);
    }

    public byte[] getFileData(Path file) {
        try (
                InputStream is = new BufferedInputStream(new FileInputStream(file.toFile()));
                ByteArrayOutputStream os = new ByteArrayOutputStream()
        ) {
            read(is, os);
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public void setFileData(Path file, byte[] data) {
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file.toFile(), false));
            os.write(data, 0, data.length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(os);
        }
    }

    public Path createDirectory(String... more) {
        Path result = Paths.get(
                this.storageLocation.toFile().getAbsolutePath(),
                more
        ).toAbsolutePath().normalize();
        createDirectory(result);
        return result;
    }

    public Path createTempDirectory(String prefix) {
        try {
            return Files.createTempDirectory(storageLocation, prefix);
        } catch (IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public Path createTempFile(String prefix, String suffix) {
        try {
            return Files.createTempFile(storageLocation, prefix, suffix);
        } catch (IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public Path createTempFile(Path dir, String prefix, String suffix) {
        try {
            return Files.createTempFile(dir, prefix, suffix);
        } catch (IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public void saveStream(InputStream source, Path target) {
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void delete(Path path) {
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public void zip(Path zip, Path contentDir) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip.toFile()))) {
            zipAddDir(zos, contentDir.toFile(), "");
        } catch (IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public void zip(Path zip, Path[] content) {
        byte[] buffer = new byte[1024];
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip.toFile()))) {
            for (Path element : content) {
                ZipEntry ze = new ZipEntry(element.toFile().getName());
                zos.putNextEntry(ze);
                try (FileInputStream in = new FileInputStream(element.toFile())) {
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                }
            }
            zos.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public void unzip(Path zip, Path targetDir) {
        try (
                ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zip.toFile()))
        ) {
            Files.createDirectories(targetDir);
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                Path filePath = Path.of(targetDir.toFile().getAbsolutePath(), entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    extractFile(zipIn, filePath);
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public void copy(Path source, Path target) {
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public void write(Path path, byte[] data) {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(path.toFile(), false))) {
            os.write(data, 0, data.length);
        } catch (IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public int countLines(Path file) {
        int result = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
            while (Objects.nonNull(br.readLine())) {
                result++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
        return result;
    }

    public int countFiles(Path targetDir) {
        return Objects.requireNonNull(new File(targetDir.toUri()).list()).length;
    }

    protected void read(InputStream is, ByteArrayOutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        while (bytesRead != -1) {
            bytesRead = is.read(buffer);
            if (bytesRead > 0) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    private void createDirectory(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    private void zipAddDir(ZipOutputStream zos, File dir, String prefix) throws IOException {
        byte[] buffer = new byte[1024];
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                zos.putNextEntry(new ZipEntry(prefix + file.getName() + "/"));
                zipAddDir(zos, file, prefix + file.getName() + "/");
            } else {
                zos.putNextEntry(new ZipEntry(prefix + file.getName()));
                try (FileInputStream in = new FileInputStream(file)) {
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                }
                zos.closeEntry();
            }
        }
    }

    private void extractFile(ZipInputStream zipIn, Path filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath.toFile()))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }
}
