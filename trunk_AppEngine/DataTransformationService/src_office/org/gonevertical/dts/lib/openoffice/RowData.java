package org.gonevertical.dts.lib.openoffice;

public class RowData {

  public String[] cell = null;
  
  public RowData() {
  }
  
  public String[] getCells() {
  	return cell;
  }

	public void setCells(String[] cell) {
	  this.cell = cell;
  }

	/**
	 * add to the front of the cell array
	 * 
	 * @param value
	 */
	public void appendToCells(String value) {
	 
		String[] ca = new String[cell.length + 1];
		ca[0] = value;
		
		for (int i=0; i < cell.length; i++) {
			ca[i+1] = cell[i];
		}
		
		cell = ca;
  }

		
}
