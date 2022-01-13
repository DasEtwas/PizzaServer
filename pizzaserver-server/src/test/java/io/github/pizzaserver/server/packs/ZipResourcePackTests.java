package io.github.pizzaserver.server.packs;

import io.github.pizzaserver.api.packs.ResourcePack;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZipResourcePackTests {

    @Test
    public void shouldRetrieveManifestInformation(@TempDir Path tempDirPath) {
        ResourcePack pack = getResourcePack(tempDirPath);
        Assertions.assertEquals(pack.getUuid(), UUID.fromString("00000000-0000-0000-0000-000000000000"));
        Assertions.assertEquals(pack.getVersion(), "1.0.0");
    }

    @Test
    public void shouldBeEqualToTheResourcePackFile(@TempDir Path tempDirPath) {
        File resourcePackFile = getResourcePackFile(tempDirPath);
        ResourcePack pack = getResourcePack(tempDirPath);

        try (InputStream testFileStream = new FileInputStream(resourcePackFile)) {
            for (int chunkIndex = 0; chunkIndex < pack.getChunkCount(); chunkIndex++) {
                byte[] chunk = pack.getChunk(chunkIndex);
                for (byte chunkByte : chunk) {
                    int testFileByte = (byte) testFileStream.read();
                    assertEquals(testFileByte, chunkByte);
                    if (testFileByte == -1) {
                        return;
                    }
                }
            }

        } catch (IOException exception) {
            throw new AssertionError(exception);
        }

    }

    @Test
    public void chunkCountLimited(@TempDir Path tempDirPath) {
        File resourcePackFile = getResourceLargePackFile(tempDirPath);
        ResourcePack pack = getResourcePack(tempDirPath);

        System.out.println(pack.getDataLength());
        System.out.println(pack.getChunkCount());
        System.out.println(pack.getMaxChunkLength());

        int fileSize = 1 << (10 + 10); // 67MiB

        // BDS: 102400
        final long minChunkLength = 102400;

        // more chunks will cause a clientside error with disconnect
        final long maxChunks = 99;

        int lel =  (int) Math.max((fileSize + maxChunks) / maxChunks, minChunkLength);


        System.out.println(lel);
        System.out.println((fileSize + lel - 1) / lel);

        assert pack.getChunkCount() < 100;
        assert pack.getMaxChunkLength() > 0;
    }

    private static ResourcePack getResourcePack(Path tempDirPath) {
        ResourcePack pack;
        try {
            pack = new ZipResourcePack(getResourcePackFile(tempDirPath));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return pack;
    }

    private static InputStream getTestResourcePackStream() {
        return ZipResourcePackTests.class.getResourceAsStream("/resourcepack.zip");
    }

    private static File getResourcePackFile(Path tempDirPath) {
        File tempResourcePackFile = tempDirPath.resolve("pack.zip").toFile();
        if (!tempResourcePackFile.exists()) {
            try (OutputStream outputStream = new FileOutputStream(tempResourcePackFile); InputStream inputStream = getTestResourcePackStream()) {
                IOUtils.write(IOUtils.toByteArray(inputStream), outputStream);
            } catch (IOException exception) {
                throw new AssertionError(exception);
            }
        }
        return tempResourcePackFile;
    }

    private static File getResourceLargePackFile(Path tempDirPath) {
        File tempResourcePackFile = tempDirPath.resolve("large_pack.zip").toFile();
        if (!tempResourcePackFile.exists()) {
            int fileSize = 1 << (10 + 10 + 6); // 67MiB

            try (OutputStream outputStream = new FileOutputStream(tempResourcePackFile); InputStream inputStream = new InputStream() {
                int counter = 0;

                @Override
                public int read() throws IOException {
                    if (this.counter >= fileSize) {
                        return -1;
                    }
                    return counter++;
                }
            }) {
                IOUtils.write(IOUtils.toByteArray(inputStream), outputStream);
            } catch (IOException exception) {
                throw new AssertionError(exception);
            }
        }
        return tempResourcePackFile;
    }

}
