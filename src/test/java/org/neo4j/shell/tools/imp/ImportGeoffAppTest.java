package org.neo4j.shell.tools.imp;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.shell.ShellException;
import org.neo4j.shell.impl.CollectingOutput;
import org.neo4j.shell.impl.SameJvmClient;
import org.neo4j.shell.kernel.GraphDatabaseShellServer;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.io.*;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Created by mh on 04.07.13.
 */
public class ImportGeoffAppTest {

    private GraphDatabaseAPI db;
    private SameJvmClient client;

    @Test
    public void testRunWithInputFile() throws Exception {
        createFile("in.geoff", "(A) {\"name\": \"foo\"}");
        assertCommand("import-geoff -g in.geoff", "Geoff import of in.geoff created 1 entities.");
        assertEquals("foo",db.getNodeById(1).getProperty("name"));
    }
    @Test
    public void testRunWithMultiLineInputFile() throws Exception {
        createFile("in.geoff",
                "(A) {\"name\": \"Alice\"}",
                "(B) {\"name\": \"Bob\"}",
                "(A)-[r:KNOWS]->(B)");
        assertCommand("import-geoff -g in.geoff", "Geoff import of in.geoff created 3 entities.");
        assertEquals("Alice",db.getNodeById(1).getProperty("name"));
        assertEquals("Bob",db.getNodeById(2).getProperty("name"));
        assertEquals("KNOWS",db.getRelationshipById(0).getType().name());
    }

    private void createFile(String fileName, String...rows) throws IOException {
        PrintWriter inFile = new PrintWriter(new FileWriter(fileName));
        for (String row : rows) {
            inFile.println(row);
        }
        inFile.close();
    }

    private void assertCommand(String command, String expected) throws RemoteException, ShellException {
        CollectingOutput out = new CollectingOutput();
        client.evaluate(command, out);
        assertEquals(expected,out.asString().trim());
    }

    @Before
    public void setUp() throws RemoteException {
        db = (GraphDatabaseAPI) new TestGraphDatabaseFactory().newImpermanentDatabase();
        client = new SameJvmClient(Collections.<String, Serializable>emptyMap(), new GraphDatabaseShellServer(db));
    }
}
