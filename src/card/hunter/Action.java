package card.hunter;

public class Action {
    public final String text,flavorText,fullText,iconLeft,iconRight;
    public final byte amountLeft,amountRight;
    
    public Action(Type type,String text, String flavorText, byte damage, byte range, byte movement){
        this.text=text.trim();
        this.flavorText=flavorText.trim();
        this.fullText=this.text + (this.text.length() > 0 ? "<br /><br />" : "") + "<i>" + this.flavorText + "</i>";
        
        this.amountRight = range;
        this.iconRight = (range != 0) ? "RangedArrow" : "";
        
        switch (type) {
            case Attack:
                this.iconLeft = "Sword";
                this.amountLeft = damage;
                break;
            case Move:
                this.iconLeft = "Boot";
                this.amountLeft = movement;
                break;
            default: 
                this.iconLeft = (movement > 0) ? "Boot" : "";
                this.amountLeft = (movement > 0) ? movement : 0;
                break;
        }
    }    
}
