package boa.datagen.forges.github;

import java.io.File;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import boa.datagen.util.FileIO;


public class GetReposByLanguage {
	
	public static void main(String[] args) {
		String[] languages = {"java", "javascript", "php"};
		TokenList tokens = new TokenList(args[0]);
		Thread[] workers = new Thread[languages.length];
		for (int i =0; i < languages.length; i++) {
			workers[i] = new Thread(new Worker(i,languages[i],args[1], tokens));
			workers[i].start();
		}
		for (Thread thread : workers)
			while (thread.isAlive())
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	}
	
	
	public static class Worker implements Runnable {
		private final int id; 
		private final String language;
		private TokenList tokens;
		private final String outDir;
		private JsonArray repos = new JsonArray();
		private final int RECORDS_PER_FILE = 100;
		private int counter = 0;
		
		public Worker(int id, String language, String outDir, TokenList tokenList) {
			this.id = id;
			this.language = language;
			this.outDir = outDir;
			this.tokens = tokenList;
		}
		
		@Override
		public void run() {
	//		HashSet<String> names = new HashSet<>();
			String time = "2018-08-18T01:01:01Z";
			Gson parser = new Gson();
			
			while (true){
				String pushedName = "";
				Token tok = this.tokens.getNextAuthenticToken("https://api.github.com/repositories");
				String url = "https://api.github.com/search/repositories?q=language:" + language +"+stars:>1+pushed:<=" + time + "&sort=updated&order=desc&per_page=100";
				System.out.println(url);
				MetadataCacher mc = new MetadataCacher(url, tok.getUserName(), tok.getToken());
				mc.authenticate();
				while (!mc.isAuthenticated() || mc.getNumberOfRemainingLimit() <= 0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					mc = new MetadataCacher(url, tok.getUserName(), tok.getToken());
					mc.authenticate();
				}
				mc.getResponseJson();
				String content = mc.getContent();
				
				JsonObject json = null;
				json = parser.fromJson(content, JsonElement.class).getAsJsonObject();
		        JsonArray items = json.getAsJsonArray("items");
		        if (items.size() > 0) {
			        for (int j = 0; j < items.size(); j++) {
			        	JsonObject item = items.get(j).getAsJsonObject();
			        	this.addRepo(item);
			        	String name = item.get("full_name").getAsString();
			        //	names.add(name);
			        	String pushed = item.get("pushed_at").getAsString();
			        	if (pushed.compareTo(time) < 0){
			        		time = pushed;
			        	}
			        }
		        }
		        int count = json.get("total_count").getAsInt();
		        if (count == items.size())
		        	break;
				if (tok.getNumberOfRemainingLimit() <= 1) {
					long t = mc.getLimitResetTime() * 1000 - System.currentTimeMillis();
					if (t >= 0) {
						System.out.println("Waiting " + (t/1000) + " seconds for sending more requests.");
						try {
							Thread.sleep(t);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			writeRemainingRepos();
		//	StringBuilder sb = new StringBuilder();
	    //	for (String name : names)
	    //		sb.append(name + "\n");
	    //    FileIO.writeFileContents(new File( outDir + "names/names.txt"), sb.toString());
		} 
		
		private void addRepo(JsonObject repo) {
			File fileToWriteJson = null;
			this.repos.add(repo);
			if (this.repos.size() % RECORDS_PER_FILE == 0) {
				fileToWriteJson = new File(
						outDir + "/Thread-" + this.id + "-page-" + counter + ".json");
				while (fileToWriteJson.exists()) {
					System.out.println("file scala/thread-" + this.id + "-page-" + counter
							+ " arleady exist");
					counter++;
					fileToWriteJson = new File(
							outDir + "/Thread-" + this.id + "-page-" + counter + ".json");
				}
				FileIO.writeFileContents(fileToWriteJson, this.repos.toString());
				System.out.println(Thread.currentThread().getId() + " " + counter++);
				this.repos = new JsonArray();
			}
		}

		public void writeRemainingRepos() {
			File fileToWriteJson = null;
			if (this.repos.size() > 0) {
				fileToWriteJson = new File(outDir + "/Thread-" + this.id + "-page-" + counter + ".json");
				while (fileToWriteJson.exists()) {
					counter++;
					fileToWriteJson = new File(outDir + "/Thread-" + this.id + "-page-" + counter + ".json");
				}
				FileIO.writeFileContents(fileToWriteJson, this.repos.toString());
				System.out.println(this.id  + counter++);
			}
		}
	}
}
