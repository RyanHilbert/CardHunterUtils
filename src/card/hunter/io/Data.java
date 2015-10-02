package card.hunter.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
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
	
	private final Table nullTable=new Table();
	private Table cache=nullTable;
	
	public static void loadAll(){
		for(Data data:Data.values())data.load();
	}
	public static void unloadAll(){
		for(Data data:Data.values())data.unload();
	}
	public Table unload(){
		final Table table=cache;
		cache=nullTable;
		return table;
	}
	public Table load(){
		final Table t=cache;
		if(t!=nullTable)return t;
		try{synchronized(cache){//double-checked-locking ensures thread-safety
			final Table syncheck=cache;
			if(syncheck!=nullTable)return syncheck;
			final String name=toString();
			final String path="data/gameplay/";
			final File dir=new File(path+name);
			final File file=new File(dir,name+".csv");
			if(!file.isFile()){
				dir.mkdirs();
				Files.copy(new URL("http","live.cardhunter.com",'/'+path+name+'/'+name+".csv").openStream(),file.toPath());
			}
			return cache=new Table(new File(dir,name+".csv"));
		}}catch(IOException e){
			throw new UncheckedIOException(name(),e);
		}
	}
	public class Table extends AbstractList<Table.Row>implements RandomAccess{
	
		private final Map<String,Integer>columnMap=new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		private final String[]columnArray;
		private final String[][]data;
		
		public final List<String>columns;
		
		@Override public Row get(final int index){
			return new Row(index);
		}
		@Override public int size(){
			return data.length;
		}
		Table(){
			columnArray=null;
			data=null;
			columns=null;
		}
		Table(File file){//not meant for files >2gb or that don't fit into JVM memory
			try(final LineNumberReader reader=new LineNumberReader(new FileReader(file))){
				reader.readLine();//skip the first line
				columnArray=reader.readLine().split(",");
				columns=Collections.unmodifiableList(Arrays.asList(columnArray));
				for(int i=0;i<columnArray.length;++i)columnMap.put(columnArray[i],i);
				reader.setLineNumber(0);
				final int length=(int)file.length();
				reader.mark(length);
				reader.skip(length);
				data=new String[reader.getLineNumber()][columnArray.length];
				reader.reset();
				for(int lineNumber=0;reader.ready();++lineNumber){
					final String line=reader.readLine();
					boolean quoted=false;
					for(int i=0,start=0,index=0;index<columnArray.length;++i){
						if(i+1==line.length()){
							data[lineNumber][index]=line.substring(start).replace("\"\"","\0").replace("\"","").replace('\0','"');
							break;
						}
						final char c=line.charAt(i);
						if(quoted){
							if(c=='"'){
								if(line.charAt(i+1)=='"')++i;
								else quoted=false;
							}
						}else if(c==','){
							data[lineNumber][index++]=line.substring(start,i).replace("\"\"","\0").replace("\"","").replace('\0','"');
							start=i+1;
						}else if(c=='"')quoted=true;
					}
				}
			}catch(IOException e){
				throw new UncheckedIOException(e);
			}
		}
		public class Row extends AbstractMap<String,String>{
			
			private final String[]row;

			Row(final int index){
				row=data[index];
			}
			public Table getTable(){
				return Table.this;
			}
			public<E extends Enum<E>>E getEnum(E e){
				return getEnum(e,e.getDeclaringClass().getSimpleName().replace('_',' '));
			}
			public<E extends Enum<E>>E getEnum(E e,String string){//throws NPE if not found or IllegalArgumentException if cannot be read as E
				string=getString(string);
				if(string.isEmpty())return e;
				try{
					final int index=Integer.parseInt(string);
					if(index<0)return e;
					return e.getDeclaringClass().getEnumConstants()[index];
				}catch(NumberFormatException ex){
					return Enum.valueOf(e.getDeclaringClass(),string.replace(' ','_'));
				}
			}
			public<E extends Enum<E>>EnumSet<E>getEnums(Class<E>e){
				return getEnums(e,e.getSimpleName()+'s');
			}
			public<E extends Enum<E>>EnumSet<E>getEnums(Class<E>e,String string){
				string=getString(string);
				final EnumSet<E>set=EnumSet.noneOf(e);
				for(String token:string.split(","))set.add(Enum.valueOf(e,token.replace(' ','_')));
				return set;
			}
			public byte getByte(String string){
				string=getString(string);
				if(string.isEmpty()||string.charAt(0)=='D')return-1;//'D' hack needed for strange trigger value of Lightning Breath card
				byte result=Byte.parseByte(string);
				return result<0?(byte)-result:result;
			}
			public int getInt(String string){//throws NullPointerException if not found or NumberFormatException if cannot be read as int
				return Integer.parseInt(getString(string));
			}
			public String getString(String string){//same as get(), but throws NullPointerException instead of returning null
				return row[columnMap.get(string)];
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
								return index<columnArray.length;
							}
							@Override public SimpleImmutableEntry<String,String>next(){
								return new SimpleImmutableEntry(columnArray[index],row[index++]);
							}
						};
					}
					@Override public int size(){
						return Table.this.columnMap.size();
					}
				};
			}
		}
	}
}
