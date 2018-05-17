package lab.wasikrafal.lab7;

import android.content.Context;

public class MarkerInfoHolder
{
    private int title;
    private int text;
    private int image;

    public MarkerInfoHolder (int title, int text, int image)
    {
        this.title = title;
        this.text = text;
        this.image = image;
    }

    public String getTitle(Context context)
    {
        return context.getString(title);
    }

    public String getText(Context context)
    {
        return context.getString(text);
    }

    public int getImage()
    {
        return image;
    }
}
