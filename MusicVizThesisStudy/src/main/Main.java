package main;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;

public class Main 
{
	public static void main(String [] args) throws IOException
	{
		ArrayList<File> listOfSongs;
		
		for( int i = 0; i < 31; i++ )
		{
			String copyDir = "Subject_" + (i+20);
			File file = new File(copyDir);
			file.mkdirs();
			
			listOfSongs = getRandomSongs();
			System.out.println("Subject_" + (i+20) + "=====================");
			for( File song: listOfSongs )
			{
				FileUtils.copyFileToDirectory(song, file, true);
				System.out.println(song.getName());
			}
			
		}
	}
	
	public static ArrayList<File> getRandomSongs()
	{
		final String [] genres = {"Classical", "Country","Jazz","Pop","Rap","Rock"};
		ArrayList<File> listOfChosenSongs = new ArrayList<File>();
		HashMap<String, File> directories = new HashMap<String, File>();
		
		directories.put(genres[0], new File("C:\\Users\\clt\\Desktop\\Study Midis\\ClassicalAndInstrumental"));
		directories.put(genres[1], new File("C:\\Users\\clt\\Desktop\\Study Midis\\Country"));
		directories.put(genres[2], new File("C:\\Users\\clt\\Desktop\\Study Midis\\Jazz"));
		directories.put(genres[3], new File("C:\\Users\\clt\\Desktop\\Study Midis\\Pop"));
		directories.put(genres[4], new File("C:\\Users\\clt\\Desktop\\Study Midis\\RapR&BHipHopReggae"));
		directories.put(genres[5], new File("C:\\Users\\clt\\Desktop\\Study Midis\\Rock"));
		
		SecureRandom randomGenre = new SecureRandom();
		randomGenre.generateSeed(2000);
		SecureRandom randomSong = new SecureRandom();
		randomSong.generateSeed(2000);
		
		HashSet<Integer> usedGenres = new HashSet<Integer>();
		
		for( int i = 0; i < genres.length; i++ )
		{
			//Randomly Choose a Genre and save it
			int chosenGenre = randomGenre.nextInt(genres.length);
			while( usedGenres.contains(chosenGenre) )
			{
				chosenGenre = randomGenre.nextInt(genres.length);
			}
			usedGenres.add(chosenGenre);
			
			//Using the chosen genre, get the list of songs for that genre
			File[] listOfSongsForGenre = directories.get( genres[chosenGenre] ).listFiles();
			
			///Choose a random song from that genre
			int chosenSong = randomSong.nextInt( listOfSongsForGenre.length );
						
			listOfChosenSongs.add( listOfSongsForGenre[chosenSong] );
		}
		
		return listOfChosenSongs;
	}
}
