package fr.aupert.jit.util;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;

@DisplayNameGeneration(value = ReplaceUnderscores.class)
class DeflateTest {

    private static String COMMIT_CONTENT = "commit 193\u0000tree 88e38705fdbd3608cddbe904b67c731f3234c45b\n"
            + "author Fabrice Aupert <fabrice.aupert@agfa.com> 1555003710 +0200\n"
            + "committer Fabrice Aupert <fabrice.aupert@agfa.com> 1555003710 +0200\n"
            + "\n"
            + "First commit"
            + "\n";

    @Test
    void can_read_commit_objects() {
        InputStream is = readObject("26", "bec462128479223c12fbdc3e63fb84ce929722");
        String commit = new Deflate().decompress(is);
        assertNotNull(commit);
        assertEquals(COMMIT_CONTENT, commit);
    }

    @Test
    void can_create_commit_object() {
        byte[] commitObject = new Deflate().compress(COMMIT_CONTENT);

        // bytes of compressed objects by the Deflate class are not equals to those generated by Git
        // (deflate compression algorithm diff between Java & Git/zlib?) so we have to check
        // uncompressed data match
        InputStream is = readObject("26", "bec462128479223c12fbdc3e63fb84ce929722");
        String expected = new Deflate().decompress(is);

        String commitString = new Deflate().decompressToString(commitObject);

        assertEquals(expected, commitString);
    }

    private InputStream readObject(String dir, String filename) {
        String path = "/git-simple-commit/gitdir/objects/" + dir + "/" + filename;
        return this.getClass().getResourceAsStream(path);
    }

}
