package card.hunter;

import javafx.scene.paint.Color;

public enum Quality{
	Black_,Black,Black$,
	Paper_,Paper,Paper$,
	Bronze_,Bronze,Bronze$,
	Silver_,Silver,Silver$,
	Gold_,Gold,Gold$,
	Emerald_,Emerald,Emerald$,
	Amethyst_,Amethyst,Amethyst$;
	
	private static final Quality[]values=values();
	
	public static Quality valueOf(int i){
		return values[i+4];
	}
	public static Quality valueOf(String quality,char modifier){
		final int value;
		switch(quality){
			case"E":case"Black":value=-3;break;
			case"D":case"Paper":value=0;break;
			case"C":case"Bronze":value=3;break;
			case"B":case"Silver":value=6;break;
			case"A":case"Gold":value=9;break;
			case"AA":case"Emerald":value=12;break;
			case"AAA":case"Amethyst":value=15;break;
			default:return valueOf(quality);
		}
		switch(modifier){
			case'-':case'_':return valueOf(value-1);
			case'+':case'$':return valueOf(value+1);
			default:return valueOf(value);
		}
	}
	public static Quality valueOf(String quality,String modifier){
		return valueOf(quality,modifier.isEmpty()?'\0':modifier.charAt(0));
	}
	@Override public String toString(){
		return super.toString().replace('_','-').replace('$','+');
	}
	public Quality fromString(String string){
		return valueOf(string.replace('-','_').replace('+','$'));
	}
	public int value(){
		return ordinal()-4;
	}
	public String quality(){
		final String string=super.toString();
		final int index=string.length()-1;
		final char end=string.charAt(index);
		if(end!='_'&end!='$')return string;
		else return string.substring(0,index);
	}
	public char modifier(){
		final String string=super.toString();
		switch(string.charAt(string.length()-1)){
			case'_':return'-';
			case'$':return'+';
			default:return'\0';
		}
	}
	public Color color(){
		switch(ordinal()/3){
			case 0:return Color.BLACK;
			case 1:return Color.TAN;
			case 2:return Color.SIENNA;
			case 3:return Color.SILVER;
			case 4:return Color.GOLD;
			case 5:return Color.CHARTREUSE;
			case 6:return Color.VIOLET;
			default:return Color.TRANSPARENT;
		}
	}
}
