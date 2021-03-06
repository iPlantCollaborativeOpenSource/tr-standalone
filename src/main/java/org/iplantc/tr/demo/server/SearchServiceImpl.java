package org.iplantc.tr.demo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.iplantc.tr.demo.client.services.SearchService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SearchServiceImpl extends RemoteServiceServlet implements SearchService
{
	private static String HOSTNAME = "http://votan.iplantcollaborative.org/";

	private HttpURLConnection getUrlConnection(String address) throws IOException
	{
		URL url = new URL(address);

		return (HttpURLConnection)url.openConnection();
	}

	private URLConnection update(String address, String body) throws IOException
	{
		// make post mode connection
		HttpURLConnection urlc = getUrlConnection(address);
		urlc.setRequestMethod("POST");
		urlc.setDoOutput(true);

		// send post
		OutputStreamWriter outRemote = null;
		try
		{
			outRemote = new OutputStreamWriter(urlc.getOutputStream());
			outRemote.write(body);
			outRemote.flush();
		}
		finally
		{
			if(outRemote != null)
			{
				outRemote.close();
			}
		}

		return urlc;
	}

	private String retrieveResult(URLConnection urlc) throws UnsupportedEncodingException, IOException
	{
		StringBuffer buffer = new StringBuffer();
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));

			while(true)
			{
				int ch = br.read();

				if(ch < 0)
				{
					break;
				}

				buffer.append((char)ch);
			}
		}
		finally
		{
			if(br != null)
			{
				br.close();
			}
		}

		return buffer.toString();
	}

	/**
	 * Sends an HTTP GET request to another service.
	 * 
	 * @param address the address to connect to.
	 * @return the URL connection used to send the request.
	 * @throws IOException if an error occurs.
	 */
	private URLConnection get(String address) throws IOException
	{
		// make post mode connection
		URLConnection urlc = getUrlConnection(address);
		urlc.setDoOutput(true);

		return urlc;
	}

	// perform BLAST search
	@Override
	public String doBLASTSearch(String json) throws IllegalArgumentException
	{
		String ret = "";

		try
		{
			URLConnection connection = update(HOSTNAME + "treereconciliation/search/blast-search", json);

			ret = retrieveResult(connection);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Search failed.", e);
		}

		return ret;
	}

	// search by gene id
	@Override
	public String doGeneIdSearch(String term) throws IllegalArgumentException
	{
		String ret = "";

		try
		{
			URLConnection connection =
					get(HOSTNAME + "treereconciliation/search/gene-id-search/" + term);

			ret = retrieveResult(connection);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Search failed.", e);
		}

		return ret;
	}

	// search by GO accession
	@Override
	public String doGoAccessionSearch(String term) throws IllegalArgumentException
	{
		String ret = "";

		try
		{
			URLConnection connection =
					update(HOSTNAME + "treereconciliation/search/go-accession-search/", term);

			ret = retrieveResult(connection);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Search failed.", e);
		}

		return ret;
	}

	// search by GO term
	@Override
	public String doGoTermSearch(String term) throws IllegalArgumentException
	{
		String ret = "";

		try
		{
			URLConnection connection = update(HOSTNAME + "treereconciliation/search/go-search/", term);

			ret = retrieveResult(connection);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Search failed.", e);
		}

		return ret;
	}

	// retrieve details of a gene family
	@Override
	public String getDetails(String idGeneFamily) throws IllegalArgumentException
	{
		String ret = "";

		try
		{
			URLConnection connection =
					get(HOSTNAME + "treereconciliation/get/gene-family-details/" + idGeneFamily);

			ret = retrieveResult(connection);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Search failed.", e);
		}

		return ret;
	}

	// retrieve summary of a gene family
	@Override
	public String getSummary(String idGeneFamily) throws IllegalArgumentException
	{
		String ret = "";

		try
		{
			URLConnection connection =
					get(HOSTNAME + "treereconciliation/get/gene-family-summary/" + idGeneFamily);

			ret = retrieveResult(connection);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Search failed.", e);
		}

		return ret;
	}

	// retrieve a GO term cloud as HTML
	public String getGoCloud(String idGeneFamily) throws IllegalArgumentException
	{
		String ret = "";

		try
		{
			URLConnection connection =
					get(HOSTNAME + "treereconciliation/get/go-cloud/" + idGeneFamily);

			ret = retrieveResult(connection);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Search failed.", e);
		}

		return ret;
	}

	/** Performs a duplication search for a given node ID. Assumes the edge was clicked on. */
	public String doDuplicationSearch(String idNode) throws IllegalArgumentException
	{
		String ret = "";

		String json = "{\"nodeId\":" + idNode + ", \"edgeSelected\":true}";
		
		try
		{
			URLConnection connection =
					update(HOSTNAME + "treereconciliation/search/duplication-search/", json);

			ret = retrieveResult(connection);
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException("Search failed.", e);
		}

		return ret;
	}
}
