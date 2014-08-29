package gamesincommon;

enum FilterType {

  multiplayer("Multi-player"), coop("Co-op"), localcoop("Local Co-op");

  private String value;

  FilterType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return this.getValue();
  }

  public static FilterType getEnum(String value) {
    
    if (value == null)
      throw new IllegalArgumentException();
    
    for (FilterType v : values())
      if (value.equalsIgnoreCase(v.getValue()))
        return v;
    
    throw new IllegalArgumentException();
    
  }
}
