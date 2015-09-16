package card.hunter;

public interface ViewableEnum<E extends Enum<E>&ViewableEnum<E>>extends Viewable{
	@Override default EnumView<E>getView(){
		return new EnumView((E)this);
	}
}