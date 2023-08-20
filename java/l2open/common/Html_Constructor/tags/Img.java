package l2open.common.Html_Constructor.tags;

public class Img implements Build{

    private String src = "icon.NOICON";
    private int width = 32;
    private int height = 32;

    private String imgHTML = "";

//    <img src="L2UI_CT1.DeBuffFrame_24" width=8 height="16">

    public Img(String src, int width, int height){
        this.src = src;
        this.width = width;
        this.height = height;
    }

    public String getSrc() {
        return src;
    }

    public Img setSrc(String src) {
        this.src = src;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public Img setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public Img setHeight(int height) {
        this.height = height;
        return this;
    }

    @Override
    public String build() {
        return "<img src=\"" + src +"\"" + " width=" + width + " height=" + height + ">";
    }
}
