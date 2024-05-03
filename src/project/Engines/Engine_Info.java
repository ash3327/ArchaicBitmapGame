package project.Engines;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class Engine_Info extends JOptionPane{//Not instance of Engine, but extention of its functionality
	
	public static int showMessageDialog(String title, Object[] contents, boolean confirm){
		int page = 0;
		int selected;
		do{
			Object[] content = (Object[]) contents[page];
			Object[] selection = {"<",
					confirm ? "[ Page "+(page+1)+" / "+contents.length+" ]"
							: new JLabel("[ Page "+(page+1)+" / "+contents.length+" ]"),
					">"};
			selected = showOptionDialog(null, content, title, 0, -1, null, 
					selection, null);
			switch(selected){
			case 0: if(page > 0) page--; break;
			case 2: if(page < contents.length - 1) page++; break;
			}
		} while (selected != -1 && selected != 1);
		return (selected == 1) ? page : -1;
	}
}
