package earth.terrarium.olympus.client.components.textbox.multiline;

record MultilineStringView(
        int line,
        int start, int end
) {
    static final MultilineStringView EMPTY = new MultilineStringView(0, 0, 0);

    public boolean contains(int i) {
        return i >= this.start && i <= this.end;
    }

    public String substring(String text) {
		return text.substring(this.start, this.end);
	}
}