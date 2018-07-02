package alg.lucene;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;

public class Indexer 
{
	public Indexer(String sourceFolder)
	{
		File dataDir = new File(sourceFolder);
		File indexDir = new File(sourceFolder + "_index");
		
		try
		{
			index(indexDir, dataDir);
		}
		catch (IOException e)
		{
			System.out.println("There was an error while creating the index.");
		}
	}
	
	public static void index(File indexDir, File dataDir) throws IOException
	{
		if (!dataDir.exists() || !dataDir.isDirectory())
		{
		    throw new IOException(dataDir + " does not exist or is not a directory");
		}
		IndexWriter writer = new IndexWriter(indexDir, new StemAnalyzer(), true);
		indexDirectory(writer, dataDir);
		writer.close();
	}
	
	private static void indexDirectory(IndexWriter writer, File dir) throws IOException
	{
		File[] files = dir.listFiles();
		
		for (int i=0; i < files.length; i++)
		{
			File f = files[i];
			if (f.isDirectory())
			{
				indexDirectory(writer, f);  // recurse
			} 
			else if (f.getName().endsWith(".txt"))
			{
				indexFile(writer, f);
			}
		}
	}
	
	private static void indexFile(IndexWriter writer, File f) throws IOException
	{
		Document doc = new Document();
		doc.add(Field.Text("contents", new FileReader(f)));
		doc.add(Field.UnIndexed("filename", f.getCanonicalPath()));
		writer.addDocument(doc);
	}
}

