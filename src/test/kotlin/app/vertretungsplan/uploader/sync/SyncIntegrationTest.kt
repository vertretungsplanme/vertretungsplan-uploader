package app.vertretungsplan.uploader.sync

import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockftpserver.fake.FakeFtpServer
import org.mockftpserver.fake.UserAccount
import org.mockftpserver.fake.filesystem.DirectoryEntry
import org.mockftpserver.fake.filesystem.FileEntry
import org.mockftpserver.fake.filesystem.WindowsFakeFileSystem
import java.io.File


class SyncIntegrationTest {
    val server = FakeFtpServer()
    val tempDirectory = createTempDir()
    private val baseDir = "c:\\data"

    @Before
    fun setUp() {
        server.addUserAccount(UserAccount("user", "password", baseDir))
        server.serverControlPort = 0  // automatically select free port
        val fileSystem = WindowsFakeFileSystem()
        fileSystem.add(DirectoryEntry(baseDir))
        server.fileSystem = fileSystem

        server.start()
    }

    @Test
    fun syncTest() {
        File(tempDirectory, "testFile.txt").writeText("Lorem ipsum")
        Sync(tempDirectory.absolutePath, "ftp://user:password@127.0.0.1:${server.serverControlPort}").run()

        assert(server.fileSystem.isFile(baseDir + "\\testFile.txt"))
        val file = server.fileSystem.getEntry(baseDir + "\\testFile.txt") as FileEntry
        val contents = file.createInputStream().bufferedReader().readText()
        assertEquals("Lorem ipsum", contents)
    }

    @After
    fun tearDown() {
        server.stop()
    }
}