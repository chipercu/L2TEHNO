package l2open.common.Html_Constructor.tags;

public class Img implements Build{

    private String src = "icon.NOICON";
    private int width = 32;
    private int height = 32;

    public Img(String src){
        this.src = src;
    }

    public Img(String src, int width, int height){
        this.src = src;
        this.width = width;
        this.height = height;
    }

    @Override
    public String build() {
        return "<img src=\"" + src +"\"" + " width=" + width + " height=" + height + ">";
    }
}
