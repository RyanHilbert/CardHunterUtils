package card.hunter.assets;

import card.hunter.Attack;
import card.hunter.Damage;
import card.hunter.Quality;
import card.hunter.Rarity;
import card.hunter.Set;
import card.hunter.Slot;
import card.hunter.Token;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.nio.file.Files;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.RandomAccess;
import java.util.TreeMap;

public enum Data{
	
	Adventures,
	Cards,
	CharacterArchetypes,
	Equipment,
	ExperienceTables,
	Figures,
	HealthTables,
	Hints,
	Keywords,
	MPRewards,
	Prices,
	Prompts,
	StarterPacks,
	TalentTables;
	
	private Reference<Data.Table>reference=new WeakReference<>(null);
	
	public Table load(){
		final Table t=reference.get();
		if(t!=null)return t;
		
		final String name=toString();
		final String path="data/gameplay/";
		final File dir=new File(path+name);
		final File file=new File(dir,name+".csv");
		try{
			if(!file.isFile()){
				dir.mkdirs();
				Files.copy(new URL(path+name+'/'+name+".csv").openStream(),file.toPath());
			}
			final Table table=new Table(new FileReader(new File(dir,name+".csv")));
			reference=new WeakReference<>(table);
			return table;
		}catch(IOException e){
			throw new UncheckedIOException("Failed to load data: "+name+".csv",e);
		}
	}
	public class Table extends AbstractList<Table.Row>implements RandomAccess{
	
		private final Map<String,Integer>columnMap=new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		private final String[]columns;
		private final String[][]data;
		
		@Override public Row get(final int index){
			return new Row(index);
		}
		@Override public int size(){
			return data.length;
		}
		public Table(final Reader r){
			String[]columns;
			String[][]data;
			try(final LineNumberReader reader=new LineNumberReader(r)){
				reader.readLine();//skip the first line
				columns=reader.readLine().split(",");
				reader.setLineNumber(0);
				reader.mark(Integer.MAX_VALUE);
				reader.skip(Long.MAX_VALUE);
				data=new String[reader.getLineNumber()][columns.length];
				reader.reset();
				reader.setLineNumber(-1);
				for(String line=reader.readLine();reader.ready();line=reader.readLine()){
					boolean quoted=false;
					int number=reader.getLineNumber();
					data[number][columns.length-1]="";
					for(int i=0,start=0,index=0;i<line.length();++i){
						final char c=line.charAt(i);
						if(quoted){
							if(c=='"'){
								final int next=i+1;
								if(next<line.length()&&line.charAt(next)=='"')++i;
								else quoted=false;
							}
						}else{
							if(c==','){
								data[number][index++]=line.substring(start,i).replace("\"\"","\0").replace("\"","").replace('\0','"');
								start=i+1;
							}else if(c=='"')quoted=true;
						}
					}
				}
			}catch(IOException e){
				throw new UncheckedIOException(e);
			}
			this.columns=columns;
			this.data=data;
		}
		public class Row extends AbstractMap<String,String>{
			
			private final String[]row;

			Row(final int index){
				row=data[index];
			}
			public int getInt(Object object){
				try{
					return Integer.parseInt(get(object));
				}catch(RuntimeException e){
					return 0;
				}
			}
			@Override public String get(Object object){
				final Integer index=columnMap.get(object);
				return index==null?null:row[index];
			}
			@Override public AbstractSet<Entry<String,String>>entrySet(){
				return new AbstractSet<Entry<String,String>>(){
					@Override public Iterator<Entry<String,String>>iterator(){
						return new Iterator<Entry<String,String>>(){
							private int index=0;
							@Override public boolean hasNext(){
								return index<columns.length;
							}
							@Override public SimpleImmutableEntry<String,String>next(){
								return new SimpleImmutableEntry(columns[index],row[index++]);
							}
						};
					}
					@Override public int size(){
						return Table.this.columnMap.size();
					}
				};
			}
			public Attack getAttackType(){
				try{
					return Attack.valueOf(row[columnMap.get("Attack Type")]);
				}catch(RuntimeException e){
					return null;
				}
			}
			public Damage getDamageType(){
				try{
					return Damage.valueOf(row[columnMap.get("Damage Type")]);
				}catch(RuntimeException e){
					return null;
				}
			}
			public Set getSet(){
				try{
					return Set.valueOf(Integer.parseUnsignedInt(row[columnMap.get("Set")]));
				}catch(RuntimeException e){
					return null;
				}
			}
			public Rarity getRarity(){
				try{
					return Rarity.valueOf(row[columnMap.get("Rarity")]);
				}catch(RuntimeException e){
					return null;
				}
			}
			public Quality getQuality(){
				try{
					return Quality.valueOf(row[columnMap.get("Quality")],row[columnMap.get("Plus Minus")]);
				}catch(RuntimeException e){
					return null;
				}
			}
			public Slot getSlot(){
				try{
					return Slot.fromString(row[columnMap.get("Slot")]);
				}catch(RuntimeException e){
					return null;
				}
			}
			public Token getToken1(){
				try{
					return Token.valueOf(getInt("Talent 1"));
				}catch(RuntimeException e){
					return null;
				}
			}
			public Token getToken2(){
				try{
					return Token.valueOf(getInt("Talent 2"));
				}catch(RuntimeException e){
					return null;
				}
			}
		}
	}
}
