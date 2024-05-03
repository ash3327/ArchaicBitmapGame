package project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import project.Engines.Engine_MapCreate;

public class ButtonListener implements ActionListener{
	String mode = "NULL";
	public ButtonListener(){}
	public ButtonListener(Engine_MapCreate eng){
		mode = "CREATE";
	}

	public void actionPerformed(ActionEvent ae) {
		System.out.println(ae.getActionCommand());
		switch(mode){
		case "CREATE":
			break;
		}
	}
	
}
